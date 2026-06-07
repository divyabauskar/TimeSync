package com.Timezone.demo.repositories;

import com.Timezone.demo.models.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Repository
public interface MeetingRepo extends JpaRepository<Meeting, Long> {
    List<Meeting> findByHostId(Long hostId);

    Collection<? extends Meeting> findByParticipantIdsContaining(Long userId);

    List<Meeting> findByHostIdAndDate(Long userId, String date);
}