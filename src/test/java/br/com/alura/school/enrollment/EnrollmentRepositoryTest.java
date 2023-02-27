package br.com.alura.school.enrollment;

import br.com.alura.school.course.Course;
import br.com.alura.school.course.CourseRepository;
import br.com.alura.school.user.User;
import br.com.alura.school.user.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Arrays;
import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class EnrollmentRepositoryTest {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void should_retrieve_enrollments_quantity_grouped_by_user_email() throws Exception {
        enrollmentsData();

        List<EnrollmentResponse> enrollmentsReport = enrollmentRepository.generateEnrollmentsReport();

        Assertions.assertEquals(2, enrollmentsReport.size());
        Assertions.assertEquals("bob@email.com", enrollmentsReport.get(0).getEmail());
        Assertions.assertEquals(2, enrollmentsReport.get(0).getEnrollments());
        Assertions.assertEquals("maria@email.com", enrollmentsReport.get(1).getEmail());
        Assertions.assertEquals(1, enrollmentsReport.get(1).getEnrollments());
    }

    @Test
    void should_retrieve_empty_list_when_no_users_enrolled() throws Exception {
        List<EnrollmentResponse> enrollmentsReport = enrollmentRepository.generateEnrollmentsReport();

        Assertions.assertEquals(0, enrollmentsReport.size());
    }

    private void enrollmentsData() {
        Course c1 = new Course("spring-1", "Spring Basics", "Spring Core and Spring MVC.");
        Course c2 = new Course("spring-2", "Spring Boot", "Spring Boot");
        Course c3 = new Course("spring-3", "Spring Data", "Spring Data JPA");
        courseRepository.saveAll(Arrays.asList(c1, c2, c3));

        User u1 = new User("bob", "bob@email.com");
        User u2 = new User("maria", "maria@email.com");
        User u3 = new User("james", "james@email.com");
        userRepository.saveAll(Arrays.asList(u1, u2, u3));

        Enrollment e1 = new Enrollment(c1, u1);
        Enrollment e2 = new Enrollment(c2, u1);
        Enrollment e3 = new Enrollment(c3, u2);
        enrollmentRepository.saveAll(Arrays.asList(e1, e2, e3));
    }
}