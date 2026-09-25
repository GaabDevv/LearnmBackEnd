package com.example.learnmaster.tccv2.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarEmailRecuperacao(
            String email,
            String codigo) {

        System.out.println("==============================");
        System.out.println("ENTREI NO EMAIL SERVICE");
        System.out.println("DESTINATARIO: " + email);
        System.out.println("CODIGO: " + codigo);
        System.out.println("==============================");

        SimpleMailMessage mensagem =
                new SimpleMailMessage();

        mensagem.setTo(email);

        mensagem.setSubject(
                "Recuperação de senha - LearnMaster"
        );

        mensagem.setText(
                "Olá!\n\n"
                        + "Recebemos uma solicitação para "
                        + "redefinir sua senha no LearnMaster.\n\n"
                        + "Seu código de recuperação é:\n\n"
                        + "        " + codigo + "\n\n"
                        + "Digite este código na tela de "
                        + "recuperação de senha do LearnMaster.\n\n"
                        + "Este código expira em 30 minutos.\n\n"
                        + "Se você não solicitou essa alteração, "
                        + "ignore este e-mail."
        );

        System.out.println(
                "Tentando enviar o e-mail..."
        );

        mailSender.send(mensagem);

        System.out.println(
                "E-MAIL ENVIADO COM SUCESSO!"
        );
    }
}