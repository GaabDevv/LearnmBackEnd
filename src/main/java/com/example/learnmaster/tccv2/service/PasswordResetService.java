package com.example.learnmaster.tccv2.service;

import com.example.learnmaster.tccv2.model.PasswordResetToken;
import com.example.learnmaster.tccv2.model.Usuario;
import com.example.learnmaster.tccv2.repository.PasswordResetTokenRepository;
import com.example.learnmaster.tccv2.repository.UsuarioRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class PasswordResetService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public PasswordResetService(
            UsuarioRepository usuarioRepository,
            PasswordResetTokenRepository tokenRepository,
            EmailService emailService,
            PasswordEncoder passwordEncoder) {

        this.usuarioRepository = usuarioRepository;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    // ==========================================
    // SOLICITAR RECUPERAÇÃO DE SENHA
    // ==========================================

    @Transactional
    public void solicitarRecuperacao(String email) {

        System.out.println("=================================");
        System.out.println("INICIANDO RECUPERACAO DE SENHA");
        System.out.println("E-MAIL: " + email);
        System.out.println("=================================");

        Optional<Usuario> usuarioOpt =
                usuarioRepository.findByEmail(email);

        if (usuarioOpt.isEmpty()) {

            System.out.println("USUARIO NAO ENCONTRADO");

            return;
        }

        Usuario usuario = usuarioOpt.get();

        System.out.println(
                "USUARIO ENCONTRADO: "
                        + usuario.getEmail()
        );

        // Remove códigos antigos
        tokenRepository.deleteByUsuario(usuario);

        System.out.println(
                "CODIGOS ANTIGOS REMOVIDOS"
        );

        // ==========================================
        // GERA CÓDIGO DE 6 DÍGITOS
        // ==========================================

        Random random = new Random();

        String codigo = String.format(
                "%06d",
                random.nextInt(1000000)
        );

        System.out.println(
                "NOVO CODIGO GERADO: "
                        + codigo
        );

        // ==========================================
        // CRIA O TOKEN/CÓDIGO
        // ==========================================

        PasswordResetToken resetToken =
                new PasswordResetToken();

        resetToken.setToken(codigo);

        resetToken.setUsuario(usuario);

        resetToken.setExpiracao(
                LocalDateTime.now().plusMinutes(30)
        );

        // ==========================================
        // SALVA NO BANCO
        // ==========================================

        System.out.println(
                "SALVANDO CODIGO NO BANCO..."
        );

        tokenRepository.saveAndFlush(resetToken);

        System.out.println(
                "CODIGO SALVO COM SUCESSO!"
        );

        // ==========================================
        // ENVIA E-MAIL
        // ==========================================

        System.out.println(
                "ENVIANDO E-MAIL..."
        );

        emailService.enviarEmailRecuperacao(
                usuario.getEmail(),
                codigo
        );

        System.out.println(
                "E-MAIL ENVIADO COM SUCESSO!"
        );

        System.out.println(
                "================================="
        );

        System.out.println(
                "RECUPERACAO FINALIZADA"
        );

        System.out.println(
                "================================="
        );
    }

    // ==========================================
    // REDEFINIR SENHA
    // ==========================================

    @Transactional
    public void redefinirSenha(
            String codigo,
            String novaSenha) {

        System.out.println("=================================");
        System.out.println("INICIANDO REDEFINICAO DE SENHA");
        System.out.println("CODIGO: " + codigo);
        System.out.println("=================================");

        // Procura o código no banco
        Optional<PasswordResetToken> tokenOpt =
                tokenRepository.findByToken(codigo);

        // Código não encontrado
        if (tokenOpt.isEmpty()) {

            System.out.println(
                    "CODIGO INVALIDO"
            );

            throw new RuntimeException(
                    "Código inválido."
            );
        }

        PasswordResetToken resetToken =
                tokenOpt.get();

        // ==========================================
        // VERIFICA EXPIRAÇÃO
        // ==========================================

        if (resetToken.getExpiracao()
                .isBefore(LocalDateTime.now())) {

            System.out.println(
                    "CODIGO EXPIRADO"
            );

            tokenRepository.delete(resetToken);

            throw new RuntimeException(
                    "Código expirado."
            );
        }

        // ==========================================
        // RECUPERA USUÁRIO
        // ==========================================

        Usuario usuario =
                resetToken.getUsuario();

        System.out.println(
                "USUARIO ENCONTRADO: "
                        + usuario.getEmail()
        );

        // ==========================================
        // CRIPTOGRAFA NOVA SENHA
        // ==========================================

        String senhaCriptografada =
                passwordEncoder.encode(novaSenha);

        usuario.setSenha(
                senhaCriptografada
        );

        usuarioRepository.save(usuario);

        System.out.println(
                "SENHA ALTERADA COM SUCESSO!"
        );

        // ==========================================
        // INVALIDA O CÓDIGO
        // ==========================================

        tokenRepository.delete(resetToken);

        System.out.println(
                "CODIGO INVALIDADO!"
        );

        System.out.println(
                "================================="
        );

        System.out.println(
                "REDEFINICAO FINALIZADA"
        );

        System.out.println(
                "================================="
        );
    }
}