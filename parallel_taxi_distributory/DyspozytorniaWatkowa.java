import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class DyspozytorniaWatkowa implements Dyspozytornia{
    // musi byc static, bo inaczej globalnego ID, nie dostane
    private final AtomicInteger idZlecenia = new AtomicInteger(0);

    private final ConcurrentHashMap<Integer, Boolean> idToState;

    // Kolejka zlecen i taxowek
    private final ConcurrentLinkedDeque<Integer> kolejkaZlecen;
    private final ConcurrentLinkedDeque<Taxi> kolejkaTaxi;

    // ThreadDispatcher
    private ExecutorService executor;
    private final AtomicInteger numberOfAvaibleTaxis;

    // Glowny watek aktywujacy watki z executora z Dystro
    Thread dyspozytorniaDeamon;

    // Komunikacja watek glowny a deamon Dyspozytorni
    ReentrantLock blokadaOperacyjna;
    Condition wakeUp;
    volatile boolean konczymyPrace;




    DyspozytorniaWatkowa() {
        this.idToState = new ConcurrentHashMap<>();

        this.kolejkaZlecen = new ConcurrentLinkedDeque<>();
        this.kolejkaTaxi = new ConcurrentLinkedDeque<>();

        // bez inicjalizacji executora to potem
        this.numberOfAvaibleTaxis = new AtomicInteger(0);

        // UWAGA watek nie jest tutaj i nie mqa byc zaczynany
        this.dyspozytorniaDeamon = new Thread(this::dyspozytorniaDeamonHandler);
        this.dyspozytorniaDeamon.setDaemon(true);

        // Inicjalizacja blokady
        this.blokadaOperacyjna = new ReentrantLock();
        this.wakeUp = blokadaOperacyjna.newCondition();
        this.konczymyPrace = false;
    }



    private void dyspozytorniaDeamonHandler() {
        while (!konczymyPrace) {
            //System.out.println("Wykonuje sie bez sensu");
            this.blokadaOperacyjna.lock();
            try {
                // jesli brak wolnej taxy i zlecenie to spij
                if(this.kolejkaZlecen.isEmpty() || this.kolejkaTaxi.isEmpty() || this.numberOfAvaibleTaxis.get() <= 0) {
                    if(this.konczymyPrace) {
                        return;
                    }
                    try {
                        wakeUp.await();
                    } catch (InterruptedException e) {
                        // opcjonalnie: Thread.currentThread().interrupt();
                    }
                }

                // wybieramy wolna taxe
                Taxi taxi = this.kolejkaTaxi.poll();
                if(taxi == null) {
                    continue;
                }

                // sprawdzamy czy taxa uszkodzona
                if(!this.idToState.get(taxi.numer())) {
                    this.kolejkaTaxi.addLast(taxi);
                    continue;
                }

                // tutaj byla zmiana potecjalnie tutaj sie wypieprzy jak juz, ze nie dodalem czegos co poprzednio usunalem
                // pobieramy zlecenie
                Integer zlecenie = this.kolejkaZlecen.poll();
                if(zlecenie == null){
                    this.kolejkaTaxi.addFirst(taxi);
                    continue;
                }

                this.numberOfAvaibleTaxis.decrementAndGet();

                // zeby nie wsadzilo zadania po zakonczeniu roboty :D
                if(this.konczymyPrace){
                    // odkladamy zlecenie tam gdzie bylo, jesli sie okazalo ze konczymy robote
                    // zlecenie nie moze byc nulem na gorze sprawdzilismy, tak samo jak taxa
                    this.kolejkaZlecen.addFirst(zlecenie);
                    return;
                }

                // odpalamy taxe
                this.executor.submit(() -> {
                    taxi.wykonajZlecenie(zlecenie);

                    // dodajemy siebie spowrotem do kolejki taxowek
                    this.kolejkaTaxi.offer(taxi);
                    this.numberOfAvaibleTaxis.incrementAndGet();

                    // budzimy deamona, ze ogarnelismy sie
                    this.blokadaOperacyjna.lock();
                    try {
                        this.wakeUp.signal();
                    } finally {
                        this.blokadaOperacyjna.unlock();
                    }
                });

            } finally {
                this.blokadaOperacyjna.unlock();
            }
        }
    }




    /**
     * Metoda przekazuje Dyspozytorni flotę taksówek.
     *
     * @param flota zbiór taksówek
     */
    @Override
    public void flota(Set<Taxi> flota) {
        // Strutkury dla Taxi
        for (Taxi taxi :  flota) {
            // zalozenie ze nr taxi sa unikatowe
            this.idToState.put(taxi.numer(), true);
            this.kolejkaTaxi.offer(taxi);
        }

        this.executor = Executors.newFixedThreadPool(flota.size(), r -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
        });

        this.numberOfAvaibleTaxis.set(flota.size());
        this.dyspozytorniaDeamon.start();
    }


    /**
     * Klient zamawia taksówkę tworząc nowe zlecenie. Metoda zwraca unikatowy numer
     * zlecenia.
     *
     * @return numer zlecenia
     */
    @Override
    public int zlecenie() {
        // tworzymy numer zlecenia
        int numerZlecenia = idZlecenia.incrementAndGet();

        // do kolejki
        this.kolejkaZlecen.offer(numerZlecenia);

        // sygnalizujemy, ze pojawia sie nowe zlecenie
        this.blokadaOperacyjna.lock();
        this.wakeUp.signal();
        this.blokadaOperacyjna.unlock();

        return numerZlecenia;
    }


    /**
     * Kierowca zgłasza awarię taksówki.
     *
     * @param numer         taksówki, która uległa awarii
     * @param numerZlecenia numer niewykonanego zlecenia
     */
    @Override
    public void awaria(int numer, int numerZlecenia) {
        // TODO
        // tutaj dodalem blokade, trzeba przemyslec jej sens
        this.blokadaOperacyjna.lock();

        this.idToState.put(numer, false);
        this.numberOfAvaibleTaxis.decrementAndGet();

        // dodajemy na poczatku kolejki zlecenie, tzn ze ma priorytet, opcjonalnie zrobie druga kolejke dla tych przerwanych
        this.kolejkaZlecen.addFirst(numerZlecenia);

        this.blokadaOperacyjna.unlock();
    }


    /**
     * Kierowca zgłasza naprawę taksówki.
     *
     * @param numer numer taksówki, która została naprawiona
     */
    @Override
    public void naprawiono(int numer) {
        // blokada tutaj jest zbyteczna, bo jesli watek dziala i wezmie taksowke w ktorej juz zmieniono state to git, mozna z niej korzytsac bo naprawa
        this.idToState.put(numer, true);
        this.numberOfAvaibleTaxis.incrementAndGet();

        this.blokadaOperacyjna.lock();
        this.wakeUp.signal();
        this.blokadaOperacyjna.unlock();
    }


    /**
     * Koniec pracy Dyspozytorni. Metoda zwraca zbiór wszystkich numerów zleceń,
     * które nie zostały przydzielone taksówkom. Przydzielone zlecenia nie są
     * umieszczane w zbiorze. Jeśli doszło do awarii taksówki i zlecenie nie zostało
     * jeszcze przydzielone, także jego numer ma być umieszczony w wynikowym zbiorze.
     * Jeśli rozwiązanie używa egzekutorów, metoda koniecPracy powinna zakończyć ich pracę.
     *
     * @return zbiór numerów zleceń, które nie zostały przydzielone
     */
    @Override
    public Set<Integer> koniecPracy() {
        this.konczymyPrace = true;

        this.blokadaOperacyjna.lock();
        HashSet<Integer> pozostaleZlecenia = new HashSet<>(this.kolejkaZlecen);
        this.executor.shutdown();
        this.wakeUp.signal();
        this.blokadaOperacyjna.unlock();

        return pozostaleZlecenia;
    }
}
