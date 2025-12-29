package com.restAPI.rest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.restAPI.rest.entity.Student;
import org.springframework.stereotype.Repository;

// Contains all the query to be done in DB
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

}
