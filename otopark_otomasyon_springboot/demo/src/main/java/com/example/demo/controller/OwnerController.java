package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Owner;
import com.example.demo.service.OwnerService;

@RestController
@RequestMapping("/api/owners")
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class OwnerController {
    @Autowired
    private OwnerService ownerService;

    @GetMapping
    public List<Owner> getAllOwners() {
        return ownerService.getAllOwners();
    }

    @PostMapping
    public Owner setCarNumber(@RequestParam String companyname, @RequestParam Integer capasity) {
        return ownerService.setCarNumber(companyname,capasity);
    }
    
}
