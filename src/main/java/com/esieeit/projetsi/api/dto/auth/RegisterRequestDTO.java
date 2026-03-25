package com.esieeit.projetsi.api.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegisterRequestDTO {

    @NotBlank(message = "username est obligatoire")
    @Size(min = 3, max = 30, message = "username doit contenir entre 3 et 30 caractères")
    private String username;

    @NotBlank(message = "email est obligatoire")
    @Email(message = "email doit être valide")
    @Size(max = 254, message = "email ne doit pas dépasser 254 caractères")
    private String email;

    @NotBlank(message = "password est obligatoire")
    @Size(min = 10, max = 100, message = "password doit contenir entre 10 et 100 caractères")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^\\w\\s]).+$",
            message = "password doit contenir minuscule, majuscule, chiffre et caractère spécial")
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
