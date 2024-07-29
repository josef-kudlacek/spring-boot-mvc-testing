package com.luv2code.springmvc.service;

import com.luv2code.springmvc.models.CollegeStudent;
import com.luv2code.springmvc.models.HistoryGrade;
import com.luv2code.springmvc.models.MathGrade;
import com.luv2code.springmvc.models.ScienceGrade;
import com.luv2code.springmvc.repository.HistoryGradeDao;
import com.luv2code.springmvc.repository.MathGradeDao;
import com.luv2code.springmvc.repository.ScienceGradeDao;
import com.luv2code.springmvc.repository.StudentDao;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource("/application.properties")
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

    @BeforeEach
    public void setUpDatabase() {
        jdbcTemplate.execute("""
                INSERT INTO STUDENT(ID, FIRSTNAME, LASTNAME, EMAIL_ADDRESS)
                VALUES (1, 'Karel', 'Macha', 'karel.macha@gmail.com')
                """);

        jdbcTemplate.execute("INSERT INTO MATH_GRADE(ID, STUDENT_ID, GRADE) VALUES (1, 1, 100.00)");
        jdbcTemplate.execute("INSERT INTO SCIENCE_GRADE(ID, STUDENT_ID, GRADE) VALUES (1, 1, 100.00)");
        jdbcTemplate.execute("INSERT INTO HISTORY_GRADE(ID, STUDENT_ID, GRADE) VALUES (1, 1, 100.00)");
    }

    @AfterEach
    public void deleteData() {
        jdbcTemplate.execute("DELETE FROM student");
        jdbcTemplate.execute("DELETE FROM MATH_GRADE");
        jdbcTemplate.execute("DELETE FROM SCIENCE_GRADE");
        jdbcTemplate.execute("DELETE FROM HISTORY_GRADE");
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
}
