package kkukmoa.kkukmoa.user.service;

public interface MailService {
    void sendOtp(String to, String code);
}
