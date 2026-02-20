import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

// Implementacja pod testy
public class TaxiWatkowa extends Thread implements Taxi{
    // Taksowka bazowa
    Taxi taxi;

    // numer zlecenia aktualnie wykonywany
    private volatile int numerZlecenia;

    // Referencje do Condition i Lock
    private final ReentrantLock lockOperation;
    private final ReentrantLock lockWorking;
    private final Condition zlecenieCondition;
    private final Condition naprawaCondition;

    // kolejka zlecen
    final private ConcurrentLinkedDeque<Integer> kolejkaZlecenia;

    // stan taxy
    private volatile Stan state;

    // Stany taksówki
    private enum Stan {
        DOSTEPNA,
        W_REALIZACJI,
        AWARIA,
        ZAKONCZONA
    }



    @Override
    public void run() {
        while(true) {
            this.lockOperation.lock();
            switch(state) {

                case DOSTEPNA:
                    try {
                        // czekamy na to, az jakies zlecenie przyjdzie
                        this.numerZlecenia = -1;
                        System.out.println("Czekam na zlecenie");
                        this.zlecenieCondition.await();
                    } catch (InterruptedException e) {}
                    //this.lock.unlock();
                    break;

                case W_REALIZACJI:
                    try {
                        System.out.println("Wykonuje zlecenie");
                        // dajemy mozliwosc wykonywania operacji przerwan itd
                        this.lockOperation.unlock();

                        // ale sygnalizujemy ciagle prace watka i jego nie wyjscie z metody
                        this.lockWorking.lock();
                        this.taxi.wykonajZlecenie(numerZlecenia);
                        this.lockWorking.unlock();

                        this.lockOperation.lock();

                        // jezeli wyjscie z wykonaj zlecenie pobralo wyjatek InterruptException(zdarzyc sie moze jak oblsuguje) to wtedy trzeba sprawdzic, czy wysjcie jest awaryjne, czy normalne
                        if(state==Stan.AWARIA || state==Stan.ZAKONCZONA) {
                            System.out.println("Mamy awarie\\zakoncznie");
                            break;
                        }

                        this.state = Stan.DOSTEPNA;
                        this.numerZlecenia = -1;
                        System.out.println("Po zleceniu normalnie");

                    // tutaj obsluga .interrupt, w zasadzie dzial tylko wtedy kiedy .wykonajZlecenie(...) nie obsluguje InterruptException
                    }catch(Exception e) {
                        if(state==Stan.AWARIA || state==Stan.ZAKONCZONA) {
                            System.out.println("Mamy awarie");
                            break;
                        }
                    }
                    //this.lock.unlock();
                    break;

                case AWARIA:
                    try {
                        System.out.println("Czekam na naprawe");
                        this.naprawaCondition.await();
                        System.out.println("Naprawiono MNIE!!");
                    } catch (InterruptedException e) {}
                    //this.lock.unlock();
                    break;


                case ZAKONCZONA:
                    // metoda konca pracy, tutaj tak naprawde zczytujemy sobie jaki jest ostatni numer zlecenie
                    System.out.println("Konczymy prace");
                    this.lockOperation.unlock();
                    return;
            }
            this.lockOperation.unlock();
        }
    }


    TaxiWatkowa(Taxi taxi, ReentrantLock lock, Condition noweZleceniaCondition, Condition naprawaCondition, ConcurrentLinkedDeque<Integer> kolejkaZlecenia) {
        super();

        this.taxi = taxi;

        this.lockWorking = new ReentrantLock();
        this.lockOperation = lock;

        this.zlecenieCondition = noweZleceniaCondition;
        this.naprawaCondition = naprawaCondition;

        this.state = Stan.DOSTEPNA;
        this.kolejkaZlecenia = kolejkaZlecenia;

        this.setDaemon(true);
        this.start();
    };

    /**
     * Unikalny numer identyfikujący taksówkę. Numery taksówek mogą być dowolne.
     *
     * @return numer identyfikujący taksówkę
     */
    @Override
    public int numer() {
        return this.taxi.numer();
    }


    /**
     * Realizacja zlecenia o podanym numerze. Wątek, który wywołał metodę zostaje
     * użyty do realizacji zlecenia. Czas realizacji zlecenia nie jest z góry znany.
     * O ile nie doszło do awarii taksówki, realizacja zlecenia kończy się wraz z
     * zakończeniem metody. Wątek wywołujący metodę jest przez okres potrzebny do
     * realizacji zlecenia zablokowany.
     *
     * @param numerZlecenia numer zlecenia do realizacji
     */
    @Override
    public void wykonajZlecenie(int numerZlecenia) {
        this.lockOperation.lock();

        // ustaw stan na to ze realizujemy, ustaw dobry numerZlecenia
        if(this.state!=Stan.ZAKONCZONA) {
            this.numerZlecenia = numerZlecenia;
            this.state = Stan.W_REALIZACJI;

            // powiadom, ze jest zlecenie
            this.zlecenieCondition.signal();
        }
        this.lockOperation.unlock();
    }

    public void oznaczAwarie(){
        this.lockOperation.lock();

        if(this.state != Stan.ZAKONCZONA) {
            this.numerZlecenia = -1;
            this.state = Stan.AWARIA;
            this.interrupt();
        }

        this.lockOperation.unlock();
    }

    public void oznaczNaprawe(){
        this.lockOperation.lock();
        if(this.state != Stan.ZAKONCZONA) {
            this.state = Stan.DOSTEPNA;
            this.naprawaCondition.signal();
        }
        this.lockOperation.unlock();
    }

    public int konczymyPrace(){
        this.lockOperation.lock();

        if(this.state == Stan.DOSTEPNA || this.state == Stan.AWARIA) {
            this.state = Stan.ZAKONCZONA;
            this.zlecenieCondition.signal();
            this.naprawaCondition.signal();
        }else if(this.state == Stan.W_REALIZACJI){
            this.state = Stan.ZAKONCZONA;
            this.interrupt();
        }
        System.out.println("Ostanti numer zlecenia: " + this.numerZlecenia);

        this.lockOperation.unlock();
        return this.numerZlecenia;
    }
}