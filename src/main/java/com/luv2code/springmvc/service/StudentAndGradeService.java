package com.luv2code.springmvc.service;

import com.luv2code.springmvc.models.CollegeStudent;
import com.luv2code.springmvc.repository.StudentDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
public class StudentAndGradeService {

    @Autowired
    private StudentDao studentDao;

    public void createStudent(String firstName, String lastname, String emailAddress) {
        CollegeStudent collegeStudent = new CollegeStudent(firstName, lastname, emailAddress);
        collegeStudent.setId(0);

        studentDao.save(collegeStudent);
    }

    public boolean checkIfStudentIsNull(int id) {
        Optional<CollegeStudent> optionalStudentDao = studentDao.findById(id);

        return optionalStudentDao.isPresent();
    }

    public void deleteStudent(int id) {
        if (checkIfStudentIsNull(id)) {
            studentDao.deleteById(id);
        }
    }

    public Iterable<CollegeStudent> getGradebook() {
        Iterable<CollegeStudent> collegeStudents = studentDao.findAll();

        return collegeStudents;
    }
}
