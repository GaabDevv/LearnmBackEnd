package com.example.learnmaster.tccv2.controller;

import com.example.learnmaster.tccv2.model.Usuario;
import com.example.learnmaster.tccv2.repository.UsuarioRepository;
import com.example.learnmaster.tccv2.service.PasswordResetService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetService passwordResetService;

    public UsuarioController(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            PasswordResetService passwordResetService) {

        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.passwordResetService = passwordResetService;
    }

    // CREATE
    @PostMapping
    public Usuario criarUsuario(@RequestBody Usuario usuario) {

        usuario.setSenha(
                passwordEncoder.encode(usuario.getSenha())
        );

        return usuarioRepository.save(usuario);
    }

    // READ
    @GetMapping
    public List<Usuario> listar() {

        return usuarioRepository.findAll();
    }

    @GetMapping("/{id}")
    public Usuario buscar(@PathVariable Integer id) {

        return usuarioRepository.findById(id).orElse(null);
    }

    // UPDATE
    @PutMapping("/{id}")
    public Usuario atualizar(
            @PathVariable Integer id,
            @RequestBody Usuario usuario) {

        usuario.setId(id);

        return usuarioRepository.save(usuario);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Integer id) {

        usuarioRepository.deleteById(id);
    }

    // RECUPERAÇÃO DE SENHA
    @PostMapping("/recuperar-senha")
    public ResponseEntity<?> recuperarSenha(
            @RequestParam String email) {

        passwordResetService.solicitarRecuperacao(email);

        return ResponseEntity.ok(
                "Se o e-mail estiver cadastrado, enviaremos um link de recuperação."
        );
    }

    // REDEFINIÇÃO DE SENHA
    @PostMapping("/redefinir-senha")
    public ResponseEntity<?> redefinirSenha(
            @RequestParam String codigo,
            @RequestParam String novaSenha) {

        passwordResetService.redefinirSenha(
                codigo,
                novaSenha
        );

        return ResponseEntity.ok(
                "Senha redefinida com sucesso!"
        );
    }
}