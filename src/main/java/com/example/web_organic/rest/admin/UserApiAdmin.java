package com.example.web_organic.rest.admin;

import com.example.web_organic.entity.User;
import com.example.web_organic.exception.UnauthorizedException;
import com.example.web_organic.modal.request.UpSertUserRequestAdmin;
import com.example.web_organic.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.nio.file.AccessDeniedException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/user")
public class UserApiAdmin {
    @Autowired
    private UserService userService;
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody UpSertUserRequestAdmin upSertUserRequestAdmin) {
        User user = userService.create(upSertUserRequestAdmin);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateUser(@PathVariable int id,@RequestBody UpSertUserRequestAdmin upSertUserRequestAdmin) {
        User user = userService.update(id,upSertUserRequestAdmin);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable int id) {
        try {
            userService.delete(id);
            return ResponseEntity.ok().build();
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage()); // 401
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(e.getMessage()); // 405
        }
    }

}
