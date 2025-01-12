package com.scm.helpers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class OAuth2Helper {
	
	@Value("${server.baseURL}")
	private String baseUrl;

    public static String getEmailOfLoggedInUser(Authentication authentication) {

        if(authentication instanceof OAuth2AuthenticationToken) {

            var oAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
            var clientId = oAuth2AuthenticationToken.getAuthorizedClientRegistrationId();

            var oauth2User = (OAuth2User) authentication.getPrincipal();
            String email = "";

            if (clientId.equalsIgnoreCase("google")) {

                email = oauth2User.getAttribute("email").toString();

            } else if (clientId.equalsIgnoreCase("github")) {

                email = oauth2User.getAttribute("email") != null ? oauth2User.getAttribute("email").toString()
                    : oauth2User.getAttribute("login").toString() + "@gmail.com";
            }

            return email;

        }  else {
            
            return authentication.getName();
        }
    }
    
    public String getLinkForEmailVerification(String emailToken) {

        String link = this.baseUrl+"/auth/verify-email?token="+emailToken;

        return link;
    }

    public static String getEmailVerificationMessge(String emailToken) {
    	OAuth2Helper oAuth2Helper = new OAuth2Helper();
        String emailVerificationLink = oAuth2Helper.getLinkForEmailVerification(emailToken);
        String message = """
                Hello,

                Thank you for signing up with Smart Contact Manager! We're thrilled to have you on board.
                Before you can start managing your contacts, we need to verify your email address.

                Please click the link below to confirm your email:
                """ + emailVerificationLink
                + """

                If you didn't create an account with us, please ignore this email.

                Thank you,
                The Smart Contact Manager Team
                """;

        return message;
    }

    public static String generateOtpMessage(int otp) {
        String otpMessage = """
                Hello,
    
                You requested to reset your password for Smart Contact Manager.
                Please use the following One-Time Password (OTP) to proceed with resetting your password:
    
                """ + otp + """

                If you did not request this, please ignore this email.
    
                Thank you,
                The Smart Contact Manager Team
                """;
    
        return otpMessage;
    }

    public static int generateOtp() {
        return (int) (Math.random() * 900000) + 100000;
    }
    
    public static String passwordChangedMessage() {
        String message = """
            Hello,

            Your password has been successfully changed.

            If you did not request this password reset, please contact our support team immediately at scmsupport@gmail.com.

            Thank you,
            The Smart Contact Manager Team

            """;
        
        return message;
    }
}