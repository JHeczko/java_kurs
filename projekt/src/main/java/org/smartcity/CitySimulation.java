package org.smartcity;

import java.util.Random;

public class CitySimulation {
    public static void main(String[] args) {
        SmartCity city = new SmartCity();

        // Symulacja trwa przez 10 "dni"
        for (int day = 1; day <= 10; day++) {
            System.out.println("Day " + day + " in Smart City");

            // 1. TODO Losowe tworzenie nowych budynków
            // Losowo zadecyduj, czy dodać nowy budynek do miasta - jeśli ma byc dodany
            // Użyj funkcji createRandomBuilding(day) i wypisz:
            // System.out.println("New building added: " + newBuilding.getAddress());

            // 2. TODO Operowanie na wszystkich budynkach w osobnych wątkach
            // Ilość wątków powinna być równa ilości budynków w mieście
            // Każdy wątek ma tylko jedno zadanie - wykonać funkcję operate() na danym budynku
            // Możesz wykorzystać klasę Runnable lub Thread do realizacji tego zadania
            // Pamietaj, żeby upewnić się, że wszystkie wątki się zakończyły przed przejściem do kolejnego dnia

        }
    }

    private static Building createRandomBuilding(int day) {
        Random random = new Random();
        int buildingType = random.nextInt(3);
        String address = "Building " + day + "-" + (random.nextInt(100) + 1);

        switch (buildingType) {
            case 0:
                return new Apartment(address, random.nextInt(10) + 5, random.nextInt(100) + 1);
            case 1:
                return new Office(address, random.nextInt(20) + 10, random.nextInt(300) + 50);
            case 2:
                return new Shop(address, random.nextInt(5) + 1, "Type " + (random.nextInt(3) + 1));
            default:
                return new Apartment(address, random.nextInt(10) + 5, random.nextInt(100) + 1);
        }
    }
}
