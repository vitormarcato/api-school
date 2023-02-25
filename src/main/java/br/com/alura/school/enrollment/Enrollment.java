package br.com.alura.school.enrollment;

import br.com.alura.school.course.Course;
import br.com.alura.school.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Objects;

@Entity
public class Enrollment {
    @EmbeddedId
    private EnrollmentId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("courseId")
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    private User user;
    @NotNull
    @Column(name = "enrollment_date")
    private LocalDate enrolmmentDate;

    private Enrollment() {
    }

    public Enrollment(Course course, User user) {
        this.id = new EnrollmentId(course.getId(), user.getId());
        this.course = course;
        this.user = user;
        this.enrolmmentDate = LocalDate.now();
    }

    public EnrollmentId getId() {
        return id;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDate getEnrolmmentDate() {
        return enrolmmentDate;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Enrollment that = (Enrollment) o;
        return Objects.equals(id, that.id) && Objects.equals(course, that.course) && Objects.equals(user, that.user) && Objects.equals(enrolmmentDate, that.enrolmmentDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, course, user, enrolmmentDate);
    }
}
