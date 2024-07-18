import java.util.ArrayList;
import java.util.List;

// Otopark için soyut bir arayüz
interface ParkingLot {
    void addCar(String licensePlate);
    void removeCar(String licensePlate);
    void listCars();
}

// ParkingLot arayüzünü uygulayan somut bir sınıf
class ParkingLotSystem implements ParkingLot {

    private List<String> cars;

    public ParkingLotSystem() {
        this.cars = new ArrayList<>();
    }

    @Override
    public void addCar(String licensePlate) {
        cars.add(licensePlate);
        System.out.println(licensePlate + " plakalı araç otoparka eklendi.");
    }

    @Override
    public void removeCar(String licensePlate) {
        if (cars.remove(licensePlate)) {
            System.out.println(licensePlate + " plakalı araç otoparktan çıkarıldı.");
        } else {
            System.out.println(licensePlate + " plakalı araç otoparkta bulunamadı.");
        }
    }

    @Override
    public void listCars() {
        if (cars.isEmpty()) {
            System.out.println("Otoparkta araç bulunmamaktadır.");
        } else {
            System.out.println("Otoparktaki araçlar:");
            for (String car : cars) {
                System.out.println(car);
            }
        }
    }
}

public class Main {
    public static void main(String[] args) {
        ParkingLot parkingLotSystem = new ParkingLotSystem();

        // Araç girişleri
        parkingLotSystem.addCar("34ABC123");
        parkingLotSystem.addCar("35XYZ789");

        // Parktaki araçları listeleme
        System.out.println("\nParktaki araçlar (eklemeden sonra):");
        parkingLotSystem.listCars();

        // Araç çıkışı
        parkingLotSystem.removeCar("34ABC123");

        // Parktaki araçları listeleme
        System.out.println("\nParktaki araçlar (çıkıştan sonra):");
        parkingLotSystem.listCars();
    }
}
