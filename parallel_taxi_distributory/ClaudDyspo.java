import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.*;

public class ClaudDyspo implements Dyspozytornia {
    // Generator unikalnych ID zleceń
    private final AtomicInteger nextOrderId = new AtomicInteger(1);

    // Flota taksówek
    private Set<Taxi> flota;

    // Kolejka zleceń priorytetowych (awarie)
    private final Queue<Zlecenie> priorityQueue = new ConcurrentLinkedQueue<>();

    // Kolejka zwykłych zleceń
    private final Queue<Zlecenie> normalQueue = new ConcurrentLinkedQueue<>();

    // Wolne taksówki gotowe do pracy
    private final Queue<Taxi> availableTaxis = new ConcurrentLinkedQueue<>();

    // Taksówki w awarii (numer taksówki -> true)
    private final Set<Integer> brokenTaxis = ConcurrentHashMap.newKeySet();

    // Zlecenia aktualnie realizowane (numerZlecenia -> Taxi)
    private final Map<Integer, Taxi> activeOrders = new ConcurrentHashMap<>();

    // ExecutorService dla wątków-demonów
    private ExecutorService executorService;

    // Lock do synchronizacji przydzielania zleceń
    private final Lock assignmentLock = new ReentrantLock();
    private final Condition newTaskOrTaxi = assignmentLock.newCondition();

    // Flaga zakończenia pracy
    private volatile boolean shutdown = false;

    // Wątek dispatcher
    private Thread dispatcherThread;

    public ClaudDyspo() {
        // ExecutorService z fabryką wątków-demonów
        this.executorService = Executors.newCachedThreadPool(r -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
        });
    }

    @Override
    public void flota(Set<Taxi> flota) {
        this.flota = flota;
        this.availableTaxis.addAll(flota);

        // Startujemy wątek dispatchera - WAŻNE: jako demon i bez blokowania
        dispatcherThread = new Thread(this::dispatchLoop, "Dispatcher-Thread");
        dispatcherThread.setDaemon(true);
        dispatcherThread.start();
        // Metoda od razu się kończy, nie czeka na dispatcherThread
    }

    @Override
    public int zlecenie() {
        int orderId = nextOrderId.getAndIncrement();
        Zlecenie zlecenie = new Zlecenie(orderId, false);

        normalQueue.offer(zlecenie);

        // Sygnalizujemy, że jest nowe zlecenie
        assignmentLock.lock();
        try {
            newTaskOrTaxi.signal();
        } finally {
            assignmentLock.unlock();
        }

        return orderId;
    }

    @Override
    public void awaria(int numer, int numerZlecenia) {
        brokenTaxis.add(numer);

        // Zlecenie wraca jako priorytetowe
        Zlecenie zlecenie = new Zlecenie(numerZlecenia, true);
        priorityQueue.offer(zlecenie);

        // Usuwamy z aktywnych
        activeOrders.remove(numerZlecenia);

        // Sygnalizujemy, że jest nowe zlecenie priorytetowe
        assignmentLock.lock();
        try {
            newTaskOrTaxi.signal();
        } finally {
            assignmentLock.unlock();
        }
    }

    @Override
    public void naprawiono(int numer) {
        brokenTaxis.remove(numer);

        // Znajdujemy taksówkę i dodajemy ją z powrotem do dostępnych
        for (Taxi taxi : flota) {
            if (taxi.numer() == numer) {
                availableTaxis.offer(taxi);

                // Sygnalizujemy, że jest wolna taksówka
                assignmentLock.lock();
                try {
                    newTaskOrTaxi.signal();
                } finally {
                    assignmentLock.unlock();
                }
                break;
            }
        }
    }

    @Override
    public Set<Integer> koniecPracy() {
        shutdown = true;

        // Budzimy dispatcher
        assignmentLock.lock();
        try {
            newTaskOrTaxi.signal();
        } finally {
            assignmentLock.unlock();
        }

        // Wyłączamy executor
        executorService.shutdownNow();

        // Zbieramy nieprzydzielone zlecenia
        Set<Integer> unassigned = new HashSet<>();

        Zlecenie z;
        while ((z = priorityQueue.poll()) != null) {
            unassigned.add(z.numerZlecenia);
        }
        while ((z = normalQueue.poll()) != null) {
            unassigned.add(z.numerZlecenia);
        }

        return unassigned;
    }

    // Główna pętla dispatchera - przydziela zlecenia do wolnych taksówek
    private void dispatchLoop() {
        while (!shutdown) {
            assignmentLock.lock();
            try {
                // Czekamy aż będzie zlecenie i wolna taksówka
                while (!shutdown && !canAssignOrder()) {
                    newTaskOrTaxi.await();
                }

                if (shutdown) {
                    break;
                }

                // Próbujemy przydzielić zlecenie
                assignOrder();

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } finally {
                assignmentLock.unlock();
            }
        }
    }

    // Sprawdza czy można przydzielić zlecenie
    private boolean canAssignOrder() {
        return !availableTaxis.isEmpty() &&
                (!priorityQueue.isEmpty() || !normalQueue.isEmpty());
    }

    // Przydziela zlecenie do wolnej taksówki
    private void assignOrder() {
        Taxi taxi = availableTaxis.poll();
        if (taxi == null) return;

        // Sprawdzamy czy taksówka nie jest zepsuta
        if (brokenTaxis.contains(taxi.numer())) {
            // Nie dodajemy jej z powrotem do dostępnych
            return;
        }

        // Najpierw priorytetowe, potem zwykłe
        Zlecenie zlecenie = priorityQueue.poll();
        if (zlecenie == null) {
            zlecenie = normalQueue.poll();
        }

        if (zlecenie == null) {
            // Brak zleceń, zwracamy taksówkę
            availableTaxis.offer(taxi);
            return;
        }

        // Zapamiętujemy aktywne zlecenie
        activeOrders.put(zlecenie.numerZlecenia, taxi);

        // Uruchamiamy wykonanie zlecenia w osobnym wątku
        final Zlecenie finalZlecenie = zlecenie;
        executorService.submit(() -> executeTaxiOrder(taxi, finalZlecenie));
    }

    // Wykonuje zlecenie w taksówce
    private void executeTaxiOrder(Taxi taxi, Zlecenie zlecenie) {
        taxi.wykonajZlecenie(zlecenie.numerZlecenia);

        // Zlecenie wykonane pomyślnie - kod poniżej wykona się TYLKO jeśli nie było awarii
        activeOrders.remove(zlecenie.numerZlecenia);

        // Taksówka jest znów wolna
        if (!brokenTaxis.contains(taxi.numer())) {
            availableTaxis.offer(taxi);

            // Sygnalizujemy, że taksówka jest wolna
            assignmentLock.lock();
            try {
                newTaskOrTaxi.signal();
            } finally {
                assignmentLock.unlock();
            }
        }

    }
}

class Zlecenie {
    final int numerZlecenia;
    final boolean priority;

    Zlecenie(int numerZlecenia, boolean priority) {
        this.numerZlecenia = numerZlecenia;
        this.priority = priority;
    }
}

// ============== PRZYKŁAD UŻYCIA ==============

/*
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        ArrayList<Dyspozytornia> dyspozytornieList = new ArrayList<>();

        int num_of_threads = 3; // Zmniejszam dla czytelności przykładu

        // Tworzenie dyspozytorni z flotami
        for (int j = 0; j < num_of_threads; j++) {
            Dyspozytornia dyspozytornia = new DyspozytorniaWatkowa();
            Set<Taxi> flota = new HashSet<>();

            for (int i = 0; i < 5; i++) {
                flota.add(new TaxiWatkowa());
            }

            dyspozytornia.flota(flota);
            dyspozytornieList.add(dyspozytornia);
            System.out.println("Dyspozytornia " + j + " utworzona z flotą " + flota.size() + " taksówek");
        }

        System.out.println("\n=== Rozpoczęcie symulacji ===\n");

        // === SCENARIUSZ 1: Podstawowe zamawianie taksówek ===
        Dyspozytornia dysp1 = dyspozytornieList.get(0);

        System.out.println("--- Zamawianie 10 taksówek w dyspozytorni 0 ---");
        for (int i = 0; i < 10; i++) {
            int zlecenie = dysp1.zlecenie();
            System.out.println("Zamówiono zlecenie #" + zlecenie);
        }

        Thread.sleep(3000); // Czekamy żeby taksówki zdążyły wykonać zlecenia


        // === SCENARIUSZ 2: Symulacja awarii ===
        Dyspozytornia dysp2 = dyspozytornieList.get(1);

        System.out.println("\n--- Symulacja awarii w dyspozytorni 1 ---");
        int zlecenie1 = dysp2.zlecenie();
        int zlecenie2 = dysp2.zlecenie();
        int zlecenie3 = dysp2.zlecenie();

        System.out.println("Zamówiono zlecenia: #" + zlecenie1 + ", #" + zlecenie2 + ", #" + zlecenie3);

        Thread.sleep(500); // Czekamy żeby zlecenia się zaczęły

        // Symulujemy awarię taksówki nr 1 podczas wykonywania zlecenia2
        System.out.println("AWARIA! Taksówka nr 1 uległa awarii podczas zlecenia #" + zlecenie2);
        dysp2.awaria(1, zlecenie2);

        Thread.sleep(1000);

        // Naprawiamy taksówkę
        System.out.println("Taksówka nr 1 została naprawiona");
        dysp2.naprawiono(1);

        Thread.sleep(2000);


        // === SCENARIUSZ 3: Wiele zleceń jednocześnie z różnych wątków ===
        Dyspozytornia dysp3 = dyspozytornieList.get(2);

        System.out.println("\n--- Wiele wątków klientów jednocześnie w dyspozytorni 2 ---");

        // Tworzymy 5 wątków klientów, każdy zamawia 3 taksówki
        Thread[] klienci = new Thread[5];
        for (int i = 0; i < 5; i++) {
            final int klientId = i;
            klienci[i] = new Thread(() -> {
                for (int j = 0; j < 3; j++) {
                    int zlecenie = dysp3.zlecenie();
                    System.out.println("Klient " + klientId + " zamówił zlecenie #" + zlecenie);
                    try {
                        Thread.sleep(100); // Małe opóźnienie między zamówieniami
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
            klienci[i].start();
        }

        // Czekamy na wszystkich klientów
        for (Thread klient : klienci) {
            klient.join();
        }

        Thread.sleep(3000); // Czekamy na wykonanie zleceń


        // === SCENARIUSZ 4: Koniec pracy z niewykonanymi zleceniami ===
        System.out.println("\n--- Koniec pracy dyspozytorni 0 ---");

        // Dodajemy dużo zleceń, więcej niż taksówek
        for (int i = 0; i < 20; i++) {
            dysp1.zlecenie();
        }

        Thread.sleep(100); // Krótkie opóźnienie, nie wszystkie zdążą się wykonać

        Set<Integer> niewykonane = dysp1.koniecPracy();
        System.out.println("Niewykonane zlecenia: " + niewykonane);
        System.out.println("Liczba niewykonanych: " + niewykonane.size());


        // === Koniec wszystkich dyspozytorni ===
        System.out.println("\n=== Zamykanie wszystkich dyspozytorni ===");
        for (int i = 1; i < dyspozytornieList.size(); i++) {
            Set<Integer> niewykonane2 = dyspozytornieList.get(i).koniecPracy();
            System.out.println("Dyspozytornia " + i + " - niewykonanych: " + niewykonane2.size());
        }

        System.out.println("\n=== Koniec symulacji ===");
    }
}
*/


// ============== PRZYKŁAD UŻYCIA ==============

/*
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        ArrayList<Dyspozytornia> dyspozytornieList = new ArrayList<>();

        int num_of_threads = 3; // Zmniejszam dla czytelności przykładu

        // Tworzenie dyspozytorni z flotami
        for (int j = 0; j < num_of_threads; j++) {
            Dyspozytornia dyspozytornia = new DyspozytorniaWatkowa();
            Set<Taxi> flota = new HashSet<>();

            for (int i = 0; i < 5; i++) {
                flota.add(new TaxiWatkowa());
            }

            dyspozytornia.flota(flota);
            dyspozytornieList.add(dyspozytornia);
            System.out.println("Dyspozytornia " + j + " utworzona z flotą " + flota.size() + " taksówek");
        }

        System.out.println("\n=== Rozpoczęcie symulacji ===\n");

        // === SCENARIUSZ 1: Podstawowe zamawianie taksówek ===
        Dyspozytornia dysp1 = dyspozytornieList.get(0);

        System.out.println("--- Zamawianie 10 taksówek w dyspozytorni 0 ---");
        for (int i = 0; i < 10; i++) {
            int zlecenie = dysp1.zlecenie();
            System.out.println("Zamówiono zlecenie #" + zlecenie);
        }

        Thread.sleep(3000); // Czekamy żeby taksówki zdążyły wykonać zlecenia


        // === SCENARIUSZ 2: Symulacja awarii ===
        Dyspozytornia dysp2 = dyspozytornieList.get(1);

        System.out.println("\n--- Symulacja awarii w dyspozytorni 1 ---");
        int zlecenie1 = dysp2.zlecenie();
        int zlecenie2 = dysp2.zlecenie();
        int zlecenie3 = dysp2.zlecenie();

        System.out.println("Zamówiono zlecenia: #" + zlecenie1 + ", #" + zlecenie2 + ", #" + zlecenie3);

        Thread.sleep(500); // Czekamy żeby zlecenia się zaczęły

        // Symulujemy awarię taksówki nr 1 podczas wykonywania zlecenia2
        System.out.println("AWARIA! Taksówka nr 1 uległa awarii podczas zlecenia #" + zlecenie2);
        dysp2.awaria(1, zlecenie2);

        Thread.sleep(1000);

        // Naprawiamy taksówkę
        System.out.println("Taksówka nr 1 została naprawiona");
        dysp2.naprawiono(1);

        Thread.sleep(2000);


        // === SCENARIUSZ 3: Wiele zleceń jednocześnie z różnych wątków ===
        Dyspozytornia dysp3 = dyspozytornieList.get(2);

        System.out.println("\n--- Wiele wątków klientów jednocześnie w dyspozytorni 2 ---");

        // Tworzymy 5 wątków klientów, każdy zamawia 3 taksówki
        Thread[] klienci = new Thread[5];
        for (int i = 0; i < 5; i++) {
            final int klientId = i;
            klienci[i] = new Thread(() -> {
                for (int j = 0; j < 3; j++) {
                    int zlecenie = dysp3.zlecenie();
                    System.out.println("Klient " + klientId + " zamówił zlecenie #" + zlecenie);
                    try {
                        Thread.sleep(100); // Małe opóźnienie między zamówieniami
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
            klienci[i].start();
        }

        // Czekamy na wszystkich klientów
        for (Thread klient : klienci) {
            klient.join();
        }

        Thread.sleep(3000); // Czekamy na wykonanie zleceń


        // === SCENARIUSZ 4: Koniec pracy z niewykonanymi zleceniami ===
        System.out.println("\n--- Koniec pracy dyspozytorni 0 ---");

        // Dodajemy dużo zleceń, więcej niż taksówek
        for (int i = 0; i < 20; i++) {
            dysp1.zlecenie();
        }

        Thread.sleep(100); // Krótkie opóźnienie, nie wszystkie zdążą się wykonać

        Set<Integer> niewykonane = dysp1.koniecPracy();
        System.out.println("Niewykonane zlecenia: " + niewykonane);
        System.out.println("Liczba niewykonanych: " + niewykonane.size());


        // === Koniec wszystkich dyspozytorni ===
        System.out.println("\n=== Zamykanie wszystkich dyspozytorni ===");
        for (int i = 1; i < dyspozytornieList.size(); i++) {
            Set<Integer> niewykonane2 = dyspozytornieList.get(i).koniecPracy();
            System.out.println("Dyspozytornia " + i + " - niewykonanych: " + niewykonane2.size());
        }

        System.out.println("\n=== Koniec symulacji ===");
    }
}
*/