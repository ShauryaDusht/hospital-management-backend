package com.restAPI.rest.controller;

import com.restAPI.rest.dto.AddStudentRequest;
import com.restAPI.rest.dto.StudentDto;
import com.restAPI.rest.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;

    @GetMapping
    public ResponseEntity<List<StudentDto>> getAllStudents() {
        return ResponseEntity.status(HttpStatus.OK).body(studentService.getAllStudents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentDto> getStudent(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(studentService.getStudentById(id));
    }

    @PostMapping
    public ResponseEntity<StudentDto> createNewStudent(@RequestBody @Valid AddStudentRequest addStudentRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(studentService.addNewStudent(addStudentRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudentById(@RequestBody Long id){
        studentService.deleteStudentById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }
}
