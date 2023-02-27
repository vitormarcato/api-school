package br.com.alura.school.enrollment;

import br.com.alura.school.course.Course;
import br.com.alura.school.course.CourseRepository;
import br.com.alura.school.user.User;
import br.com.alura.school.user.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;

import static java.lang.String.format;
import static org.springframework.http.HttpStatus.*;

@RestController
class EnrollmentController {

    private final CourseRepository courseRepository;

    private final UserRepository userRepository;

    private final EnrollmentRepository enrollmentRepository;

    EnrollmentController(CourseRepository courseRepository, UserRepository userRepository, EnrollmentRepository enrollmentRepository) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    @PostMapping("/courses/{courseCode}/enroll")
    ResponseEntity newEnrollment(@PathVariable("courseCode") String courseCode, @RequestBody @Valid NewEnrollmentRequest newEnrollmentRequest) {
        User user = userRepository.findByUsername(newEnrollmentRequest.getUsername()).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, format("User with username %s not found", newEnrollmentRequest.getUsername())));
        Course course = courseRepository.findByCode(courseCode).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, format("Course with code %s not found", courseCode)));

        if (enrollmentRepository.existsByCourseIdAndUserId(course.getId(), user.getId())) {
            throw new ResponseStatusException(BAD_REQUEST, format("User with username %s already enrolled", newEnrollmentRequest.getUsername()));
        }
        Enrollment enrollment = new Enrollment(course, user);
        enrollmentRepository.save(enrollment);

        return ResponseEntity.created(null).build();
    }

    @GetMapping("/courses/enroll/report")
    ResponseEntity<List<EnrollmentResponse>> allEnrollments() {

        List<EnrollmentResponse> enrolments = enrollmentRepository.generateEnrollmentsReport();

        if (enrolments.isEmpty()) {
            throw new ResponseStatusException(NO_CONTENT, "no enrollment");
        }

        return ResponseEntity.ok(enrolments);
    }
}
