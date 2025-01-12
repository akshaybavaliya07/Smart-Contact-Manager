package com.scm.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.scm.entities.User;
import com.scm.helpers.OAuth2Helper;
import com.scm.services.UserService;

@ControllerAdvice
public class RootController {

    @Autowired
    UserService userService;

    @ModelAttribute
    public void addLoggedInUserInformation(Model model, Authentication authentication) {

        if(authentication == null) {
            return;
        }

        String email = OAuth2Helper.getEmailOfLoggedInUser(authentication);

        User user = userService.getUserByEmail(email);

        model.addAttribute("loggedInUser", user);
    }
}
