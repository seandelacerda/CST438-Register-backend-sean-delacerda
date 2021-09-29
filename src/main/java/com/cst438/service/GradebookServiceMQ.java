package com.cst438.service;


import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import com.cst438.domain.CourseDTOG;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentDTO;
import com.cst438.domain.EnrollmentRepository;


public class GradebookServiceMQ extends GradebookService {
	
	@Autowired
	RabbitTemplate rabbitTemplate;
	
	@Autowired
	EnrollmentRepository enrollmentRepository;
	
	@Autowired
	Queue gradebookQueue;
	
	
	public GradebookServiceMQ() {
		System.out.println("MQ grade book service");
	}
	
	// send message to grade book service about new student enrollment in course
	@Override
	public void enrollStudent(String student_email, String student_name, int course_id) {
		 
	   EnrollmentDTO enrollDTO = new EnrollmentDTO(student_email, student_name, course_id);
      this.rabbitTemplate.convertAndSend(gradebookQueue.getName(), enrollDTO);
		
	}
	
	@RabbitListener(queues = "registration-queue")
	@Transactional
	public void receive(CourseDTOG courseDTOG) {
		
	   for(CourseDTOG.GradeDTO g : courseDTOG.grades) {
         Enrollment tempEnrollment = enrollmentRepository.findByEmailAndCourseId(g.student_email, courseDTOG.course_id);
         if(tempEnrollment != null) {
            tempEnrollment.setCourseGrade(g.grade);
         } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Could not find enrollment record.");
         }
      }
		
	}
	
	

}
