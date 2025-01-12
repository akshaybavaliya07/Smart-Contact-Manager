package com.scm.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.scm.entities.Contact;
import com.scm.entities.User;

@Service
public interface ContactService {

    Contact save(Contact contact);

    List<Contact> getAll();

    Contact getById(String id);

    void deleteById(String id);

    Contact updateByContact(Contact contact); 
    
    long getTotalContacts(User user);

    Page<Contact> getRecentContacts(User user);

    // List<Contact> getByUserId(String userId);

    Page<Contact> getByUser(User user, int size, int page, String sortBy, String sortOrder);

    Page<Contact> searchByName(User user, String name, int size, int page, String sortBy, String sortOrder);
    Page<Contact> searchByEmail(User user, String email, int size, int page, String sortBy, String sortOrder);
    Page<Contact> searchByPhone(User user, String phone, int size, int page, String sortBy, String sortOrder);
}