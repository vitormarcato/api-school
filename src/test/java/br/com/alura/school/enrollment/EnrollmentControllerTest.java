package br.com.alura.school.enrollment;

import br.com.alura.school.course.Course;
import br.com.alura.school.course.CourseRepository;
import br.com.alura.school.user.User;
import br.com.alura.school.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "classpath:schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class EnrollmentControllerTest {

    private final ObjectMapper jsonMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Test
    void should_add_new_enrollment() throws Exception {
        enrollmentData();

        NewEnrollmentRequest newEnrollmentRequest = new NewEnrollmentRequest("bob");

        mockMvc.perform(post("/courses/spring-1/enroll")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(newEnrollmentRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    void should_add_new_enrollment_with_right_date() throws Exception {
        LocalDate date = LocalDate.now();
        enrollmentData();

        Course course = courseRepository.getOne(1L);
        User user = userRepository.getOne(1L);

        Enrollment enrollment = new Enrollment(course, user);

        Assertions.assertThat(enrollment.getEnrolmmentDate().isEqual(date));
    }

    @Test
    void bad_request_when_user_already_enrolled() throws Exception {
        enrollmentData();
        NewEnrollmentRequest newEnrollmentRequest = new NewEnrollmentRequest("alex");

        mockMvc.perform(post("/courses/spring-1/enroll")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(newEnrollmentRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_retrieve_enrollments_quantity_grouped_by_user_email() throws Exception {
        enrollmentData();
        mockMvc.perform(get("/courses/enroll/report")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()", is(2)))
                .andExpect(jsonPath("$[0].email", is("alex@email.com")))
                .andExpect(jsonPath("$[0].quantidade_matriculas", is(2)))
                .andExpect(jsonPath("$[1].email", is("ana@email.com")))
                .andExpect(jsonPath("$[1].quantidade_matriculas", is(1)));
    }

    @Test
    void no_content_when_no_enrolled_users() throws Exception {
        mockMvc.perform(get("/courses/enroll/report")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    private void enrollmentData() {
        Course c1 = new Course("spring-1", "Spring Basics", "Spring Core and Spring MVC.");
        Course c2 = new Course("spring-2", "Spring Boot", "Spring Boot");
        Course c3 = new Course("java-1", "Java OO", "Java and Object Orientation: Encapsulation, Inheritance and Polymorphism.");
        courseRepository.saveAll(Arrays.asList(c1, c2, c3));

        User u1 = new User("alex", "alex@email.com");
        User u2 = new User("ana", "ana@email.com");
        User u3 = new User("bob", "bob@email.com");
        userRepository.saveAll(Arrays.asList(u1, u2, u3));

        Enrollment e1 = new Enrollment(c1, u1);
        Enrollment e2 = new Enrollment(c2, u1);
        Enrollment e3 = new Enrollment(c2, u2);
        enrollmentRepository.saveAll(Arrays.asList(e1, e2, e3));
    }

}