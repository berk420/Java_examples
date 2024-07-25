package com.example.demo.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private ParkingLotService parkingLotService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CarRepository carRepository;

    @GetMapping
    public List<Car> getAllCars() {
        return parkingLotService.getAllCars();
    }

    @GetMapping("/{licensePlate}")
    public ResponseEntity<Car> getCarByLicensePlate(@PathVariable String licensePlate) {
        Optional<Car> car = parkingLotService.getCarByLicensePlate(licensePlate);
        return car.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Car addCar(@RequestParam String licensePlate, @RequestParam String brand,
                      @RequestParam String model, @RequestParam String userName,
                      @RequestParam String userEmail) {
        return parkingLotService.addCar(licensePlate, brand, model, userName, userEmail);
    }

    @PutMapping("/{licensePlate}/exit")
    public ResponseEntity<Car> updateCarExitTime(@PathVariable String licensePlate) {
        try {
            Car car = parkingLotService.updateCarExitTime(licensePlate);
            return ResponseEntity.ok(car);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{licensePlate}")
    public ResponseEntity<Void> deleteCar(@PathVariable String licensePlate) {
        parkingLotService.deleteCar(licensePlate);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{licensePlate}/fee")
    public ResponseEntity<Double> calculateFee(@PathVariable String licensePlate) {
        try {
            double fee = parkingLotService.calculateFee(licensePlate);
            return ResponseEntity.ok(fee);
        } catch (RuntimeException e) {
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
            return ResponseEntity.ok(updatedCar);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
}
