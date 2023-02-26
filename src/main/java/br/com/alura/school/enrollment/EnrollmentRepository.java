package br.com.alura.school.enrollment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    Boolean existsByCourseIdAndUserId(long courseId, Long userId);
    @Query("SELECT new br.com.alura.school.enrollment.EnrollmentResponse(u.email, COUNT(e.course)) FROM Enrollment e LEFT JOIN e.user u WHERE (SELECT COUNT(e.course) FROM Enrollment e) > 0 GROUP BY u.email ORDER BY COUNT(e.course) DESC")
    List<EnrollmentResponse> generateEnrollmentsReport();
}
