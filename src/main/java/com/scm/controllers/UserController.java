package com.scm.controllers;

import org.springframework.data.domain.Page;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

import com.scm.entities.Contact;
import com.scm.entities.User;
import com.scm.forms.ProfileEditForm;
import com.scm.helpers.OAuth2Helper;
import com.scm.services.ContactService;
import com.scm.services.ImageService;
import com.scm.services.UserService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ContactService contactService;

    @Autowired
    private ImageService imageService;

    @GetMapping("/dashboard")
    public String getDashboardView(Model model, Authentication authentication) {

        String email = OAuth2Helper.getEmailOfLoggedInUser(authentication);
        User user = userService.getUserByEmail(email);

        long totalContacts = contactService.getTotalContacts(user);
        Page<Contact> recentContacts = contactService.getRecentContacts(user);

        model.addAttribute("totalContacts", totalContacts);
        model.addAttribute("recentContacts", recentContacts);

        return "user/dashboard";
    }

    @GetMapping("/profile")
    public String getProfileView() {
        return "user/profile";
    }

    @GetMapping("/profile/edit")
    public String getProfileEditView(Model model, Authentication authentication) {
        
        String userEmail = OAuth2Helper.getEmailOfLoggedInUser(authentication);
        User user = userService.getUserByEmail(userEmail);
        
        ProfileEditForm profileEditForm = new ProfileEditForm();
        profileEditForm.setName(user.getName());
        profileEditForm.setAddress(user.getAddress());
        profileEditForm.setAbout(user.getAbout());
        profileEditForm.setPhoneNumber(user.getPhoneNumber());


        model.addAttribute("profileEditForm", profileEditForm);

        return "user/update_profile";
    }

    @PostMapping("/profile/edit")
    public String saveProfileEdit(@Valid @ModelAttribute ProfileEditForm profileEditForm, BindingResult result, Authentication authentication) {

        if(result.hasErrors()) {
            return "user/update_profile";
        }

        String userEmail = OAuth2Helper.getEmailOfLoggedInUser(authentication);
        User user = userService.getUserByEmail(userEmail);

        user.setName(profileEditForm.getName());
        user.setAddress(profileEditForm.getAddress());
        user.setAbout(profileEditForm.getAbout());
        user.setPhoneNumber(profileEditForm.getPhoneNumber());

        if(profileEditForm.getProfileImage() != null && !profileEditForm.getProfileImage().isEmpty()) {
            String filePublicId = UUID.randomUUID().toString();
            String fileURL = imageService.uploadImage(profileEditForm.getProfileImage(), filePublicId);
            user.setProfilePic(fileURL);
            user.setCloudinaryImagePublicId(filePublicId);
        }

        userService.updateUser(user);

        return "redirect:/user/profile";
    }
}
