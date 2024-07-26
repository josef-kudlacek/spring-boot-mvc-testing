package com.luv2code.springmvc.controller;

import com.luv2code.springmvc.models.CollegeStudent;
import com.luv2code.springmvc.models.GradebookCollegeStudent;
import com.luv2code.springmvc.service.StudentAndGradeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.ModelAndViewAssert;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@TestPropertySource("/application.properties")
@AutoConfigureMockMvc
@SpringBootTest
class GradebookControllerTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private StudentAndGradeService studentAndGradeServiceMock;

    @BeforeEach
    public void beforeEach() {
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
    public void getStudentHttpRequest() throws Exception {
        CollegeStudent collegeStudentOne = new GradebookCollegeStudent("Eric", "Roby",
                "eric_roby@luv2code_school.com");
        CollegeStudent collegeStudentTwo = new GradebookCollegeStudent("Chad", "Darby",
                "chad_darby@luv2code_school.com");

        List<CollegeStudent> collegeStudentList = new ArrayList<>(Arrays.asList(collegeStudentOne, collegeStudentTwo));

        when(studentAndGradeServiceMock.getGradebook())
                .thenReturn(collegeStudentList);

        assertIterableEquals(collegeStudentList, studentAndGradeServiceMock.getGradebook());

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(status().isOk())
                .andReturn();

        ModelAndView modelAndView = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(modelAndView, "index");
    }

}