package br.com.alura.school.enrollment;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class NewEnrollmentRequest {
    @Size(max = 20)
    @NotBlank
    @JsonProperty
    private String username;

    public NewEnrollmentRequest(String username) {
        this.username = username;
    }

    public NewEnrollmentRequest() {
    }

    public String getUsername() {
        return username;
    }
}
