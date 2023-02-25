package br.com.alura.school.course;

import br.com.alura.school.enrollment.Enrollment;
import br.com.alura.school.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
public
class Course {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Size(max = 10)
    @NotBlank
    @Column(nullable = false, unique = true)
    private String code;

    @Size(max = 20)
    @NotBlank
    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    @OneToMany(
            mappedBy = "course",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<Enrollment> enrollments = new HashSet<>();

    @Deprecated
    protected Course() {
    }

    public Course(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    String getCode() {
        return code;
    }

    String getName() {
        return name;
    }

    String getDescription() {
        return description;
    }

    public Set<Enrollment> getEnrollments() {
        return enrollments;
    }

    public void setEnrollments(Set<Enrollment> enrollments) {
        this.enrollments = enrollments;
    }

    public void addUser(User user) {
        Enrollment enrollment = new Enrollment(this, user);
        enrollments.add(enrollment);
        user.getEnrollments().add(enrollment);
    }

    public void removeUser(User user) {
        for (Iterator<Enrollment> iterator = enrollments.iterator();
             iterator.hasNext(); ) {
            Enrollment enrollment = iterator.next();
            if (enrollment.getCourse().equals(this) && enrollment.getUser().equals(user)) {
                iterator.remove();
                enrollment.getCourse().getEnrollments().remove(enrollment);
                enrollment.setCourse(null);
                enrollment.setUser(null);
            }
        }
    }

}
