package com.example.demo.model;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


@Component
@Entity
@Table(name = "car")
public class Car implements InitializingBean, DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(Car.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "license_plate", nullable = false)
    private String licensePlate;

    @Column(name = "brand", nullable = false)
    private String brand;

    @Column(name = "model", nullable = false)
    private String model;

    @Column(name = "entry_time", nullable = false)
    private LocalDateTime entryTime;

    @Column(name = "exit_time")
    private LocalDateTime exitTime;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false) 
    private User user;

    // Constructors, getters, and setters

    public Car() {}

    public Car(String licensePlate, String brand, String model, User user) {
        this.licensePlate = licensePlate;
        this.brand = brand;
        this.model = model;
        this.entryTime = LocalDateTime.now();
        this.exitTime = null; 
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(LocalDateTime entryTime) {
        this.entryTime = entryTime;
    }

    public LocalDateTime getExitTime() {
        return exitTime;
    }

    public void setExitTime(LocalDateTime exitTime) {
        this.exitTime = exitTime;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public long getParkDurationInMinutes() {
        LocalDateTime endTime = (exitTime != null) ? exitTime : LocalDateTime.now();
        return java.time.Duration.between(entryTime, endTime).toMinutes();
    }

    @PostConstruct
    public void postConstruct() {
        logger.info("Car bean postConstruct: " + this.toString());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("Car bean afterPropertiesSet: " + this.toString());
    }

    @PreDestroy
    public void preDestroy() {
        logger.info("Car bean preDestroy: " + this.toString());
    }

    @Override
    public void destroy() throws Exception {
        logger.info("Car bean destroy: " + this.toString());
    }

    @Override
    public String toString() {
        return "Car{" +
                "licensePlate='" + licensePlate + '\'' +
                ", brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", user=" + user +
                '}';
    }
}
