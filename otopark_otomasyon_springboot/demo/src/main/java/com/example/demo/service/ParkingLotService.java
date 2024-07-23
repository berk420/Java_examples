package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Car;
import com.example.demo.repository.CarRepository;

@Service
public class ParkingLotService {

    @Autowired
    private CarRepository carRepository;

    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    public Optional<Car> getCarByLicensePlate(String licensePlate) {
        return carRepository.findByLicensePlate(licensePlate);
    }

    public Car addCar(String licensePlate, String brand, String model) {
        Car car = new Car();
        car.setLicensePlate(licensePlate);
        car.setBrand(brand);
        car.setModel(model);
        car.setEntryTime(LocalDateTime.now());
        return carRepository.save(car);
    }

    public Car updateCarExitTime(String licensePlate) {
        Optional<Car> carOptional = carRepository.findByLicensePlate(licensePlate);
        if (carOptional.isPresent()) {
            Car car = carOptional.get();
            car.setExitTime(LocalDateTime.now());
            return carRepository.save(car);
        } else {
            throw new RuntimeException("Car not found");
        }
    }

    public void deleteCar(String licensePlate) {
        Optional<Car> carOptional = carRepository.findByLicensePlate(licensePlate);
        carOptional.ifPresent(carRepository::delete);
    }

    public double calculateFee(String licensePlate) {
        Optional<Car> carOptional = carRepository.findByLicensePlate(licensePlate);
        if (carOptional.isPresent()) {
            Car car = carOptional.get();
            // Ücret hesaplama mantığı
            // Örneğin, her saat başına 10 TL
            long hours = java.time.Duration.between(car.getEntryTime(), car.getExitTime()).toHours();
            return hours * 10;
        } else {
            throw new RuntimeException("Car not found");
        }
    }
}
