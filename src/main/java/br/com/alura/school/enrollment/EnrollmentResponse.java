package br.com.alura.school.enrollment;

import com.fasterxml.jackson.annotation.JsonProperty;

class EnrollmentResponse {

    @JsonProperty
    private final String email;

    @JsonProperty("quantidade_matriculas")
    private final Long enrollments;

    public EnrollmentResponse(String email, Long enrollments) {
        this.email = email;
        this.enrollments = enrollments;
    }

    public String getEmail() {
        return email;
    }

    public Long getEnrollments() {
        return enrollments;
    }
}
