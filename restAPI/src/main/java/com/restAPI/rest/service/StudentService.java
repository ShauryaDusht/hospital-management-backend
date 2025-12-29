package com.restAPI.rest.service;

import com.restAPI.rest.dto.AddStudentRequest;
import com.restAPI.rest.dto.StudentDto;

import java.util.List;

public interface StudentService {

    List<StudentDto> getAllStudents();

    StudentDto getStudentById(Long id);

    StudentDto addNewStudent(AddStudentRequest addStudentRequest);

    void deleteStudentById(Long id);
}
