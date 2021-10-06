package com.cst438;

import static org.mockito.ArgumentMatchers.any;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.cst438.controller.StudentController;
import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentRepository;
import com.cst438.domain.ScheduleDTO;
import com.cst438.domain.Student;
import com.cst438.domain.StudentRepository;
import com.cst438.service.GradebookService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.test.context.ContextConfiguration;

/* 
 * Example of using Junit with Mockito for mock objects
 *  the database repositories are mocked with test data.
 *  
 * Mockmvc is used to test a simulated REST call to the RestController
 * 
 * the http response and repository is verified.
 * 
 *   Note: This tests uses Junit 5.
 *  ContextConfiguration identifies the controller class to be tested
 *  addFilters=false turns off security.  (I could not get security to work in test environment.)
 *  WebMvcTest is needed for test environment to create Repository classes.
 */
@ContextConfiguration(classes = { StudentController.class })
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest
public class JunitTestStudent {

   static final String URL = "http://localhost:8080";
   public static final int TEST_COURSE_ID = 40442;
   public static final String TEST_STUDENT_EMAIL = "test@csumb.edu";
   public static final String TEST_STUDENT_NAME  = "test";

   @MockBean
   StudentRepository studentRepository;

   @Autowired
   private MockMvc mvc;

   @Test
   public void addStudent()  throws Exception {
      MockHttpServletResponse response;
      
      Student student = new Student();
      student.setEmail(TEST_STUDENT_EMAIL);
      student.setName(TEST_STUDENT_NAME);
      
      // given  -- stubs for database repositories that return test data
       given(studentRepository.findByEmail(TEST_STUDENT_EMAIL)).willReturn(student);
      
      response = mvc.perform(
            MockMvcRequestBuilders
            .post("/student?name=" + TEST_STUDENT_NAME + "&email=" + TEST_STUDENT_EMAIL)
            .accept(MediaType.APPLICATION_JSON))
         .andReturn().getResponse();
      
      // verify that return status = OK (value 200) 
      assertEquals(200, response.getStatus());
      
      boolean found = false;     
      
      Student stu = fromJsonString(response.getContentAsString(), Student.class);
      
      if ((stu.getEmail().equals(TEST_STUDENT_EMAIL)) &&
         (stu.getName().equals(TEST_STUDENT_NAME))) {
            found = true;
      }
      
      assertTrue("student created", found);
      
      verify(studentRepository).save(any(Student.class));
      verify(studentRepository, times(1)).findByEmail(TEST_STUDENT_EMAIL);
   }
   
   @Test
   public void addHold()  throws Exception {
      MockHttpServletResponse response;
      
      Student student = new Student();
      student.setEmail(TEST_STUDENT_EMAIL);
      student.setName(TEST_STUDENT_NAME);
      
      // given  -- stubs for database repositories that return test data
       given(studentRepository.findByEmail(TEST_STUDENT_EMAIL)).willReturn(student);
      
      response = mvc.perform(
            MockMvcRequestBuilders
            .post("/student?name=" + TEST_STUDENT_NAME + "&email=" + TEST_STUDENT_EMAIL)
            .accept(MediaType.APPLICATION_JSON))
         .andReturn().getResponse();
      
   // verify that return status = OK (value 200) 
      assertEquals(200, response.getStatus());
      
      boolean found = false;     
      
      Student stu = fromJsonString(response.getContentAsString(), Student.class);
      
      if ((stu.getEmail().equals(TEST_STUDENT_EMAIL)) &&
         (stu.getName().equals(TEST_STUDENT_NAME))) {
            found = true;
      }
      
      assertTrue("student created", found);
      
      verify(studentRepository).save(any(Student.class));
      
      response = mvc.perform(
            MockMvcRequestBuilders
               .post("/student/addhold?email=" + TEST_STUDENT_EMAIL)
               .accept(MediaType.APPLICATION_JSON))
            .andReturn().getResponse();
      
      // verify that return status = OK (value 200) 
      assertEquals(200, response.getStatus());
      
      Boolean holdAdded = false; 
      
      Student addedStu = fromJsonString(response.getContentAsString(), Student.class);
      
      if (addedStu.getStatusCode() == 1) {
         holdAdded = true;
      }
      
      assertTrue("hold added", holdAdded);

      verify(studentRepository, times(1)).findByEmail(TEST_STUDENT_EMAIL);
   }
   
   @Test
   public void removeHold()  throws Exception {
      MockHttpServletResponse response;
      
      Student student = new Student();
      student.setEmail(TEST_STUDENT_EMAIL);
      student.setName(TEST_STUDENT_NAME);
      
      // given  -- stubs for database repositories that return test data
       given(studentRepository.findByEmail(TEST_STUDENT_EMAIL)).willReturn(student);
      
      response = mvc.perform(
            MockMvcRequestBuilders
            .post("/student?name=" + TEST_STUDENT_NAME + "&email=" + TEST_STUDENT_EMAIL)
            .accept(MediaType.APPLICATION_JSON))
         .andReturn().getResponse();
      
   // verify that return status = OK (value 200) 
      assertEquals(200, response.getStatus());
      
      boolean found = false;     
      
      Student stu = fromJsonString(response.getContentAsString(), Student.class);
      
      if ((stu.getEmail().equals(TEST_STUDENT_EMAIL)) &&
         (stu.getName().equals(TEST_STUDENT_NAME))) {
            found = true;
      }
      
      assertTrue("student created", found);
      
      verify(studentRepository).save(any(Student.class));
      
      response = mvc.perform(
            MockMvcRequestBuilders
               .post("/student/addhold?email=" + TEST_STUDENT_EMAIL)
               .accept(MediaType.APPLICATION_JSON))
            .andReturn().getResponse();
      
      // verify that return status = OK (value 200) 
      assertEquals(200, response.getStatus());
      
      Boolean holdAdded = false; 
      
      Student addedStu = fromJsonString(response.getContentAsString(), Student.class);
      
      if (addedStu.getStatusCode() == 1) {
         holdAdded = true;
      }
      
      assertTrue("hold added", holdAdded);
      
      response = mvc.perform(
            MockMvcRequestBuilders
               .post("/student/removehold?email=" + TEST_STUDENT_EMAIL)
               .accept(MediaType.APPLICATION_JSON))
            .andReturn().getResponse();
      
      // verify that return status = OK (value 200) 
      assertEquals(200, response.getStatus());
      
      Boolean holdRemoved = false;  
      
      // verify that returned data contains the added course 
      Student removedStu = fromJsonString(response.getContentAsString(), Student.class);
      
      if (removedStu.getStatusCode() == 0) {
         holdRemoved = true;
      }
      
      assertTrue("hold removed", holdRemoved);

      verify(studentRepository, times(3)).findByEmail(TEST_STUDENT_EMAIL);
   }
      
   private static String asJsonString(final Object obj) {
      try {

         return new ObjectMapper().writeValueAsString(obj);
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }

   private static <T> T  fromJsonString(String str, Class<T> valueType ) {
      try {
         return new ObjectMapper().readValue(str, valueType);
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }

}
