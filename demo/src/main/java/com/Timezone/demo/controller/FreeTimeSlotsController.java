package com.Timezone.demo.controller;

import com.Timezone.demo.models.FreeTimeSlots;
import com.Timezone.demo.models.YourBuddy;
import com.Timezone.demo.repositories.FreeTimeSlotRepo;
import com.Timezone.demo.repositories.YourBuddyRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class FreeTimeSlotsController {

    @Autowired
    FreeTimeSlotRepo freeTimeSlotRepo;

    @Autowired
    YourBuddyRepo yourBuddyRepo;
    @PostMapping("/add-timeslot")
    public String addslot(@RequestBody FreeTimeSlots free) {
        // Get the list of existing slots for that day
        List<FreeTimeSlots> existingSlots = freeTimeSlotRepo.findByUserIdAndDay(free.getUserId(), free.getDay());

        if (existingSlots != null && !existingSlots.isEmpty()) {
            // UPDATE logic:
            FreeTimeSlots slotToUpdate = existingSlots.get(0);
            slotToUpdate.setStartTime(free.getStartTime());
            slotToUpdate.setEndTime(free.getEndTime());
            freeTimeSlotRepo.save(slotToUpdate);
            return "Time Slot Updated";
        } else {
            // ADD logic
            freeTimeSlotRepo.save(free);
            return "Time Slot Added";
        }
    }

    @GetMapping("/get-timeslots/{userId}")
    public List<FreeTimeSlots> gettimeslots(@PathVariable Long userId) {
        return freeTimeSlotRepo.findAllByUserId(userId);
    }
    @GetMapping("/buddy/accepted/{userId}")
    public List<YourBuddy> buddyaccepted(@PathVariable Long userId) {
        return yourBuddyRepo.findAllAcceptedByUserId(userId);
    }
    @DeleteMapping("/delete-timeslot/{id}")
    public ResponseEntity<?> deleteTimeslot(@PathVariable Long id) {
        freeTimeSlotRepo.deleteById(id);
        return ResponseEntity.ok().build();
    }
}

