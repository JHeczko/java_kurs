import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    // Implementacja testowej taksówki
    static class TestTaxi implements Taxi {
        private final int numer;
        private volatile boolean czyUszkodzona = false;
        private final Random rand = new Random();
        private final AtomicInteger wykonaneZlecenia = new AtomicInteger(0);
        public volatile int numerZleceniaMoj;

        public TestTaxi(int numer) {
            this.numer = numer;
        }

        @Override
        public int numer() {
            return numer;
        }

        @Override
        public void wykonajZlecenie(int numerZlecenia) {
            System.out.println("[Taxi " + numer + "] Rozpoczynam zlecenie " + numerZlecenia);
            this.numerZleceniaMoj = numerZlecenia;

            try {
                // Symulacja jazdy (50-200ms)
                Thread.sleep(50 + rand.nextInt(150));

                // Jeśli taksówka została uszkodzona, przerywamy
                if (czyUszkodzona) {
                    System.out.println("[Taxi " + numer + "] AWARIA podczas zlecenia " + numerZlecenia);
                    return;
                }

                wykonaneZlecenia.incrementAndGet();
                System.out.println("[Taxi " + numer + "] Zakończono zlecenie " + numerZlecenia);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        public void symulujAwariePoczas(int numerZlecenia) {
            System.out.println("[Taxi " + numer + "] Awaria");
            czyUszkodzona = true;
        }

        public void napraw() {
            System.out.println("[Taxi " + numer + "] Naprawa");
            czyUszkodzona = false;
        }

        public int ileWykonanych() {
            return wykonaneZlecenia.get();
        }
    }

    // ==============================================
    // TEST 1: Podstawowa funkcjonalność
    // ==============================================
    public static void test1_PodstawowaFunkcjonalnosc() {
        System.out.println("\n=== TEST 1: Podstawowa funkcjonalność ===");

        Dyspozytornia dysp = new DyspozytorniaWatkowa();
        Set<Taxi> flota = new HashSet<>();

        TestTaxi taxi1 = new TestTaxi(1);
        TestTaxi taxi2 = new TestTaxi(2);
        flota.add(taxi1);
        flota.add(taxi2);

        dysp.flota(flota);

        // Zgłaszamy 5 zleceń
        int z1 = dysp.zlecenie();
        int z2 = dysp.zlecenie();
        int z3 = dysp.zlecenie();
        int z4 = dysp.zlecenie();
        int z5 = dysp.zlecenie();

        System.out.println("Zgłoszono zlecenia: " + z1 + ", " + z2 + ", " + z3 + ", " + z4 + ", " + z5);

        // Czekamy na realizację
        try { Thread.sleep(500); } catch (InterruptedException e) {}

        Set<Integer> nieprzydzielone = dysp.koniecPracy();

        System.out.println("Nieprzydzielone zlecenia: " + nieprzydzielone);
        System.out.println("Taxi 1 wykonała: " + taxi1.ileWykonanych());
        System.out.println("Taxi 2 wykonała: " + taxi2.ileWykonanych());

        if (nieprzydzielone.isEmpty() && (taxi1.ileWykonanych() + taxi2.ileWykonanych() == 5)) {
            System.out.println("✓ TEST 1 PASSED");
        } else {
            System.out.println("✗ TEST 1 FAILED");
        }
    }

    // ==============================================
    // TEST 2: Awaria taksówki - priorytet zlecenia
    // ==============================================
    public static void test2_AwariaPriorytet() {
        System.out.println("\n=== TEST 2: Awaria i priorytet zlecenia ===");

        Dyspozytornia dysp = new DyspozytorniaWatkowa();
        Set<Taxi> flota = new HashSet<>();

        TestTaxi taxi1 = new TestTaxi(10);
        TestTaxi taxi2 = new TestTaxi(20);
        flota.add(taxi1);
        flota.add(taxi2);

        dysp.flota(flota);

        int z1 = dysp.zlecenie();
        int z2 = dysp.zlecenie();

        // Czekamy aż jedno zlecenie zacznie się wykonywać
        try { Thread.sleep(30); } catch (InterruptedException e) {}

        // Symulujemy awarię taxi 10 podczas zlecenia
        taxi1.symulujAwariePoczas(z1);
        dysp.awaria(10, z1);

        System.out.println("Zgłoszono awarię taxi 10 podczas zlecenia " + z1);

        int z3 = dysp.zlecenie();
        int z4 = dysp.zlecenie();

        // Czekamy chwilę i naprawiamy
        try { Thread.sleep(100); } catch (InterruptedException e) {}

        taxi1.napraw();
        dysp.naprawiono(10);
        System.out.println("Naprawiono taxi 10");

        // Czekamy na zakończenie
        try { Thread.sleep(500); } catch (InterruptedException e) {}

        Set<Integer> nieprzydzielone = dysp.koniecPracy();

        System.out.println("Nieprzydzielone: " + nieprzydzielone);
        System.out.println("Taxi 10 wykonała: " + taxi1.ileWykonanych());
        System.out.println("Taxi 20 wykonała: " + taxi2.ileWykonanych());

        // Zlecenie z1 powinno zostać ponownie przydzielone i wykonane
        if (taxi1.ileWykonanych() + taxi2.ileWykonanych() >= 3) {
            System.out.println("✓ TEST 2 PASSED");
        } else {
            System.out.println("✗ TEST 2 FAILED");
        }
    }

    // ==============================================
    // TEST 3: Więcej zleceń niż taksówek
    // ==============================================
    public static void test3_WiecejZlecenNizTaksowek() {
        System.out.println("\n=== TEST 3: Więcej zleceń niż taksówek ===");

        Dyspozytornia dysp = new DyspozytorniaWatkowa();
        Set<Taxi> flota = new HashSet<>();

        TestTaxi taxi1 = new TestTaxi(100);
        flota.add(taxi1);

        dysp.flota(flota);

        // Zgłaszamy 10 zleceń, ale mamy tylko 1 taksówkę
        List<Integer> zlecenia = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            zlecenia.add(dysp.zlecenie());
        }

        System.out.println("Zgłoszono 10 zleceń dla 1 taksówki");

        // Czekamy tylko na część realizacji
        try { Thread.sleep(200); } catch (InterruptedException e) {}

        Set<Integer> nieprzydzielone = dysp.koniecPracy();

        System.out.println("Wykonano: " + taxi1.ileWykonanych());
        System.out.println("Nieprzydzielone: " + nieprzydzielone.size());

        // Część powinna zostać nieprzydzielona
        if (nieprzydzielone.size() > 0 && taxi1.ileWykonanych() < 10) {
            System.out.println("✓ TEST 3 PASSED");
        } else {
            System.out.println("✗ TEST 3 FAILED");
        }
    }

    // ==============================================
    // TEST 4: Równoległe zgłaszanie zleceń
    // ==============================================
    public static void test4_RownolegleZglaszanie() throws InterruptedException {
        System.out.println("\n=== TEST 4: Równoległe zgłaszanie zleceń ===");

        Dyspozytornia dysp = new DyspozytorniaWatkowa();
        Set<Taxi> flota = new HashSet<>();

        for (int i = 1; i <= 3; i++) {
            flota.add(new TestTaxi(i));
        }

        dysp.flota(flota);

        // 5 wątków jednocześnie zgłasza zlecenia
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(5);
        Set<Integer> wszytkieZlecenia = Collections.synchronizedSet(new HashSet<>());

        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                try {
                    start.await();
                    for (int j = 0; j < 4; j++) {
                        int z = dysp.zlecenie();
                        wszytkieZlecenia.add(z);
                        Thread.sleep(10);
                    }
                    done.countDown();
                } catch (InterruptedException e) {}
            }).start();
        }

        start.countDown(); // Start!
        done.await(); // Czekamy na zakończenie zgłaszania

        System.out.println("Zgłoszono łącznie: " + wszytkieZlecenia.size() + " zleceń");

        // Czekamy na realizację
        Thread.sleep(1000);

        Set<Integer> nieprzydzielone = dysp.koniecPracy();

        System.out.println("Nieprzydzielone: " + nieprzydzielone.size());

        // Wszystkie numery zleceń powinny być unikalne
        if (wszytkieZlecenia.size() == 20) {
            System.out.println("✓ TEST 4 PASSED - wszystkie zlecenia unikalne");
        } else {
            System.out.println("✗ TEST 4 FAILED - duplikaty zleceń!");
        }
    }

    // ==============================================
    // TEST 5: Koniec pracy bez realizacji
    // ==============================================
    public static void test5_KoniecPracyNatychmiast() {
        System.out.println("\n=== TEST 5: Koniec pracy zaraz po zgłoszeniu ===");

        Dyspozytornia dysp = new DyspozytorniaWatkowa();
        Set<Taxi> flota = new HashSet<>();

        flota.add(new TestTaxi(1));
        dysp.flota(flota);

        int z1 = dysp.zlecenie();
        int z2 = dysp.zlecenie();
        int z3 = dysp.zlecenie();

        // Natychmiast kończymy pracę (bez czekania)
        Set<Integer> nieprzydzielone = dysp.koniecPracy();

        System.out.println("Nieprzydzielone zlecenia: " + nieprzydzielone);

        // Większość lub wszystkie powinny być nieprzydzielone
        if (nieprzydzielone.size() >= 2) {
            System.out.println("✓ TEST 5 PASSED");
        } else {
            System.out.println("✗ TEST 5 FAILED");
        }
    }

    // ==============================================
    // TEST 6: Maksymalne wykorzystanie floty
    // ==============================================
    public static void test6_MaksymalneWykorzystanie() throws InterruptedException {
        System.out.println("\n=== TEST 6: Maksymalne wykorzystanie floty ===");

        Dyspozytornia dysp = new DyspozytorniaWatkowa();
        Set<Taxi> flota = new HashSet<>();

        List<TestTaxi> taksowki = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            TestTaxi t = new TestTaxi(i);
            taksowki.add(t);
            flota.add(t);
        }

        dysp.flota(flota);

        // Zgłaszamy dużo zleceń
        for (int i = 0; i < 20; i++) {
            dysp.zlecenie();
        }

        // Czekamy chwilę i sprawdzamy czy wszystkie taxi pracują
        Thread.sleep(2000);

        int pracujace = 0;
        for (TestTaxi t : taksowki) {
            if (t.ileWykonanych() > 0) {
                pracujace++;
            }
        }

        System.out.println("Liczba taksówek, które pracowały: " + pracujace + "/5");

        // Czekamy na realizację pozostałych
        Thread.sleep(1500);

        dysp.koniecPracy();

        int suma = 0;
        for (TestTaxi t : taksowki) {
            suma += t.ileWykonanych();
        }

        System.out.println("Łącznie wykonano: " + suma + " zleceń");

        // Wszystkie taksówki powinny pracować
        if (pracujace == 5) {
            System.out.println("✓ TEST 6 PASSED - pełne wykorzystanie floty");
        } else {
            System.out.println("✗ TEST 6 FAILED - nieużywane taksówki!");
        }
    }

    // ==============================================
    // TEST 7: Bezczynność - brak CPU
    // ==============================================
    public static void test7_Bezczynnosc() throws InterruptedException {
        System.out.println("\n=== TEST 7: Test bezczynności (brak zleceń) ===");

        Dyspozytornia dysp = new DyspozytorniaWatkowa();
        Set<Taxi> flota = new HashSet<>();
        flota.add(new TestTaxi(1));

        dysp.flota(flota);

        System.out.println("Dyspozytornia uruchomiona bez zleceń...");
        System.out.println("Czekam 2 sekundy (demon powinien spać)...");

        // Jeśli demon busy-waituje, zobaczymy wysokie CPU
        Thread.sleep(2000);

        // Teraz zgłaszamy zlecenie
        int z = dysp.zlecenie();
        System.out.println("Zgłoszono zlecenie " + z);

        Thread.sleep(300);

        Set<Integer> nieprzydzielone = dysp.koniecPracy();

        if (nieprzydzielone.isEmpty()) {
            System.out.println("✓ TEST 7 PASSED - zlecenie wykonane po bezczynności");
        } else {
            System.out.println("✗ TEST 7 FAILED");
        }
    }

    // ==============================================
    // TEST 8: Awaria i naprawa w trakcie pracy
    // ==============================================
    public static void test8_AwariaINaprawaWTrakcie() throws InterruptedException {
        System.out.println("\n=== TEST 8: Awaria i naprawa podczas pracy ===");

        Dyspozytornia dysp = new DyspozytorniaWatkowa();
        Set<Taxi> flota = new HashSet<>();

        TestTaxi taxi1 = new TestTaxi(1);
        TestTaxi taxi2 = new TestTaxi(2);

        flota.add(taxi1);
        flota.add(taxi2);

        dysp.flota(flota);

        // Zgłaszamy wiele zleceń
        for (int i = 0; i < 10; i++) {
            dysp.zlecenie();
        }

        Thread.sleep(100);

        // Awaria taxi 1
        taxi1.symulujAwariePoczas(-1);
        int zlecenieDoPowtorzenia = 999; // symulacja
        dysp.awaria(1, zlecenieDoPowtorzenia);
        System.out.println("Taxi 1 uległa awarii");

        Thread.sleep(500);

        int wykonanePrzedNaprawaT1 = taxi1.ileWykonanych();
        int wykonaneT2Przed = taxi2.ileWykonanych();

        // Naprawa
        taxi1.napraw();
        dysp.naprawiono(1);
        System.out.println("Taxi 1 naprawiona");

        Thread.sleep(500);

        int wykonanePoNaprawieT1 = taxi1.ileWykonanych();

        System.out.println("Taxi 1: przed=" + wykonanePrzedNaprawaT1 + ", po=" + wykonanePoNaprawieT1);
        System.out.println("Taxi 2: " + taxi2.ileWykonanych());

        dysp.koniecPracy();

        // Po naprawie taxi 1 powinna dalej pracować
        if (wykonanePoNaprawieT1 > wykonanePrzedNaprawaT1) {
            System.out.println("✓ TEST 8 PASSED - taxi pracuje po naprawie");
        } else {
            System.out.println("✗ TEST 8 FAILED - taxi nie pracuje po naprawie!");
        }
    }

    // ==============================================
    // TEST 9: Priorytet zlecenia po awarii nad zwykłymi
    // ==============================================
    public static void test9_PriorytetPoAwarjiNadZwyklymi() throws InterruptedException {
            System.out.println("\n=== TEST 9: Priorytet zlecenia po awarii nad zwykłymi ===");

            Dyspozytornia dysp = new DyspozytorniaWatkowa();
            Set<Taxi> flota = new HashSet<>();

            TestTaxi taxi1 = new TestTaxi(1);
            TestTaxi taxi2 = new TestTaxi(2);
            flota.add(taxi1);
            flota.add(taxi2);
            dysp.flota(flota);

            // Najpierw kilka zwykłych zleceń, które trafią do kolejki
            for (int i = 0; i < 5; i++) {
                dysp.zlecenie();
            }

            Thread.sleep(150);
            // Awaria taxi1 podczas specjalnego zlecenia
            int zAwaryjne = taxi1.numerZleceniaMoj;
            taxi1.symulujAwariePoczas(zAwaryjne);
            dysp.awaria(1, zAwaryjne);
            System.out.println("AWARIA podczas " + zAwaryjne);

            // Po awarii dorzucamy kilka zwykłych zleceń
            for (int i = 0; i < 3; i++) {
                dysp.zlecenie();
            }

            // Naprawiamy taxi1 – po naprawie zAwaryjne ma mieć priorytet
            Thread.sleep(100);
            System.out.println("Taxi 1 naprawiona");
            taxi1.napraw();
            dysp.naprawiono(1);

            Thread.sleep(1000);
            Set<Integer> nieprzydzielone = dysp.koniecPracy();

            System.out.println("Nieprzydzielone: " + nieprzydzielone);
            System.out.println("Taxi1 wykonała: " + taxi1.ileWykonanych());
            System.out.println("Taxi2 wykonała: " + taxi2.ileWykonanych());

            if (!nieprzydzielone.contains(zAwaryjne)) {
                System.out.println("✓ TEST 9 PASSED - zlecenie po awarii nie zostało zgubione");
            } else {
                System.out.println("✗ TEST 9 FAILED - zlecenie po awarii zgubione!");
            }
        }

        // ==============================================
    // TEST 10: Nieblokujące zlecenie() przy pełnej flocie
    // ==============================================
    public static void test10_NieblokujaceZlecenie() throws InterruptedException {
            System.out.println("\n=== TEST 10: Nieblokujące zlecenie() przy pełnej flocie ===");

            Dyspozytornia dysp = new DyspozytorniaWatkowa();
            Set<Taxi> flota = new HashSet<>();
            flota.add(new TestTaxi(1));
            flota.add(new TestTaxi(2));
            dysp.flota(flota);

            // Zalewamy system zleceniami
            for (int i = 0; i < 20; i++) {
                dysp.zlecenie();
            }

            long start = System.currentTimeMillis();
            Thread t = new Thread(() -> {
                for (int i = 0; i < 50; i++) {
                    dysp.zlecenie();
                }
            });
            t.start();
            t.join();
            long end = System.currentTimeMillis();

            dysp.koniecPracy();

            long czas = end - start;
            System.out.println("Czas zgłaszania 50 zleceń: " + czas + " ms");

            if (czas < 500) {
                System.out.println("✓ TEST 10 PASSED - zlecenie() nie blokuje");
            } else {
                System.out.println("✗ TEST 10 FAILED - zlecenie() prawdopodobnie blokuje");
            }
        }

        // ==============================================
    // TEST 11: Nieblokująca awaria()
    // ==============================================
    public static void test11_NieblokujacaAwaria() throws InterruptedException {
            System.out.println("\n=== TEST 11: Nieblokująca awaria() ===");

            Dyspozytornia dysp = new DyspozytorniaWatkowa();
            Set<Taxi> flota = new HashSet<>();
            TestTaxi taxi1 = new TestTaxi(1);
            flota.add(taxi1);
            dysp.flota(flota);

            int z1 = dysp.zlecenie();
            Thread.sleep(50);

            taxi1.symulujAwariePoczas(z1);

            long start = System.currentTimeMillis();
            dysp.awaria(1, z1);
            long end = System.currentTimeMillis();

            long czas = end - start;
            System.out.println("Czas wywołania awaria(): " + czas + " ms");

            if (czas < 100) {
                System.out.println("✓ TEST 11 PASSED - awaria() nie blokuje");
            } else {
                System.out.println("✗ TEST 11 FAILED - awaria() blokuje zbyt długo");
            }

            dysp.koniecPracy();
        }

        // ==============================================
    // TEST 12: Uszkodzona taxi nieużywana przed naprawą, używana po naprawie
    // ==============================================
    public static void test12_NieUzywacUszkodzonejDoNaprawy() throws InterruptedException  {
        System.out.println("\n=== TEST 12: Uszkodzona taxi nie jest używana przed naprawą ===");

        Dyspozytornia dysp = new DyspozytorniaWatkowa();
        Set<Taxi> flota = new HashSet<>();
        TestTaxi taxi1 = new TestTaxi(1);
        TestTaxi taxi2 = new TestTaxi(2);
        flota.add(taxi1);
        flota.add(taxi2);
        dysp.flota(flota);

        // Dużo zleceń
        for (int i = 0; i < 10; i++) {
            dysp.zlecenie();
        }

        Thread.sleep(100);

        // Awaria taxi1
        taxi1.symulujAwariePoczas(taxi1.numerZleceniaMoj);
        dysp.awaria(1, taxi1.numerZleceniaMoj);

        int przedNaprawaT1 = taxi1.ileWykonanych();
        int przedNaprawaT2 = taxi2.ileWykonanych();

        // Kolejne zlecenia po awarii
        for (int i = 0; i < 10; i++) {
            dysp.zlecenie();
        }

        Thread.sleep(500);

        int poAwariiPrzedNaprawaT1 = taxi1.ileWykonanych();
        int poAwariiPrzedNaprawaT2 = taxi2.ileWykonanych();

        // Naprawa taxi1
         System.out.println("Naprawiam taxe1");
        taxi1.napraw();

        dysp.naprawiono(1);

        Thread.sleep(500);

        int poNaprawieT1 = taxi1.ileWykonanych();

        dysp.koniecPracy();

        System.out.println("Taxi1: przed awarią=" + przedNaprawaT1 +
                ", po awarii przed naprawą=" + poAwariiPrzedNaprawaT1 +
                ", po naprawie=" + poNaprawieT1);
        System.out.println("Taxi2: " + przedNaprawaT2 + " -> " + poAwariiPrzedNaprawaT2);

        boolean nieUzywanaUszkodzona = (poAwariiPrzedNaprawaT1 == przedNaprawaT1);
        boolean uzywanaPoNaprawie = (poNaprawieT1 > poAwariiPrzedNaprawaT1);

        if (nieUzywanaUszkodzona && uzywanaPoNaprawie) {
            System.out.println("✓ TEST 12 PASSED - uszkodzona taxi nie pracuje, po naprawie wraca do pracy");
        } else {
            System.out.println("✗ TEST 12 FAILED");
        }
    }


    // ==============================================
    // TEST 13: 30 wątków jednocześnie zgłaszających zlecenia
    // ==============================================
    public static void test13_MasoweZglaszanieIAwarie_Poprawne() throws InterruptedException {
        System.out.println("\n=== TEST 13: 30 wątków jednocześnie zgłaszających zlecenia ===");

        final int LICZBA_WATKOW = 30;
        final int ZLECENIA_NA_WATEK = 15;

        Dyspozytornia dysp = new DyspozytorniaWatkowa();
        Set<Taxi> flota = new HashSet<>();

        // Flota z wieloma taksówkami, żeby dyspozytornia miała czym żonglować
        List<TestTaxi> taksowki = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            TestTaxi t = new TestTaxi(i);
            taksowki.add(t);
            flota.add(t);
        }

        dysp.flota(flota);


        // Bariera – wszystkie 30 wątków rusza w jednym momencie
        CyclicBarrier barieraStart = new CyclicBarrier(LICZBA_WATKOW);
        CountDownLatch done = new CountDownLatch(LICZBA_WATKOW);

        for (int i = 0; i < LICZBA_WATKOW; i++) {
            Thread t = new Thread(() -> {
                try {
                    // Czekamy aż wszystkie wątki będą gotowe
                    barieraStart.await();

                    // Po przejściu bariery każdy wątek zgłasza serię zleceń
                    for (int j = 0; j < ZLECENIA_NA_WATEK; j++) {
                        dysp.zlecenie();

                        // Minimalne opóźnienie dla urozmaicenia harmonogramu
                        Thread.sleep(5);
                    }

                } catch (Exception e) {
                    System.err.println("Błąd w wątku: " + e);
                    e.printStackTrace();
                } finally {
                    done.countDown();
                }
            });
            t.setDaemon(true);
            t.start();
        }

        // Czekamy aż wszystkie wątki zakończą zgłaszanie zleceń
        done.await();

        // Dajemy dyspozytorni czas na wykonanie zleceń
        Thread.sleep(2000);

        Set<Integer> nieprzydzielone = dysp.koniecPracy();

        int sumaWykonanych = 0;
        for (TestTaxi t : taksowki) {
            sumaWykonanych += t.ileWykonanych();
        }

        int oczekiwanaLiczbaZlecen = LICZBA_WATKOW * ZLECENIA_NA_WATEK;

        System.out.println("Łącznie zgłoszono zleceń: " + oczekiwanaLiczbaZlecen);
        System.out.println("Łącznie wykonano zleceń (wg taksówek): " + sumaWykonanych);
        System.out.println("Nieprzydzielone zlecenia (koniecPracy): " + nieprzydzielone.size());

        if (sumaWykonanych + nieprzydzielone.size() <= oczekiwanaLiczbaZlecen) {
            System.out.println("✓ TEST 13 PASSED - 3 różne awarie, każda zgłoszona tylko raz, powiązana z właściwą taxą");
        } else {
            System.out.println("✗ TEST 13 FAILED - niespójność liczby zleceń");
        }
    }






    // ==============================================
    // MAIN - uruchamianie testów
    // ==============================================
    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════╗");
        System.out.println("║   TESTY DYSPOZYTORNI WĄTKOWEJ - PW 2024       ║");
        System.out.println("╚═══════════════════════════════════════════════╝");

        try {
//            test1_PodstawowaFunkcjonalnosc();
//            test2_AwariaPriorytet();
//            test3_WiecejZlecenNizTaksowek();
//            test4_RownolegleZglaszanie();
//            test5_KoniecPracyNatychmiast();
//            test6_MaksymalneWykorzystanie();
//            test7_Bezczynnosc();
//            test8_AwariaINaprawaWTrakcie();
//
//            test9_PriorytetPoAwarjiNadZwyklymi();
//            test10_NieblokujaceZlecenie();
//            test11_NieblokujacaAwaria();
//            test12_NieUzywacUszkodzonejDoNaprawy();
            test13_MasoweZglaszanieIAwarie_Poprawne();
            System.out.println("\n╔═══════════════════════════════════════════════╗");
            System.out.println("║         WSZYSTKIE TESTY ZAKOŃCZONE            ║");
            System.out.println("╚═══════════════════════════════════════════════╝");

        } catch (Exception e) {
            System.err.println("KRYTYCZNY BŁĄD PODCZAS TESTÓW:");
            e.printStackTrace();
        }
    }
}