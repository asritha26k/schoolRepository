package com.example.schoolportal.service;

import com.example.schoolportal.model.Assignment;
import com.example.schoolportal.model.Classroom;
import com.example.schoolportal.model.Student;
import com.example.schoolportal.model.Submission;
import com.example.schoolportal.repository.AssignmentRepository;
import com.example.schoolportal.repository.StudentRepository;
import com.example.schoolportal.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepo;
    private final AssignmentRepository assignmentRepo;
    private final SubmissionRepository submissionRepo;

    public Student getStudentByEmail(String email) {
        return studentRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Student not found"));
    }

    public Classroom getAssignedClassroom(String studentEmail) {
        Student student = getStudentByEmail(studentEmail);
        return student.getClassroom();
    }

    public List<Assignment> getAssignmentsForStudent(String studentEmail) {
        Student student = getStudentByEmail(studentEmail);
        return assignmentRepo.findByClassroom(student.getClassroom());
    }

    public Submission submitAssignment(String studentEmail, Long assignmentId, List<String> photoUrls) {
        Student student = getStudentByEmail(studentEmail);
        Assignment assignment = assignmentRepo.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));

        Submission submission = new Submission();
        submission.setStudent(student);
        submission.setAssignment(assignment);
        submission.setPhotoUrls(photoUrls);

        return submissionRepo.save(submission);
    }

    public List<Submission> getSubmissions(String studentEmail) {
        Student student = getStudentByEmail(studentEmail);
        return submissionRepo.findByStudent(student);
    }
}
