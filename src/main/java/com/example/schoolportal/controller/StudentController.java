package com.example.schoolportal.controller;

import com.example.schoolportal.model.Assignment;
import com.example.schoolportal.model.Classroom;
import com.example.schoolportal.model.Student;
import com.example.schoolportal.model.Submission;
import com.example.schoolportal.repository.AssignmentRepository;
import com.example.schoolportal.repository.ClassroomRepository;
import com.example.schoolportal.repository.StudentRepository;
import com.example.schoolportal.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/student")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STUDENT')")
public class StudentController {

    private final StudentRepository studentRepo;
    private final SubmissionRepository submissionRepo;
    private final AssignmentRepository assignmentRepo;

    @GetMapping("/classroom")
    public ResponseEntity<Classroom> getAssignedClassroom(Authentication authentication) {
        String email = authentication.getName();
        Student student = studentRepo.findByEmail(email).orElseThrow();
        return ResponseEntity.ok(student.getClassroom());
    }

    @GetMapping("/classroom/assignments")
    public ResponseEntity<List<Assignment>> getAssignments(Authentication authentication) {
        String email = authentication.getName();
        Student student = studentRepo.findByEmail(email).orElseThrow();
        List<Assignment> assignments = assignmentRepo.findByClassroom(student.getClassroom());
        return ResponseEntity.ok(assignments);
    }

    @PostMapping("/assignment/{id}/submit")
    public ResponseEntity<Submission> submitAssignment(@PathVariable Long id,
                                                       @RequestBody List<String> photoUrls,  // URLs or file paths
                                                       Authentication authentication) {
        String email = authentication.getName();
        Student student = studentRepo.findByEmail(email).orElseThrow();

        Submission submission = new Submission();
        submission.setAssignment(assignmentRepo.findById(id).orElseThrow());
        submission.setStudent(student);
        submission.setPhotoUrls(photoUrls);
        // submittedDate will be handled by @CreationTimestamp
        Submission saved = submissionRepo.save(submission);

        return ResponseEntity.ok(saved);
    }

    @GetMapping("/submissions")
    public ResponseEntity<List<Submission>> getSubmissions(Authentication authentication) {
        String email = authentication.getName();
        Student student = studentRepo.findByEmail(email).orElseThrow();
        List<Submission> submissions = submissionRepo.findByStudent(student);
        return ResponseEntity.ok(submissions);
    }
}
