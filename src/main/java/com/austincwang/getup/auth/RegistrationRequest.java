package com.austincwang.getup.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RegistrationRequest {

    @NotEmpty(message = "First name is mandated")
    @NotBlank(message = "First name is mandated")
    private String firstname;
    @NotEmpty(message = "Last name is mandated")
    @NotBlank(message = "Last name is mandated")
    private String lastname;
    @Email(message = "Email is not properly formatted")
    @NotEmpty(message = "Email is mandated")
    @NotBlank(message = "Email is mandated")
    private String email;
    @NotEmpty(message = "Password is mandated")
    @NotBlank(message = "Password is mandated")
    @Size(min = 8, message = "Password should be at least 8 characters long.")
    private String password;

}
