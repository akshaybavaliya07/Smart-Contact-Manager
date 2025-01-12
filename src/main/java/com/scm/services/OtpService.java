package com.scm.services;

import org.springframework.stereotype.Service;

@Service
public interface OtpService {

    void saveOtp(String email, int otp);

    boolean verifyOtp(String email, int otp);
}
