package com.scm.config;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.scm.entities.Providers;
import com.scm.entities.User;
import com.scm.helpers.AppContstants;
import com.scm.repositories.UserRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OauthAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UserRepository userRepo;

    @Override
    public void onAuthenticationSuccess(
        HttpServletRequest request, 
        HttpServletResponse response,
        Authentication authentication) throws IOException, ServletException {

        var oauth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
        String authorizedClientRegistrationId = oauth2AuthenticationToken.getAuthorizedClientRegistrationId();
 
        DefaultOAuth2User user = (DefaultOAuth2User) authentication.getPrincipal();

        User newUser = new User();
        newUser.setUserId(UUID.randomUUID().toString());
        newUser.setEnabled(true);
        newUser.setEmailVerified(true);
        newUser.setRoleList(List.of(AppContstants.ROLE_USER));
        newUser.setProviderUserId(user.getName());

        if(authorizedClientRegistrationId.equalsIgnoreCase("google")) {
            
            String name = user.getAttribute("name").toString();
            String email = user.getAttribute("email").toString();
            String picture = user.getAttribute("picture").toString();

            newUser.setName(name);
            newUser.setEmail(email);
            newUser.setProfilePic(picture);
            newUser.setProvider(Providers.GOOGLE);
            newUser.setPassword("login with google");
            newUser.setAbout("This account is logged in using Google ...");

        } else if(authorizedClientRegistrationId.equalsIgnoreCase("github")) {
            
            String name = user.getAttribute("name").toString();
            String email = user.getAttribute("email") != null ? user.getAttribute("email") : user.getAttribute("login").toString() + "@gmail.com";
            String picture = user.getAttribute("avatar_url").toString();

            newUser.setName(name);
            newUser.setEmail(email);
            newUser.setProfilePic(picture);
            newUser.setProvider(Providers.GITHUB);
            newUser.setPassword("login with github");
            newUser.setAbout("This account is logged in using Github ...");
            

        } else if(authorizedClientRegistrationId.equalsIgnoreCase("facebook")) {
            // facebook login
        } else if (authorizedClientRegistrationId.equalsIgnoreCase("linkedin")) {
            // linkedin login
        }

        User checkIfUserExits = userRepo.findByEmail(newUser.getEmail()).orElse(null);

        if(checkIfUserExits == null) {
            userRepo.save(newUser);    
        }

        new DefaultRedirectStrategy().sendRedirect(request, response, "/user/profile");
    }
}