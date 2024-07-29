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
    }

    @AfterEach
    public void deleteData() {
        jdbcTemplate.execute("DELETE FROM student");
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

        assertTrue(collegeStudentOptional.isPresent(), "Student has to be found");

        studentAndGradeService.deleteStudent(collegeStudentOptional.get().getId());

        collegeStudentOptional = studentDao.findById(collegeStudentOptional.get().getId());

        assertFalse(collegeStudentOptional.isPresent(), "Student should be deleted and not found");
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
        assertTrue(mathGrades.iterator().hasNext(), "Student has math grades");
        assertTrue(scienceGrades.iterator().hasNext(), "Student has math grades");
        assertTrue(historyGrades.iterator().hasNext(), "Student has math grades");
    }
}
