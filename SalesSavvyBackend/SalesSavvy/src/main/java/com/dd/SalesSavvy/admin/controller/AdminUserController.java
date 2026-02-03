package com.dd.SalesSavvy.admin.controller;

import com.dd.SalesSavvy.Entities.Customer;
import com.dd.SalesSavvy.admin.services.AdminServiceUserContract;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin/user")
@AllArgsConstructor
public class AdminUserController {

    private final AdminServiceUserContract adminServiceUserContract;

    @PutMapping("/modify")
    public ResponseEntity<?> modifyUser(@RequestBody Map<String, Object> userRequest) {
        try {
            Integer userId = (Integer) userRequest.get("userId");
            String username = (String) userRequest.get("username");
            String email = (String) userRequest.get("email");
            String role = (String) userRequest.get("role");
            Customer updatedUser = adminServiceUserContract.modifyUser(userId, username, email, role);
            Map<String, Object> response = new HashMap<>();
            response.put("userId", updatedUser.getUser_id());
            response.put("username", updatedUser.getUsername());
            response.put("email", updatedUser.getEmail());
            response.put("role", updatedUser.getRole().name());
            response.put("createdAt", updatedUser.getCreated_at());
            response.put("updatedAt", updatedUser.getUpdated_at());
            return ResponseEntity.status(HttpStatus.OK).body (response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body (e.getMessage());
        } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong");
        }
    }

    @GetMapping("/getbyid")
    public ResponseEntity<?> getUserById(@RequestParam int userId) {
        try {
//            Integer userId = userRequest.get("userId");
            Customer user = adminServiceUserContract.getUserById(userId);
            return ResponseEntity.status(HttpStatus.OK).body(user);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
