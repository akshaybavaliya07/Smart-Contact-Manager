package com.scm.controllers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.scm.entities.User;
import com.scm.forms.ContactUsForm;
import com.scm.forms.ForgotPasswordForm;
import com.scm.forms.UserForm;
import com.scm.helpers.OAuth2Helper;
import com.scm.services.EmailService;
import com.scm.services.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class PageController {

    @Autowired
    UserService userService;

    @Autowired
    EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${spring.mail.username}")
    private String toDomain;

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }

    @GetMapping("/service")
    public String service() {
        return "service";
    }

    @GetMapping("/contact")
    public String contact(Model model) {

        ContactUsForm contactUsForm = new ContactUsForm();

        model.addAttribute("contactUsForm", contactUsForm);

        return "contact";
    }

    @PostMapping("/contact")
    public String contact(@Valid @ModelAttribute ContactUsForm contactUsForm, BindingResult result, Model model) {

        if (result.hasErrors()) {
            return "contact";
        }

        String message = """
        Name: %s
        Email: %s
        
        %s
        """.formatted(contactUsForm.getName(), contactUsForm.getEmail(), contactUsForm.getMessage());

        emailService.sendEmail(toDomain, "Contact Us Message", message);

        model.addAttribute("successMessage", "Your message has been sent successfully!");

        return "contact";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/signup")
    public String signup(Model model, HttpSession session) {

        model.addAttribute("userForm", new UserForm());

        return "signup";
    }

    @PostMapping("/signup")
    public String processSignup(@Valid @ModelAttribute UserForm userForm, BindingResult result, RedirectAttributes redirectAttributes) {

        if(result.hasErrors()) {
            return "signup";
        }

        if(userService.isUserExistsByEmail(userForm.getEmail())) {
            result.rejectValue("email", "error.userForm", "Email already exists");
            return "signup";
        }

        User user = new User();
        user.setName(userForm.getName());
        user.setEmail(userForm.getEmail());
        user.setPassword(userForm.getPassword());

        userService.saveUser(user);

        redirectAttributes.addFlashAttribute("signupSuccess", "A verification link has been sent to your email account. Please verify your email to activate your account.");

        return "redirect:/login";
    }

    @PostMapping("/resendVerification")
    public String resendVerification(@RequestParam("mail") String email){
        User user = userService.getUserByEmail(email);
        if (user != null) {

            String newEmailToken = UUID.randomUUID().toString();
            user.setEmailToken(newEmailToken);
    
            userService.updateUser(user);

            String emailMessage = OAuth2Helper.getEmailVerificationMessge(newEmailToken);
            emailService.sendEmail(email, "Verify your email address", emailMessage);
    
            return "redirect:/login?verificationSent=true";
        } else {
            // Return an error message if the user is not found
            return "redirect:/login?error=userNotFound";
        }
    }

    @GetMapping("/forgot-password") 
    public String forgotPassword(Model model) {
        ForgotPasswordForm forgotPasswordForm = new ForgotPasswordForm();
        model.addAttribute("forgotPasswordForm", forgotPasswordForm);
        return "forgotPassword";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@Valid @ModelAttribute ForgotPasswordForm forgotPasswordForm, BindingResult result, Model model) {

        if(result.hasErrors()) {
            model.addAttribute("invalidInputs", "invalidInputs");
            return "forgotPassword";
        }
        
        User user = userService.getUserByEmail(forgotPasswordForm.getEmail());
        user.setPassword(passwordEncoder.encode(forgotPasswordForm.getPassword()));
        
        userService.updateUser(user);

        String passwordChangedMessage = OAuth2Helper.passwordChangedMessage();
        emailService.sendEmail(forgotPasswordForm.getEmail(), "Password Reset", passwordChangedMessage);

        return "redirect:/login?success";
    }
}