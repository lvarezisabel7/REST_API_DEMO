package com.example.security.entities;

import jakarta.persistence.Column;

public class Role {

    @Column(name = "id_role")
    private int idRole;
    private String role;


}
