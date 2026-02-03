package com.dd.SalesSavvy.dtos;

import com.dd.SalesSavvy.Entities.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class CustomerRegisterDto {
    String username;
    String password;
    String email;
    Role role;
}
