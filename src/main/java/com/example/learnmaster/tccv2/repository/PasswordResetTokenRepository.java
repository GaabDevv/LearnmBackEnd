package com.example.learnmaster.tccv2.repository;

import com.example.learnmaster.tccv2.model.PasswordResetToken;
import com.example.learnmaster.tccv2.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository
        extends JpaRepository<PasswordResetToken, Long> {

    void deleteByUsuario(Usuario usuario);

    Optional<PasswordResetToken> findByToken(String token);
}