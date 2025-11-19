package com.example.usermanagement.repository;

import com.example.usermanagement.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    Page<Usuario> findByNomeContainingIgnoreCaseOrEmailContainingIgnoreCase(String nome, String email, Pageable pageable);
}
