package com.example.web_organic.service;
import com.example.web_organic.entity.Contact;
import com.example.web_organic.modal.request.InsertContactRequest;
import com.example.web_organic.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContactService {
    @Autowired
    private ContactRepository contactRepository;
    public void sendContactMessage(InsertContactRequest insertContactRequest) {
          Contact contact = Contact.builder()
                .fullName(insertContactRequest.getName())
                .email(insertContactRequest.getEmail())
                .phone(insertContactRequest.getPhone())
                .title(insertContactRequest.getSubject())
                .message(insertContactRequest.getMessage())
                .build();
        contactRepository.save(contact);
    }
}
