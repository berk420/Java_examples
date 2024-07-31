package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.demo.model.Car;
import com.example.demo.model.User;
import com.example.demo.service.ParkingLotService;

@SpringBootTest
public class DemoApplicationTests {

    @Autowired
    private ParkingLotService parkingLotService;

    @Test
    void testCarBeanLifecycle() {
        // Step 1: Create a User object to associate with the Car
        User user = new User();
        user.setEmail("test@example.com");
        user.setName("Test User");
        user.setlicense_plate("TEST123");

        // Assuming you have a method in ParkingLotService to save a user

        // Step 2: Create and save a Car object
        Car car = new Car("TEST123", "TestBrand", "TestModel", user);
        parkingLotService.save(car);

        // Step 3: Verify that the Car bean is created and logged correctly
        assertNotNull(car.getId(), "Car ID should not be null");
        // You can add more assertions to verify the state of the Car object if needed
    }
}
