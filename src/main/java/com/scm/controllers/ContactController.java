package com.scm.controllers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.scm.entities.Contact;
import com.scm.entities.User;
import com.scm.forms.ContactForm;
import com.scm.forms.ContactSearchForm;
import com.scm.helpers.AppContstants;
import com.scm.helpers.OAuth2Helper;
import com.scm.services.ContactService;
import com.scm.services.ImageService;
import com.scm.services.UserService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/user/contacts")
public class ContactController {

    @Autowired
    ContactService contactService;

    @Autowired
    UserService userService;

    @Autowired
    ImageService imageService;

    @GetMapping({ "/", "" })
    public String viewContacts(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = AppContstants.PAGE_SIZE + "") int size,
            @RequestParam(value = "sortBy", defaultValue = "name") String sortBy,
            @RequestParam(value = "sortOrder", defaultValue = "asc") String sortOrder, Model model,
            Authentication authentication) {

        String email = OAuth2Helper.getEmailOfLoggedInUser(authentication);
        User user = userService.getUserByEmail(email);

        Page<Contact> contactsPages = contactService.getByUser(user, size, page, sortBy, sortOrder);

        if (!contactsPages.isEmpty()) {
            model.addAttribute("contactsPages", contactsPages);
            model.addAttribute("pageSize", AppContstants.PAGE_SIZE + "");
            model.addAttribute("contactSearchForm", new ContactSearchForm());
        } else {
            model.addAttribute("message", "No contacts found");
        }

        return "user/contacts";
    }

    @GetMapping("/add")
    public String addContactView(Model model) {
        ContactForm contactForm = new ContactForm();
        model.addAttribute("contactForm", contactForm);

        return "user/add_contact";
    }

    @PostMapping("/add")
    public String saveContact(@Valid @ModelAttribute ContactForm contactForm, BindingResult result, Model model,
            Authentication authentication) {

        if (result.hasErrors()) {
            return "user/add_contact";
        }

        String email = OAuth2Helper.getEmailOfLoggedInUser(authentication);
        User user = userService.getUserByEmail(email);

        Contact contact = new Contact();
        contact.setName(contactForm.getName());
        contact.setEmail(contactForm.getEmail());
        contact.setPhoneNumber(contactForm.getPhoneNumber());
        contact.setAddress(contactForm.getAddress());
        contact.setDescription(contactForm.getDescription());
        contact.setFavorite(contactForm.isFavorite());
        contact.setFacebookLink(contactForm.getFacebookLink());
        contact.setLinkedinLink(contactForm.getLinkedInLink());
        contact.setUser(user);

        if (contactForm.getContactImage() != null && !contactForm.getContactImage().isEmpty()) {
            String filePublicId = UUID.randomUUID().toString();
            String fileURL = imageService.uploadImage(contactForm.getContactImage(), filePublicId);
            contact.setCloudinaryImagePublicId(filePublicId);
            contact.setPicture(fileURL);
        }

        contactService.save(contact);

        return "redirect:/user/contacts";
    }

    @GetMapping("/search")
    public String searchHandler(
            @ModelAttribute ContactSearchForm contactSearchForm,
            @RequestParam(value = "size", defaultValue = AppContstants.PAGE_SIZE + "") int size,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "sortBy", defaultValue = "name") String sortBy,
            @RequestParam(value = "sortOrder", defaultValue = "asc") String sortOrder, Model model,
            Authentication authentication) {

        String email = OAuth2Helper.getEmailOfLoggedInUser(authentication);
        User user = userService.getUserByEmail(email);

        Page<Contact> contactsPages = null;

        if (contactSearchForm.getSearchField().equalsIgnoreCase("name")) {
            contactsPages = contactService.searchByName(user, contactSearchForm.getValue(), size, page, sortBy,
                    sortOrder);
        } else if (contactSearchForm.getSearchField().equalsIgnoreCase("email")) {
            contactsPages = contactService.searchByEmail(user, contactSearchForm.getValue(), size, page, sortBy,
                    sortOrder);
        } else if (contactSearchForm.getSearchField().equalsIgnoreCase("phone")) {
            contactsPages = contactService.searchByPhone(user, contactSearchForm.getValue(), size, page, sortBy,
                    sortOrder);
        }

        model.addAttribute("contactsPages", contactsPages);
        model.addAttribute("pageSize", AppContstants.PAGE_SIZE + "");
        model.addAttribute("contactSearchForm", contactSearchForm);

        return "user/search_result";
    }

    @GetMapping("/delete/{contactId}")
    public String deleteContact(@PathVariable String contactId) {
        contactService.deleteById(contactId);

        return "redirect:/user/contacts";
    }

    @GetMapping("/view/{contactId}")
    public String updateContact(@PathVariable String contactId, Model model) {

        Contact contact = contactService.getById(contactId);
        ContactForm contactForm = new ContactForm();

        contactForm.setName(contact.getName());
        contactForm.setEmail(contact.getEmail());
        contactForm.setPhoneNumber(contact.getPhoneNumber());
        contactForm.setAddress(contact.getAddress());
        contactForm.setDescription(contact.getDescription());
        contactForm.setFavorite(contact.isFavorite());
        contactForm.setFacebookLink(contact.getFacebookLink());
        contactForm.setLinkedInLink(contact.getLinkedinLink());

        model.addAttribute("contactForm", contactForm);
        model.addAttribute("contactId", contact.getId());

        return "user/update_contact";
    }

    @PostMapping("/update/{contactId}")
    public String updateContact(@PathVariable String contactId, @Valid @ModelAttribute ContactForm contactForm,
            BindingResult result) {

        if (result.hasErrors()) {
            return "user/update_contact";
        }

        Contact contact = contactService.getById(contactId);
        contact.setName(contactForm.getName());
        contact.setEmail(contactForm.getEmail());
        contact.setPhoneNumber(contactForm.getPhoneNumber());
        contact.setAddress(contactForm.getAddress());
        contact.setDescription(contactForm.getDescription());
        contact.setFavorite(contactForm.isFavorite());
        contact.setFacebookLink(contactForm.getFacebookLink());
        contact.setLinkedinLink(contactForm.getLinkedInLink());

        if (contactForm.getContactImage() != null && !contactForm.getContactImage().isEmpty()) {
            String filePublicId = UUID.randomUUID().toString();
            String fileURL = imageService.uploadImage(contactForm.getContactImage(), filePublicId);
            contact.setPicture(fileURL);
            contact.setCloudinaryImagePublicId(filePublicId);
        }

        contactService.updateByContact(contact);

        return "redirect:/user/contacts/view/" + contactId;
    }
}