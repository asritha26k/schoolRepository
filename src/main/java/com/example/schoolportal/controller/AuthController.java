package com.example.schoolportal.controller;

import com.example.schoolportal.dto.AuthRequest;
import com.example.schoolportal.dto.AuthResponse;
import com.example.schoolportal.dto.RegisterRequest;
import com.example.schoolportal.model.Role;
import com.example.schoolportal.model.Student;
import com.example.schoolportal.model.Teacher;
import com.example.schoolportal.repository.StudentRepository;
import com.example.schoolportal.repository.TeacherRepository;
import com.example.schoolportal.security.JwtUtil;
import com.example.schoolportal.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final TeacherRepository teacherRepo;
    private final StudentRepository studentRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final AuthService authService;

    // Register teacher
    @PostMapping("/register/teacher")
    public ResponseEntity<String> registerTeacher(@RequestBody RegisterRequest request) {
        if (teacherRepo.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        Teacher teacher = new Teacher();
        teacher.setName(request.getName());
        teacher.setEmail(request.getEmail());
        teacher.setPassword(passwordEncoder.encode(request.getPassword()));
        teacher.setRole(Role.ROLE_TEACHER);
        teacherRepo.save(teacher);

        return ResponseEntity.ok("Teacher registered successfully");
    }

    // Register student
    @PostMapping("/register/student")
    public ResponseEntity<String> registerStudent(@RequestBody RegisterRequest request) {
        if (studentRepo.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        Student student = new Student();
        student.setName(request.getName());
        student.setEmail(request.getEmail());
        student.setPassword(passwordEncoder.encode(request.getPassword()));
        student.setRole(Role.ROLE_STUDENT);
        studentRepo.save(student);

        return ResponseEntity.ok("Student registered successfully");
    }
    @PostMapping("/test-auth")
    public ResponseEntity<String> testAuthenticate(@RequestBody AuthRequest request) {
        try {
            String token = authService.authenticate(request);
            return ResponseEntity.ok(token);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed");
        }
    }


    // Login
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // Extract role from authenticated user's authorities
        String role = authentication.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("ROLE_USER");  // fallback role if none found

        // Generate JWT token with username and role
        String token = jwtUtil.generateToken(request.getEmail(), role);

        return ResponseEntity.ok(new AuthResponse(token));
    }

}

