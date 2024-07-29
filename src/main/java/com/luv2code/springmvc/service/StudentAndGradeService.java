package com.luv2code.springmvc.service;

import com.luv2code.springmvc.models.*;
import com.luv2code.springmvc.repository.HistoryGradeDao;
import com.luv2code.springmvc.repository.MathGradeDao;
import com.luv2code.springmvc.repository.ScienceGradeDao;
import com.luv2code.springmvc.repository.StudentDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class StudentAndGradeService {

    @Autowired
    private StudentDao studentDao;

    @Autowired
    @Qualifier("mathGrades")
    private MathGrade mathGrade;

    @Autowired
    private MathGradeDao mathGradeDao;

    @Autowired
    @Qualifier("scienceGrades")
    private ScienceGrade scienceGrade;

    @Autowired
    private ScienceGradeDao scienceGradeDao;

    @Autowired
    @Qualifier("historyGrades")
    private HistoryGrade historyGrade;

    @Autowired
    private HistoryGradeDao historyGradeDao;

    @Autowired
    private StudentGrades studentGrades;

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
            mathGradeDao.deleteByStudentId(id);
            scienceGradeDao.deleteByStudentId(id);
            historyGradeDao.deleteByStudentId(id);
        }
    }

    public Iterable<CollegeStudent> getGradebook() {
        Iterable<CollegeStudent> collegeStudents = studentDao.findAll();

        return collegeStudents;
    }

    public boolean createGrade(double grade, int studentId, String gradeType) {
        if (!checkIfStudentIsNull(studentId)) {
            return false;
        }

        if (grade >= 0 && grade <= 100) {
            if (gradeType.equals("math")) {
                mathGrade.setId(0);
                mathGrade.setGrade(grade);
                mathGrade.setStudentId(studentId);
                mathGradeDao.save(mathGrade);
                return true;
            }

            if (gradeType.equals("science")) {
                scienceGrade.setId(0);
                scienceGrade.setGrade(grade);
                scienceGrade.setStudentId(studentId);
                scienceGradeDao.save(scienceGrade);
                return true;
            }

            if (gradeType.equals("history")) {
                historyGrade.setId(0);
                historyGrade.setGrade(grade);
                historyGrade.setStudentId(studentId);
                historyGradeDao.save(historyGrade);
                return true;
            }
        }

        return false;
    }

    public int deleteGrade(int gradeId, String gradeType) {
        int studentId = 0;

        if (gradeType.equals("math")) {
            Optional<MathGrade> mathGrade = mathGradeDao.findById(gradeId);
            if (!mathGrade.isPresent()) {
                return studentId;
            }
            studentId = mathGrade.get().getStudentId();
            mathGradeDao.deleteById(gradeId);
        }

        if (gradeType.equals("science")) {
            Optional<ScienceGrade> scienceGrade = scienceGradeDao.findById(gradeId);
            if (!scienceGrade.isPresent()) {
                return studentId;
            }
            studentId = scienceGrade.get().getStudentId();
            scienceGradeDao.deleteById(gradeId);
        }

        if (gradeType.equals("history")) {
            Optional<HistoryGrade> historyGrade = historyGradeDao.findById(gradeId);
            if (!historyGrade.isPresent()) {
                return studentId;
            }
            studentId = historyGrade.get().getStudentId();
            historyGradeDao.deleteById(gradeId);
        }

        return studentId;
    }

    public GradebookCollegeStudent studentInformation(int studentId) {
        if (!checkIfStudentIsNull(studentId)) {
            return null;
        }

        CollegeStudent student = studentDao.findById(studentId).get();
        Iterable<MathGrade> mathGrades = mathGradeDao.findGradeByStudentId(studentId);
        Iterable<ScienceGrade> scienceGrades = scienceGradeDao.findGradeByStudentId(studentId);
        Iterable<HistoryGrade> historyGrades = historyGradeDao.findGradeByStudentId(studentId);

        List<Grade> mathGradesList = new ArrayList<>();
        mathGrades.forEach(mathGradesList::add);
        List<Grade> scienceGradesList = new ArrayList<>();
        scienceGrades.forEach(scienceGradesList::add);
        List<Grade> historyGradesList = new ArrayList<>();
        historyGrades.forEach(historyGradesList::add);

        studentGrades.setMathGradeResults(mathGradesList);
        studentGrades.setScienceGradeResults(scienceGradesList);
        studentGrades.setHistoryGradeResults(historyGradesList);

        return new GradebookCollegeStudent(
                studentId, student.getFirstname(), student.getLastname(), student.getEmailAddress(), studentGrades
        );
    }
}
