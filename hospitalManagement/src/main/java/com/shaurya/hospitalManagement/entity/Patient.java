package com.shaurya.hospitalManagement.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.ToString;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;

@Entity
@ToString
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    @ToString.Exclude
    private LocalDate birthDate;
    private String email;
    private String gender;
}
