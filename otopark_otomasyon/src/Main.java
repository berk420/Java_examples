import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class Car {
    private String licensePlate;
    private String brand;
    private String model;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;

    public Car(String licensePlate, String brand, String model) {
        this.licensePlate = licensePlate;
        this.brand = brand;
        this.model = model;
        this.entryTime = LocalDateTime.now();
        this.exitTime = null; // Araç henüz çıkış yapmadı
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public LocalDateTime getExitTime() {
        return exitTime;
    }

    public void setExitTime(LocalDateTime exitTime) {
        this.exitTime = exitTime;
    }

    public long getParkDurationInMinutes() {
        LocalDateTime endTime = (exitTime != null) ? exitTime : LocalDateTime.now();
        return java.time.Duration.between(entryTime, endTime).toMinutes();
    }
}

interface ParkingLot {
    void addCar(String licensePlate, String brand, String model);
    void exitCar(String licensePlate);
    void listCars();
    Optional<Car> searchCar(String licensePlate);
    void calculateFee(String licensePlate);
}

class ParkingLotSystem implements ParkingLot {
    private List<Car> cars;
    private int capacity;

    public ParkingLotSystem(int capacity) {
        this.cars = new ArrayList<>();
        this.capacity = capacity;
    }

    @Override
    public Optional<Car> searchCar(String licensePlate) {
        return cars.stream()
                .filter(car -> car.getLicensePlate().equals(licensePlate))
                .findFirst();
    }

    @Override
    public void addCar(String licensePlate, String brand, String model) {
        if (cars.size() >= capacity) {
            System.out.println("Otopark dolu, yeni araç kabul edilemiyor.");
        } else {
            Car car = new Car(licensePlate, brand, model);
            cars.add(car);
            System.out.println(licensePlate + " plakalı " + brand + " " + model + " otoparka eklendi.");
        }
    }

    @Override
    public void exitCar(String licensePlate) {
        Optional<Car> carOptional = searchCar(licensePlate);
        if (carOptional.isPresent()) {
            Car car = carOptional.get();
            car.setExitTime(LocalDateTime.now()); // Çıkış zamanı ayarlandı
            System.out.println(licensePlate + " plakalı araç otoparktan çıkarıldı. Çıkış Zamanı: " + car.getExitTime());
            cars.remove(car);
            capacity++;
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
            for (Car car : cars) {
                String exitTime = (car.getExitTime() != null) ? car.getExitTime().toString() : "Henüz çıkış yapılmadı";
                long duration = car.getParkDurationInMinutes();

                System.out.println(car.getLicensePlate() + " - " + car.getBrand() + " " + car.getModel() + 
                                   " - Giriş Saati: " + car.getEntryTime() + 
                                   ", Çıkış Saati: " + exitTime + 
                                   ", Otoparkta Kalma Süresi: " + duration + " dakika");
            }
        }
    }

    @Override
    public void calculateFee(String licensePlate) {
        Optional<Car> carOptional = searchCar(licensePlate);
        if (carOptional.isPresent()) {
            Car car = carOptional.get();
            long minutesParked = car.getParkDurationInMinutes();
            double fee = calculateParkingFee(minutesParked);
            System.out.println(licensePlate + " plakalı araç için park ücreti: " + fee + " TL");
        } else {
            System.out.println(licensePlate + " plakalı araç otoparkta bulunamadı.");
        }
    }

    private double calculateParkingFee(long minutesParked) {
        double firstEntry = 95.0; 
        double hourlyRate = 35.0;
        double hoursParked = Math.ceil(minutesParked / 60.0);
        return ((hoursParked - 1) * hourlyRate) + firstEntry;
    }
}

public class Main {
    public static void main(String[] args) {
        ParkingLot parkingLotSystem = new ParkingLotSystem(2);

        parkingLotSystem.addCar("34ABC123", "Toyota", "Corolla");

        try {
            Thread.sleep(70 * 1000); // 10 saniye bekle
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        parkingLotSystem.addCar("35XYZ789", "Honda", "Civic");
        parkingLotSystem.addCar("06DEF456", "Ford", "Focus");

        System.out.println("\nParktaki araçlar (eklemeden sonra):");
        parkingLotSystem.listCars();

        parkingLotSystem.exitCar("34ABC123");

        System.out.println("\nParktaki araçlar (çıkıştan sonra):");
        parkingLotSystem.listCars();

        parkingLotSystem.calculateFee("35XYZ789");

        Optional<Car> searchedCar = parkingLotSystem.searchCar("35XYZ789");
        if (searchedCar.isPresent()) {
            System.out.println("\nAranan araç bulundu: " + searchedCar.get().getLicensePlate() + 
                               " - " + searchedCar.get().getBrand() + " " + searchedCar.get().getModel());
        } else {
            System.out.println("\nAranan araç bulunamadı.");
        }
    }
}
