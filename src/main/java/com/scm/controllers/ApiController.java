package com.scm.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.scm.entities.Contact;
import com.scm.helpers.OAuth2Helper;
import com.scm.services.ContactService;
import com.scm.services.EmailService;
import com.scm.services.OtpService;
import com.scm.services.UserService;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private ContactService contactService;

    @Autowired
    UserService userService;

    @Autowired
    EmailService emailService;

    @Autowired
    OtpService otpService;

    @GetMapping("/contacts/{contactId}")
    public Contact getContact(@PathVariable String contactId) {

        return contactService.getById(contactId);
    }

    @GetMapping("/user/check-existence/{email}")
    public ResponseEntity<Map<String, Boolean>> isUserExists(@PathVariable String email) {
        boolean userExists = userService.isUserExistsByEmail(email);
        Map<String, Boolean> response = new HashMap<>();
        response.put("isUserExists", userExists);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/send-otp/{email}")
    public void sendOtpToEmail(@PathVariable String email) {
        int otp = OAuth2Helper.generateOtp();
        otpService.saveOtp(email, otp);
        String otpMessage = OAuth2Helper.generateOtpMessage(otp);
        emailService.sendEmail(email, "Your OTP for Password Change",otpMessage);
    }


    @GetMapping("/user/verify-otp/{otp}/{email}")
    public ResponseEntity<Map<String, Boolean>> verifyOtp(@PathVariable String email, @PathVariable int otp) {
        boolean isValidOtp = otpService.verifyOtp(email, otp);
        Map<String, Boolean> response = new HashMap<>();
        response.put("isValidOtp", isValidOtp);
        return ResponseEntity.ok(response);
    }
}
