package com.example.web_organic.rest;

import com.example.web_organic.modal.request.InsertContactRequest;
import com.example.web_organic.service.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/contact")
public class ContactApi {
    @Autowired
    private ContactService contactService;

    @PostMapping("/insert")
    public ResponseEntity<?> insertContact(@RequestBody InsertContactRequest insertContactRequest){
        contactService.sendContactMessage(insertContactRequest);
        return ResponseEntity.ok().build();
    }
}
