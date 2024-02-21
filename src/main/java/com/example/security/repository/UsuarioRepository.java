package com.example.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.security.entities.Usuario;
import java.util.Optional;


public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByUserName(String userName);
    // El userName no se puede repetir por lo que puede ser un Optional: 
    // en la cajita que tiene que venir el usuario, puede venir o venir null, el optional
    // nos protege del nullpointexception

    boolean existsByUserName(String userName); // primero preguntamos si existe, y si existe que lo traiga
}
