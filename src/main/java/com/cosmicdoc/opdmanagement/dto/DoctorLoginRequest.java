package com.cosmicdoc.opdmanagement.dto;

import lombok.Data;

@Data
public class DoctorLoginRequest {
    private String email;
    private String password;
}
