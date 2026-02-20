import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class TaxiImpl implements Taxi {

    private static AtomicInteger counter = new AtomicInteger(0);

    private final int numer;
    private final Dyspozytornia dyspozytornia;

    public TaxiImpl(Dyspozytornia dyspozytornia) {
        this.numer = TaxiImpl.counter.incrementAndGet();
        this.dyspozytornia = dyspozytornia;
    }

    @Override
    public int numer() {
        return this.numer;
    }

    @Override
    public void wykonajZlecenie(int numerZlecenia) {
        System.out.println("[Taxi " + numer + "] Start zlecenia " + numerZlecenia);

        try {
            // --- Etap 1: normalna jazda ---
            jedz("Etap 1 — spokojna jazda", 600);

            // --- losowa szansa awarii ---
            boolean wystapilaAwaria = Math.random() < 0.4;


            if (wystapilaAwaria) {
                System.out.println("[Taxi " + numer + "] WYKRYTO AWARIĘ! Zatrzymuję się...");

                // symulacja zatrzymania i diagnozy
                Thread.sleep(800);

                // zgłoszenie awarii
                System.out.println("[Taxi " + numer + "] Zgłaszam awarię do dyspozytorni.");
                dyspozytornia.awaria(numer, numerZlecenia);

                // symulacja naprawy trwa chwilę
                Thread.sleep(1200);

                System.out.println("[Taxi " + numer + "] Naprawiono! Zgłaszam do dyspozytorni.");
                dyspozytornia.naprawiono(numer);

                // powrót do jazdy
                System.out.println("[Taxi " + numer + "] Kontynuuję zlecenie po naprawie.");
            }

            // --- Etap 2: dalsza jazda po awarii lub normalnie ---
            jedz("Etap 2 — dojazd po naprawie", 700);

            System.out.println("[Taxi " + numer + "] Zakończono zlecenie " + numerZlecenia);
            return;
        } catch (InterruptedException e) {
            System.out.println("[Taxi " + numer + "] Przerwano jazdę!");
        }
    }

    private void jedz(String opis, int czas) throws InterruptedException {
        System.out.println("[Taxi " + numer + "] " + opis);
        Thread.sleep(czas);
    }
}
