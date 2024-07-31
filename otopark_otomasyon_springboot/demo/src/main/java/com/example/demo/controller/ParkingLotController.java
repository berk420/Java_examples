package com.example.demo.controller;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Car;
import com.example.demo.model.User;
import com.example.demo.repository.CarRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.ParkingLotService;

@RestController
@RequestMapping("/api/cars")
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class ParkingLotController {

    private static final Logger logger = LoggerFactory.getLogger(ParkingLotController.class);

    @Autowired
    private ParkingLotService parkingLotService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CarRepository carRepository;

    @GetMapping
    public List<Car> getAllCars() {
        logger.info("Fetching all cars");
        return parkingLotService.getAllCars();
    }


    @Autowired
    public ParkingLotController(ParkingLotService parkingLotService){
        this.parkingLotService= parkingLotService;
    }

    @GetMapping("/{licensePlate}")
    public ResponseEntity<Car> getCarByLicensePlate(@PathVariable String licensePlate) {
        Optional<Car> car = parkingLotService.getCarByLicensePlate(licensePlate);
        if (car.isPresent()) {
            logger.info("Fetching car with license plate: " + licensePlate);
            return ResponseEntity.ok(car.get());
        } else {
            logger.warn("Car not found with license plate: " + licensePlate);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<String> addCar(@RequestParam String licensePlate, @RequestParam String brand,
                      @RequestParam String model, @RequestParam String userName,
                      @RequestParam String userEmail) {
        logger.info("Adding car with license plate: " + licensePlate);
        parkingLotService.addCar(licensePlate, brand, model, userName, userEmail);
        return new ResponseEntity<>("Car added successfully", HttpStatus.CREATED);
    }

    @PutMapping("/{licensePlate}/exit")
    public ResponseEntity<Car> updateCarExitTime(@PathVariable String licensePlate) {
        try {
            Car car = parkingLotService.updateCarExitTime(licensePlate);
            logger.info("Updated exit time for car with license plate: " + licensePlate);
            return ResponseEntity.ok(car);
        } catch (RuntimeException e) {
            logger.error("Error updating exit time for car with license plate: " + licensePlate, e);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{licensePlate}")
    public ResponseEntity<Void> deleteCar(@PathVariable String licensePlate) {
        logger.info("Deleting car with license plate: " + licensePlate);
        parkingLotService.deleteCar(licensePlate);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{licensePlate}/fee")
    public ResponseEntity<Double> calculateFee(@PathVariable String licensePlate) {
        try {
            double fee = parkingLotService.calculateFee(licensePlate);
            logger.info("Calculating fee for car with license plate: " + licensePlate);
            return ResponseEntity.ok(fee);
        } catch (RuntimeException e) {
            logger.error("Error calculating fee for car with license plate: " + licensePlate, e);
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{licensePlate}")
    public ResponseEntity<Car> updateCar(
            @PathVariable String licensePlate, 
            @RequestParam String brand, 
            @RequestParam String model, 
            @RequestParam String userName, 
            @RequestParam String userEmail) {

        Optional<Car> carOptional = parkingLotService.getCarByLicensePlate(licensePlate);
        if (carOptional.isPresent()) {
            Car car = carOptional.get();
            car.setBrand(brand);
            car.setModel(model);

            Optional<User> userOptional = userRepository.findByEmail(userEmail);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                user.setName(userName);
                userRepository.save(user);
                car.setUser(user);
            }

            Car updatedCar = carRepository.save(car);
            logger.info("Updated car with license plate: " + licensePlate);
            return ResponseEntity.ok(updatedCar);
        } else {
            logger.warn("Car not found with license plate: " + licensePlate);
            return ResponseEntity.notFound().build();
        }
    }
}
