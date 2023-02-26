package br.com.alura.school.enrollment;

import br.com.alura.school.course.Course;
import br.com.alura.school.course.CourseRepository;
import br.com.alura.school.user.User;
import br.com.alura.school.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

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
        courseRepository.save(new Course("java-1", "Java OO", "Java and Object Orientation: Encapsulation, Inheritance and Polymorphism."));

        userRepository.save(new User("alex", "alex@email.com"));

        NewEnrollmentRequest newEnrollmentRequest = new NewEnrollmentRequest("alex");

        mockMvc.perform(post("/courses/java-1/enroll")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(newEnrollmentRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    void should_not_allow_duplication_of_enrollment() throws Exception {
        Course course = courseRepository.save(new Course("java-1", "Java OO", "Java and Object Orientation: Encapsulation, Inheritance and Polymorphism."));

        User user = userRepository.save(new User("alex", "alex@email.com"));

        enrollmentRepository.save(new Enrollment(course, user));

        NewEnrollmentRequest newEnrollmentRequest = new NewEnrollmentRequest("alex");

        mockMvc.perform(post("/courses/java-1/enroll")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(newEnrollmentRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_retrieve_enrollments_quantity_grouped_by_user_email() throws Exception {
        courseRepository.save(new Course("spring-1", "Spring Basics", "Spring Core and Spring MVC."));
        courseRepository.save(new Course("spring-2", "Spring Boot", "Spring Boot"));

        userRepository.save(new User("alex", "alex@email.com"));
        userRepository.save(new User("ana", "ana@email.com"));

        NewEnrollmentRequest newEnrollmentRequestAlex = new NewEnrollmentRequest("alex");
        NewEnrollmentRequest newEnrollmentRequestAna = new NewEnrollmentRequest("ana");

        mockMvc.perform(post("/courses/spring-1/enroll")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(newEnrollmentRequestAna)));

        mockMvc.perform(post("/courses/spring-1/enroll")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(newEnrollmentRequestAlex)));

        mockMvc.perform(post("/courses/spring-2/enroll")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(newEnrollmentRequestAlex)));


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
    void should_retrieve_no_content_for_no_enrolled_users() throws Exception {
        mockMvc.perform(get("/courses/enroll/report")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

}