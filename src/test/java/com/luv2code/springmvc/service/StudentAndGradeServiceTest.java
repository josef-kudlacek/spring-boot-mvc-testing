package com.luv2code.springmvc.service;

import com.luv2code.springmvc.models.*;
import com.luv2code.springmvc.repository.HistoryGradeDao;
import com.luv2code.springmvc.repository.MathGradeDao;
import com.luv2code.springmvc.repository.ScienceGradeDao;
import com.luv2code.springmvc.repository.StudentDao;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource("/application-test.properties")
@SpringBootTest
public class StudentAndGradeServiceTest {

    @Autowired
    private StudentAndGradeService studentAndGradeService;

    @Autowired
    private StudentDao studentDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private MathGradeDao mathGradeDao;

    @Autowired
    private ScienceGradeDao scienceGradeDao;

    @Autowired
    private HistoryGradeDao historyGradeDao;

    @Value("${sql.script.create.student}")
    private String sqlAddStudent;

    @Value("${sql.script.create.math.grade}")
    private String sqlAddMathGrade;

    @Value("${sql.script.create.science.grade}")
    private String sqlAddScienceGrade;

    @Value("${sql.script.create.history.grade}")
    private String sqlAddHistoryGrade;

    @Value("${sql.script.delete.student}")
    private String sqlDeleteStudent;

    @Value("${sql.script.delete.math.grade}")
    private String sqlDeleteMathGrade;

    @Value("${sql.script.delete.science.grade}")
    private String sqlDeleteScienceGrade;

    @Value("${sql.script.delete.history.grade}")
    private String sqlDeleteHistoryGrade;

    @BeforeEach
    public void setUpDatabase() {
        jdbcTemplate.execute(sqlAddStudent);
        jdbcTemplate.execute(sqlAddMathGrade);
        jdbcTemplate.execute(sqlAddScienceGrade);
        jdbcTemplate.execute(sqlAddHistoryGrade);
    }

    @AfterEach
    public void deleteData() {
        jdbcTemplate.execute(sqlDeleteStudent);
        jdbcTemplate.execute(sqlDeleteMathGrade);
        jdbcTemplate.execute(sqlDeleteScienceGrade);
        jdbcTemplate.execute(sqlDeleteHistoryGrade);
    }

    @Test
    public void createStudentService() {
        studentAndGradeService.createStudent("Joseph", "Kudlacek", "joseph.kudlacek@gmail.com");

        CollegeStudent collegeStudent = studentDao.findByEmailAddress("joseph.kudlacek@gmail.com");

        assertEquals("joseph.kudlacek@gmail.com", collegeStudent.getEmailAddress(), "find by email is not working");
    }

    @Test
    public void isStudentNullCheck() {
        assertTrue(studentAndGradeService.checkIfStudentIsNull(1), "Student should be found by id");
        assertFalse(studentAndGradeService.checkIfStudentIsNull(0), "Student should not be found by id");
    }

    @Test
    public void deleteStudentService() {
        Optional<CollegeStudent> collegeStudentOptional = studentDao.findById(1);
        Optional<MathGrade> deletedMathGrade = mathGradeDao.findById(1);
        Optional<HistoryGrade> deletedHistoryGrade = historyGradeDao.findById(1);
        Optional<ScienceGrade> deletedScienceGrade = scienceGradeDao.findById(1);

        assertTrue(collegeStudentOptional.isPresent(), "Student has to be found");
        assertTrue(deletedMathGrade.isPresent(), "Math grade has to be found");
        assertTrue(deletedHistoryGrade.isPresent(), "History grade has to be found");
        assertTrue(deletedScienceGrade.isPresent(), "Science grade has to be found");


        studentAndGradeService.deleteStudent(collegeStudentOptional.get().getId());

        collegeStudentOptional = studentDao.findById(collegeStudentOptional.get().getId());
        deletedMathGrade = mathGradeDao.findById(1);
        deletedHistoryGrade = historyGradeDao.findById(1);
        deletedScienceGrade = scienceGradeDao.findById(1);

        assertFalse(collegeStudentOptional.isPresent(), "Student should be deleted and not found");
        assertFalse(deletedMathGrade.isPresent(), "Math grade should be deleted and not found");
        assertFalse(deletedHistoryGrade.isPresent(), "History grade should be deleted and not found");
        assertFalse(deletedScienceGrade.isPresent(), "Science grade should be deleted and not found");
    }

    @Sql("/insertData.sql")
    @Test
    public void getGradebookService() {
        Iterable<CollegeStudent> iterableCollegeStudents = studentAndGradeService.getGradebook();

        List<CollegeStudent> collegeStudents = new ArrayList<>();
        for (CollegeStudent collegeStudent : iterableCollegeStudents) {
            collegeStudents.add(collegeStudent);
        }

        assertEquals(5, collegeStudents.size());
    }

    @Test
    public void createGradeService() {
        // Create the grade
        assertTrue(studentAndGradeService.createGrade(80.50, 1, "math"));
        assertTrue(studentAndGradeService.createGrade(90.25, 1, "science"));
        assertTrue(studentAndGradeService.createGrade(60.75, 1, "history"));

        // Get all grades with studentId
        Iterable<MathGrade> mathGrades = mathGradeDao.findGradeByStudentId(1);
        Iterable<ScienceGrade> scienceGrades = scienceGradeDao.findGradeByStudentId(1);
        Iterable<HistoryGrade> historyGrades = historyGradeDao.findGradeByStudentId(1);

        // Verify there is grades
        assertTrue(((Collection<MathGrade>) mathGrades).size() == 2, "Student has two math grades");
        assertTrue(((Collection<ScienceGrade>) scienceGrades).size() == 2, "Student has two science grades");
        assertTrue(((Collection<HistoryGrade>) historyGrades).size() == 2, "Student has two history grades");
    }

    @Test
    public void createGradeServiceReturnFalse() {
        assertFalse(studentAndGradeService.createGrade(105, 1, "math"));
        assertFalse(studentAndGradeService.createGrade(-5, 1, "math"));
        assertFalse(studentAndGradeService.createGrade(80.50, 2, "math"));
        assertFalse(studentAndGradeService.createGrade(80.50, 2, "literature"));
    }

    @Test
    public void deleteGradeService() {
        assertEquals(1, studentAndGradeService.deleteGrade(1, "math"), "Returns student id after delete math grade");
        assertEquals(1, studentAndGradeService.deleteGrade(1, "science"), "Returns student id after delete science grade");
        assertEquals(1, studentAndGradeService.deleteGrade(1, "history"), "Returns student id after delete history grade");
    }

    @Test
    public void deleteGradeServiceReturnStudentIdIsZero() {
        assertEquals(0, studentAndGradeService.deleteGrade(0, "science"), "No students should have 0 id");
        assertEquals(0, studentAndGradeService.deleteGrade(1, "literature"), "No student should have a literature class");
    }

    @Test
    public void studentInformation() {
        GradebookCollegeStudent gradebookCollegeStudent = studentAndGradeService.studentInformation(1);

        assertNotNull(gradebookCollegeStudent);
        assertEquals(1, gradebookCollegeStudent.getId());
        assertEquals("Karel", gradebookCollegeStudent.getFirstname());
        assertEquals("Macha", gradebookCollegeStudent.getLastname());
        assertEquals("karel.macha@gmail.com", gradebookCollegeStudent.getEmailAddress());
        assertTrue(gradebookCollegeStudent.getStudentGrades().getMathGradeResults().size() == 1);
        assertTrue(gradebookCollegeStudent.getStudentGrades().getScienceGradeResults().size() == 1);
        assertTrue(gradebookCollegeStudent.getStudentGrades().getHistoryGradeResults().size() == 1);
    }

    @Test
    public void studentInformationServiceReturnNull() {
        GradebookCollegeStudent gradebookCollegeStudent = studentAndGradeService.studentInformation(0);

        assertNull(gradebookCollegeStudent, "Student should not to be found with 0 id");
    }
}
