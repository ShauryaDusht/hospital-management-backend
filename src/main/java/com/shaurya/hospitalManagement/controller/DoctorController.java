package com.shaurya.hospitalManagement.controller;

import com.shaurya.hospitalManagement.dto.AppointmentResponseDto;
import com.shaurya.hospitalManagement.entity.User;
import com.shaurya.hospitalManagement.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final AppointmentService appointmentService;

    @GetMapping("/appointments")
    public ResponseEntity<List<AppointmentResponseDto>> getAllAppointmentsOfDoctor() {
        // the logged in doctor can only see his own appointments
        User user = (User) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();

        if(user != null) {
            return ResponseEntity.ok(appointmentService.getAllAppointmentsOfDoctor(user.getId()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
