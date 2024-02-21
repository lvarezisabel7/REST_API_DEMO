package com.example.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.security.entities.Role;
import java.util.Optional;


public interface RoleRepository extends JpaRepository<Role, Integer> {

    Optional<Role> findByRole(String role); // El role tampoco se repite asi que -> optional
}
