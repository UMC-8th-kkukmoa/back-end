package kkukmoa.kkukmoa.user.service;

import lombok.RequiredArgsConstructor;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {
    private final JavaMailSender mailSender;

    @Override
    public void sendOtp(String to, String code) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject("회원가입 인증번호 안내");
        msg.setText(
                """
                안녕하세요. 꾹모아입니다.
                아래 인증번호를 입력해 주세요.

                인증번호: %s

                인증번호는 5분 후 만료됩니다.
                """
                        .formatted(code));
        mailSender.send(msg);
    }
}
