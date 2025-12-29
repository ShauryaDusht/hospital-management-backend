package com.restAPI.rest.service.impl;

import com.restAPI.rest.dto.AddStudentRequest;
import com.restAPI.rest.dto.StudentDto;
import com.restAPI.rest.entity.Student;
import com.restAPI.rest.repository.StudentRepository;
import com.restAPI.rest.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<StudentDto> getAllStudents(){
        List<Student> students = studentRepository.findAll();

        // Convert Student to StudentDto
        return students
                .stream()
//                .map(student -> new StudentDto(
//                    student.getId(),
//                    student.getName(),
//                    student.getEmail())
                .map(student -> modelMapper.map(student, StudentDto.class)
                ).toList();
    }

    @Override
    public StudentDto getStudentById(Long id) {
        Student student = studentRepository
                .findById(id).
                orElseThrow(() -> new IllegalArgumentException("Student not found with ID: " + id));

        return modelMapper.map(student, StudentDto.class);
    }

    @Override
    public StudentDto addNewStudent(AddStudentRequest addStudentRequest) {
        Student newStudent = modelMapper.map(addStudentRequest, Student.class);
        Student student = studentRepository.save(newStudent);
        return modelMapper.map(student, StudentDto.class);
    }

    @Override
    public void deleteStudentById(Long id) {
        if(!studentRepository.existsById(id)){
            throw new IllegalArgumentException("Student not found with ID: " + id);
        }
        studentRepository.deleteById(id);
    }

}
