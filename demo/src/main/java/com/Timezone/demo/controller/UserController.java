package com.Timezone.demo.controller;

import com.Timezone.demo.dto.LoginDto;
import com.Timezone.demo.dto.ProfileDto;
import com.Timezone.demo.dto.UpdateDto;
import com.Timezone.demo.dto.YourBuddyDto;
import com.Timezone.demo.models.User;
import com.Timezone.demo.models.YourBuddy;
import com.Timezone.demo.repositories.UserRepo;
import com.Timezone.demo.repositories.YourBuddyRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    UserRepo userRepo;

    @Autowired
    YourBuddyRepo yourBuddyRepo;

    @PostMapping("/signup")
    public String signup(@RequestBody User user){
        userRepo.save(user);
        return "User signup successfully!";
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto dto){
        User user = userRepo.findByUsername(dto.getUsername());

        if(user == null){
            return ResponseEntity.status(404).body("User not found!");
        }
        if(!user.getPassword().equals(dto.getPassword())){
            return ResponseEntity.status(401).body("Wrong password!");
        }
        if(!user.getCountry().equals(dto.getCountry())){
            return ResponseEntity.status(401).body("Wrong country!");
        }
        if(!user.getTimezone().equals(dto.getTimezone())){
            return ResponseEntity.status(401).body("Wrong timezone!");
        }
        // Returning the whole user object as JSON
        return ResponseEntity.ok(user);
    }
    @GetMapping("/profile/{userId}")
    public ResponseEntity<ProfileDto> getUserProfile(@PathVariable Long userId) {
        return userRepo.findById(userId)
                .map(user -> {
                    ProfileDto dto = new ProfileDto();
                    dto.setUsername(user.getUsername());
                    dto.setEmail(user.getEmail());
                    dto.setCountry(user.getCountry());
                    dto.setTimezone(user.getTimezone());
                    dto.setFullName(user.getFullName());
                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    @PostMapping("/buddy/add")
    public String addBuddy(@RequestBody YourBuddyDto buddyDto) {
        User sender = userRepo.findById(buddyDto.getId()).orElse(null);
        User rec = userRepo.findByUsername(buddyDto.getUsername());
        if (rec == null || sender == null) return "User not found";
        if(sender.getUsername().equals(rec.getUsername())){
            return "Self Request Not Allowed!";
        }
        Optional<YourBuddy> existing = yourBuddyRepo.findExistingRelation(sender.getId(), rec.getId());
        if (existing.isPresent()) {
            return "Relationship already exists or is pending!";
        }

        YourBuddy buddy = new YourBuddy();
        buddy.setSenderId(sender.getId());
        buddy.setSenderUsername(sender.getUsername());
        buddy.setReceiverId(rec.getId());
        buddy.setReceiverUsername(rec.getUsername());

        buddy.setReceiverCountry(rec.getCountry());
        buddy.setSenderCountry(sender.getCountry());
        buddy.setSenderEmail(sender.getEmail());
        buddy.setStatus("Pending");
        yourBuddyRepo.save(buddy);
        return "success";
    }

    @GetMapping("/buddy/notifications/{userId}")
    public List<YourBuddy> getNotifications(@PathVariable Long userId) {
        return yourBuddyRepo.findAllPendingByReceiverId(userId);
    }

    @PostMapping("/buddy/accept")
    public String acceptBuddy(@RequestBody YourBuddyDto dto) {
        Optional<YourBuddy> existingBuddy = yourBuddyRepo.findById(dto.getId());
        if (existingBuddy.isPresent()) {
            YourBuddy buddy = existingBuddy.get();
            buddy.setStatus("Accepted");
            yourBuddyRepo.save(buddy);
            return "success";
        }
        return "error: Request not found";
    }

    @GetMapping("/buddy/list/{userId}")
    public List<YourBuddy> getAcceptedBuddies(@PathVariable Long userId) {
        return yourBuddyRepo.findAllAcceptedByUserId(userId);
    }

    @PostMapping("/buddy/reject")
    public ResponseEntity<?> deleteBuddy(@RequestBody YourBuddyDto dto) {
        yourBuddyRepo.deleteById(dto.getId());
        return ResponseEntity.ok("Deleted");

    }
    @PostMapping("/update-profile")
    public String update(@RequestBody UpdateDto dto) {
        User existingUser = userRepo.findById(dto.getId()).orElseThrow(()->new RuntimeException("user not found"));

        if (existingUser != null) {
            if (dto.getEmail() != null) existingUser.setEmail(dto.getEmail());
            if (dto.getCountry() != null) existingUser.setCountry(dto.getCountry());
            if (dto.getTimezone() != null) existingUser.setTimezone(dto.getTimezone());
            if (dto.getPassword() != null && !dto.getPassword().isEmpty()) existingUser.setPassword(dto.getPassword());

            userRepo.save(existingUser);
            return "Profile Updated Successfully";
        }
        return  "User not found";
    }
}