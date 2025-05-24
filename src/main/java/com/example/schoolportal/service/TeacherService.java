package com.example.schoolportal.service;

import com.example.schoolportal.model.Assignment;
import com.example.schoolportal.model.Classroom;
import com.example.schoolportal.model.Submission;
import com.example.schoolportal.model.Teacher;
import com.example.schoolportal.repository.AssignmentRepository;
import com.example.schoolportal.repository.ClassroomRepository;
import com.example.schoolportal.repository.SubmissionRepository;
import com.example.schoolportal.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final TeacherRepository teacherRepo;
    private final ClassroomRepository classroomRepo;
    private final AssignmentRepository assignmentRepo;
    private final SubmissionRepository submissionRepo;

    public Teacher getTeacherByEmail(String email) {
        return teacherRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
    }

    public Classroom createClassroom(String teacherEmail, Classroom classroom) {
        Teacher teacher = getTeacherByEmail(teacherEmail);
        classroom.setTeacher(teacher);
        return classroomRepo.save(classroom);
    }

    public Classroom getClassroom(Long id) {
        return classroomRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Classroom not found"));
    }

    public Assignment uploadAssignment(Long classroomId, Assignment assignment) {
        Classroom classroom = getClassroom(classroomId);
        assignment.setClassroom(classroom);
        return assignmentRepo.save(assignment);
    }

    public List<Assignment> getAssignmentsByClassroom(Long classroomId) {
        Classroom classroom = getClassroom(classroomId);
        return assignmentRepo.findByClassroom(classroom);
    }

    public Submission getSubmission(Long id) {
        return submissionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Submission not found"));
    }

    public Submission evaluateSubmission(Long id, Integer marks, String feedback) {
        Submission submission = getSubmission(id);
        submission.setMarks(marks);
        submission.setFeedback(feedback);
        return submissionRepo.save(submission);
    }
}
