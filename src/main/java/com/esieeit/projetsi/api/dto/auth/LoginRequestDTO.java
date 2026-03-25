package com.esieeit.projetsi.api.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LoginRequestDTO {

    @NotBlank(message = "login est obligatoire")
    private String login;

    @NotBlank(message = "password est obligatoire")
    @Size(min = 1, max = 100, message = "password est invalide")
    private String password;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
