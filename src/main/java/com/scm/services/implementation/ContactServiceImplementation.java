package com.scm.services.implementation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.scm.entities.Contact;
import com.scm.entities.User;
import com.scm.helpers.ResourceNotFoundException;
import com.scm.repositories.ContactRepository;
import com.scm.services.ContactService;

@Service
public class ContactServiceImplementation implements ContactService {

    @Autowired
    private ContactRepository contactRepo;

    @Override
    public Contact save(Contact contact) {
        String id = UUID.randomUUID().toString();
        contact.setId(id);
        contact.setCreatedDate(LocalDateTime.now());
        return contactRepo.save(contact);
    }

    @Override
    public List<Contact> getAll() {
        return contactRepo.findAll();
    }

    @Override
    public Contact getById(String id) {
        return contactRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Contact not found with thid id"));
    }

    @Override
    public void deleteById(String id) {
        contactRepo.deleteById(id);
    }

    @Override
    public Contact updateByContact(Contact contact) {
        Contact oldContact = contactRepo.findById(contact.getId()).orElseThrow(() -> new ResourceNotFoundException("Contact not found with"));
        oldContact.setName(contact.getName());
        oldContact.setEmail(contact.getEmail());
        oldContact.setPhoneNumber(contact.getPhoneNumber());
        oldContact.setDescription(contact.getDescription());
        oldContact.setAddress(contact.getAddress());
        oldContact.setFavorite(contact.isFavorite());
        oldContact.setFacebookLink(contact.getFacebookLink());
        oldContact.setLinkedinLink(contact.getLinkedinLink());
        oldContact.setPicture(contact.getPicture());
        oldContact.setCloudinaryImagePublicId(contact.getCloudinaryImagePublicId());
        
        return contactRepo.save(oldContact);
    }

    // @Override
    // public List<Contact> getByUserId(String userId) {
    //     return contactRepo.findByUserId(userId);
    // }

    @Override
    public Page<Contact> getByUser(User user, int size, int page, String sortBy, String sortOrder) {

        Sort sort = sortOrder.equals("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        return contactRepo.findByUser(user, pageable);
    }

    @Override
    public Page<Contact> searchByName(User user, String name, int size, int page, String sortBy, String sortOrder) {

        Sort sort = sortOrder.equals("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        return contactRepo.findByUserAndNameContaining(user, name, pageable);
    }

    @Override
    public Page<Contact> searchByEmail(User user, String email, int size, int page, String sortBy, String sortOrder) {

        Sort sort = sortOrder.equals("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        return contactRepo.findByUserAndEmailContaining(user, email, pageable);
    }

    @Override
    public Page<Contact> searchByPhone(User user, String phone, int size, int page, String sortBy, String sortOrder) {
        
        Sort sort = sortOrder.equals("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        return contactRepo.findByUserAndPhoneNumberContaining(user, phone, pageable);
    }

    @Override
    public long getTotalContacts(User user) {
        return contactRepo.countByUser(user);
    }

    @Override
    public Page<Contact> getRecentContacts(User user) {
        Sort sort = Sort.by("createdDate").descending();
        Pageable pageable = PageRequest.of(0, 5, sort);

        return contactRepo.findByUser(user, pageable);
    }
}