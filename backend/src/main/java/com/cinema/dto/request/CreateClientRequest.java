package com.cinema.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateClientRequest(
        @NotBlank @Size(max = 255) String organizationName,
        @NotBlank @Email @Size(max = 255) String email,
        @Size(max = 20) String phone,
        String address
) {
}
