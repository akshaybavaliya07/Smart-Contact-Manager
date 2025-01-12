package com.scm.services.implementation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.scm.entities.User;
import com.scm.repositories.UserRepository;
import com.scm.services.EmailService;
import com.scm.services.UserService;
import com.scm.helpers.AppContstants;
import com.scm.helpers.OAuth2Helper;
import com.scm.helpers.ResourceNotFoundException;

@Service
public class UserServiceImplementation implements UserService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Override
    public User saveUser(User user) {

        String id = UUID.randomUUID().toString();
        user.setUserId(id);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoleList(List.of(AppContstants.ROLE_USER));

        String emailToken = UUID.randomUUID().toString();
        String emailMessage = OAuth2Helper.getEmailVerificationMessge(emailToken);

        user.setEmailToken(emailToken);
        User savedUser = userRepo.save(user);

        emailService.sendEmail(savedUser.getEmail(), "Verify Email : Smart Contact Manager", emailMessage);

        return savedUser;
    }

    @Override
    public Optional<User> getUserById(String id) {

        return userRepo.findById(id);
    }

    @Override
    public User updateUser(User user) {

        User existingUser = userRepo.findById(user.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        existingUser.setName(user.getName());
        existingUser.setEmail(user.getEmail());
        existingUser.setPhoneNumber(user.getPhoneNumber());
        existingUser.setAbout(user.getAbout());
        existingUser.setProfilePic(user.getProfilePic());
        existingUser.setCloudinaryImagePublicId(user.getCloudinaryImagePublicId());
        existingUser.setEmailVerified(user.isEmailVerified());
        existingUser.setPhoneVerified(user.isPhoneVerified());
        existingUser.setProvider(user.getProvider());
        existingUser.setProviderUserId(user.getProviderUserId());

        return userRepo.save(existingUser);
    }

    @Override
    public void deleteUser(String id) {

        User user = userRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        userRepo.delete(user);
    }

    @Override
    public boolean isUserExists(String id) {

        User user = userRepo.findById(id).orElse(null);
        return user != null ? true : false;
    }

    @Override
    public boolean isUserExistsByEmail(String email) {

        User user = userRepo.findByEmail(email).orElse(null);
        return user != null ? true : false;
    }

    @Override
    public List<User> getAllUsers() {

        return userRepo.findAll();
    }

    @Override
    public User getUserByEmail(String email) {

        return userRepo.findByEmail(email).orElse(null);
    }
}