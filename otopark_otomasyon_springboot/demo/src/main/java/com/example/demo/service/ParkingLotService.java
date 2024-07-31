package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Car;
import com.example.demo.model.User;
import com.example.demo.repository.CarRepository;
import com.example.demo.repository.UserRepository;

@Service
public class ParkingLotService {

    private static final Logger logger = LoggerFactory.getLogger(ParkingLotService.class);

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    public Optional<Car> getCarByLicensePlate(String licensePlate) {
        return carRepository.findByLicensePlate(licensePlate);
    }

    public Car save(Car car) {
        logger.info("Saving car: " + car.toString());
        return carRepository.save(car);
    }

    public Car addCar(String licensePlate, String brand, String model, String userName, String userEmail) {
        User user = userRepository.findByEmail(userEmail).orElseGet(() -> {
            User newUser = new User(); 
            newUser.setName(userName);
            newUser.setEmail(userEmail);
            return userRepository.save(newUser);
        });

        Car car = new Car(licensePlate, brand, model, user);
        car.setEntryTime(LocalDateTime.now());

        logger.info("Adding car: " + car.toString());
        System.out.println("test");
        return carRepository.save(car);
    }

    public Car updateCarExitTime(String licensePlate) {
        Optional<Car> carOptional = carRepository.findByLicensePlate(licensePlate);
        if (carOptional.isPresent()) {
            Car car = carOptional.get();
            car.setExitTime(LocalDateTime.now());
            logger.info("Updating exit time for car: " + car.toString());
            return carRepository.save(car);
        } else {
            throw new RuntimeException("Car not found");
        }
    }

    public void deleteCar(String licensePlate) {
        Optional<Car> carOptional = carRepository.findByLicensePlate(licensePlate);
        if (carOptional.isPresent()) {
            Car car = carOptional.get();
            logger.info("Deleting car: " + car.toString());
            carRepository.delete(car);
        }
    }

    public double calculateFee(String licensePlate) {
        Optional<Car> carOptional = carRepository.findByLicensePlate(licensePlate);
        if (carOptional.isPresent()) {
            Car car = carOptional.get();
            long hours = java.time.Duration.between(car.getEntryTime(), car.getExitTime()).toHours();
            return hours * 10;
        } else {
            throw new RuntimeException("Car not found");
        }
    }
}
