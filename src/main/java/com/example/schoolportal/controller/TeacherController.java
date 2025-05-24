package com.example.schoolportal.controller;

import com.example.schoolportal.model.Assignment;
import com.example.schoolportal.model.Classroom;
import com.example.schoolportal.model.Submission;
import com.example.schoolportal.model.Teacher;
import com.example.schoolportal.repository.AssignmentRepository;
import com.example.schoolportal.repository.ClassroomRepository;
import com.example.schoolportal.repository.SubmissionRepository;
import com.example.schoolportal.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/teacher")
@RequiredArgsConstructor
@PreAuthorize("hasRole('TEACHER')")
public class TeacherController {

    private final TeacherRepository teacherRepo;
    private final ClassroomRepository classroomRepo;
    private final AssignmentRepository assignmentRepo;
    private final SubmissionRepository submissionRepo;

    @PostMapping("/classroom")
    public ResponseEntity<Classroom> createClassroom(Authentication authentication,
                                                     @RequestBody Classroom classroom) {
        String email = authentication.getName();
        Teacher teacher = teacherRepo.findByEmail(email).orElseThrow();
        classroom.setTeacher(teacher);
        Classroom savedClassroom = classroomRepo.save(classroom);
        return ResponseEntity.ok(savedClassroom);
    }

    @GetMapping("/classroom/{id}")
    public ResponseEntity<Classroom> getClassroomDetails(@PathVariable Long id, Authentication authentication) {
        // Optional: check if classroom belongs to logged-in teacher
        Classroom classroom = classroomRepo.findById(id).orElseThrow();
        return ResponseEntity.ok(classroom);
    }

    @PostMapping("/classroom/{id}/assignment")
    public ResponseEntity<Assignment> uploadAssignment(@PathVariable Long id,
                                                       @RequestBody Assignment assignment,
                                                       Authentication authentication) {
        Classroom classroom = classroomRepo.findById(id).orElseThrow();
        assignment.setClassroom(classroom);
        assignmentRepo.save(assignment);
        return ResponseEntity.ok(assignment);
    }

    @GetMapping("/classroom/{id}/assignments")
    public ResponseEntity<List<Assignment>> getAssignments(@PathVariable Long id) {
        Classroom classroom = classroomRepo.findById(id).orElseThrow();
        List<Assignment> assignments = assignmentRepo.findByClassroom(classroom);
        return ResponseEntity.ok(assignments);
    }

    @GetMapping("/submission/{id}")
    public ResponseEntity<Submission> getSubmission(@PathVariable Long id) {
        Submission submission = submissionRepo.findById(id).orElseThrow();
        return ResponseEntity.ok(submission);
    }

    @PostMapping("/submission/{id}/evaluate")
    public ResponseEntity<Submission> evaluateSubmission(@PathVariable Long id,
                                                         @RequestParam Integer marks,
                                                         @RequestParam String feedback) {
        Submission submission = submissionRepo.findById(id).orElseThrow();
        submission.setMarks(marks);
        submission.setFeedback(feedback);
        Submission updated = submissionRepo.save(submission);
        return ResponseEntity.ok(updated);
    }
}
