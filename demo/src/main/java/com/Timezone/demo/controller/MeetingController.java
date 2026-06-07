package com.Timezone.demo.controller;

import com.Timezone.demo.dto.*;
import com.Timezone.demo.models.*;
import com.Timezone.demo.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*")
public class MeetingController {

    @Autowired FreeTimeSlotRepo freeTimeSlotRepo;
    @Autowired UserRepo userRepo;
    @Autowired MeetingRepo meetingRepo;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @GetMapping("/compare/slots-for-me")
    public List<FreeTimeSlots> getBuddySlots(@RequestParam Long buddyId) {
        return freeTimeSlotRepo.findAllByUserId(buddyId);
    }

    private LocalDate getAnchorForDay(String dayName) {
        DayOfWeek target = DayOfWeek.valueOf(dayName.toUpperCase());
        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        int diff = target.getValue() - today.getDayOfWeek().getValue();
        if (diff < 0) diff += 7;
        return today.plusDays(diff);
    }

    @PostMapping("/compare/find-overlap")
    public ResponseEntity<OverlapResponseDto> findOverlap(@RequestBody CompareDto dto) {
        User host = userRepo.findById(dto.getMyId()).orElse(null);
        if (host == null || dto.getBuddyIds() == null || dto.getBuddyIds().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        ZoneId hostZone = ZoneId.of(host.getTimezone());
        LocalDate anchorDate = getAnchorForDay(dto.getDay());//converts tuesday to actual date

        List<TimeInterval> masterOverlaps = getParticipantIntervals(dto.getMyId(), anchorDate, hostZone);

        if (masterOverlaps.isEmpty()) {
            return ResponseEntity.ok(new OverlapResponseDto(false, null, null));
        }

        for (Long buddyId : dto.getBuddyIds()) {
            User buddy = userRepo.findById(buddyId).orElse(null);
            if (buddy == null) continue;

            ZoneId buddyZone = ZoneId.of(buddy.getTimezone());
            List<TimeInterval> buddyIntervals = getParticipantIntervals(buddyId, anchorDate, buddyZone);

            masterOverlaps = intersectIntervalLists(masterOverlaps, buddyIntervals);

            if (masterOverlaps.isEmpty()) break;
        }

        for (TimeInterval interval : masterOverlaps) {
            ZonedDateTime startInHostZone = ZonedDateTime.ofInstant(interval.start, hostZone);
            ZonedDateTime endInHostZone = ZonedDateTime.ofInstant(interval.end, hostZone);

            if (startInHostZone.toLocalDate().equals(anchorDate) &&
                    Duration.between(interval.start, interval.end).toMinutes() >= 15) {

                return ResponseEntity.ok(new OverlapResponseDto(true,
                        startInHostZone.format(TIME_FORMATTER),
                        endInHostZone.format(TIME_FORMATTER)));
            }
        }

        return ResponseEntity.ok(new OverlapResponseDto(false, null, null));
    }

     List<TimeInterval> intersectIntervalLists(List<TimeInterval> listA, List<TimeInterval> listB) {
        List<TimeInterval> result = new ArrayList<>();
        for (TimeInterval a : listA) {
            for (TimeInterval b : listB) {
                Instant maxStart = a.start.isAfter(b.start) ? a.start : b.start;
                Instant minEnd = a.end.isBefore(b.end) ? a.end : b.end;

                if (maxStart.isBefore(minEnd)) {
                    result.add(new TimeInterval(maxStart, minEnd));
                }
            }
        }
        return result;
    }

     List<TimeInterval> getParticipantIntervals(Long userId, LocalDate anchorDate, ZoneId zone) {
        List<FreeTimeSlots> allSlots = freeTimeSlotRepo.findAllByUserId(userId);
        List<TimeInterval> intervals = new ArrayList<>();

        for (FreeTimeSlots s : allSlots) {
            // Check Yesterday, Today, and Tomorrow to catch timezone shifts
            for (int i = -1; i <= 1; i++) {
                LocalDate date = anchorDate.plusDays(i);
                String dayName = date.getDayOfWeek().name();
                String formattedDay = dayName.substring(0, 1).toUpperCase() + dayName.substring(1).toLowerCase();

                if (s.getDay().equalsIgnoreCase("Everyday") || s.getDay().equalsIgnoreCase(formattedDay)) {
                    ZonedDateTime start = ZonedDateTime.of(date, LocalTime.parse(s.getStartTime()), zone);
                    ZonedDateTime end = ZonedDateTime.of(date, LocalTime.parse(s.getEndTime()), zone);

                    // Handle midnight crossing slots
                    if (!end.isAfter(start)) end = end.plusDays(1);

                    intervals.add(new TimeInterval(start.toInstant(), end.toInstant()));
                }
            }
        }
        return intervals;

    }

    @PostMapping("/meetings/schedule")
    public String schedule(@RequestBody ScheduleDto dto) {

            Meeting meet = new Meeting();
            meet.setHostId(dto.getHostId());
            meet.setHostUsername(dto.getHostUsername());
            meet.setTitle(dto.getTitle());
            meet.setDate(dto.getDate());
            meet.setStartTime(dto.getStartTime());
            meet.setEndTime(dto.getEndTime());
            meet.setDay(dto.getDay());
            meet.setBuddyNames(dto.getBuddyNames());
            meet.setParticipantIds(dto.getParticipantIds());
            meet.setPlatform(dto.getPlatform());
            meet.setHostTimezone(dto.getHostTimezone());
            meetingRepo.save(meet);
            return "Meeting scheduled successfully";
    }

    @GetMapping("/meetings/user/{userId}")
    public List<Meeting> getMeetingsForUser(@PathVariable Long userId) {
        Set<Meeting> uniqueMeetings = new HashSet<>();
        uniqueMeetings.addAll(meetingRepo.findByHostId(userId));
        uniqueMeetings.addAll(meetingRepo.findByParticipantIdsContaining(userId));

        return new ArrayList<>(uniqueMeetings);
    }

    @DeleteMapping("/meetings/delete/{id}")
    public String deleteMeeting(@PathVariable Long id) {
        meetingRepo.deleteById(id);
        return "Meeting deleted successfully";
    }


    private static class TimeInterval {
        Instant start;
        Instant end;
        TimeInterval(Instant s, Instant e) { this.start = s; this.end = e; }
    }

    @GetMapping("/check-conflict")
    public ResponseEntity<Boolean> checkConflict(
            @RequestParam Long userId,
            @RequestParam String date,
            @RequestParam String start,
            @RequestParam String end
    ) {
        LocalDate meetingDate = LocalDate.parse(date);
        LocalTime newStart = LocalTime.parse(start);
        LocalTime newEnd = LocalTime.parse(end);

        // DO NOT create 'new MeetingController()'. Call the method directly.
        boolean isBusy = isUserBusy(userId, meetingDate, newStart, newEnd);

        return ResponseEntity.ok(isBusy);
    }

    private boolean isUserBusy(Long userId, LocalDate meetingDate, LocalTime newStart, LocalTime newEnd) {
        // Use the repository already autowired in this class
        List<Meeting> existingMeetings = getMeetingsForUser(userId);

        for (Meeting existing : existingMeetings) {
            // Only check meetings on the same date
            if (existing.getDate().equals(meetingDate)) {
                LocalTime exStart = LocalTime.parse(existing.getStartTime());
                LocalTime exEnd = LocalTime.parse(existing.getEndTime());

                // Handle the 00:00 (midnight) case for end times if necessary
                // Standard overlap formula: (StartA < EndB) AND (EndA > StartB)
                if (newStart.isBefore(exEnd) && newEnd.isAfter(exStart)) {
                    return true;
                }
            }
        }
        return false;
    }
}