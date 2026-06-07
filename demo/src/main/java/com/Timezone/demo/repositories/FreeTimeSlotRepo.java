package com.Timezone.demo.repositories;

import com.Timezone.demo.models.FreeTimeSlots;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FreeTimeSlotRepo extends JpaRepository<FreeTimeSlots,Long> {

    List<FreeTimeSlots> findAllByUserId(Long userId);

    List<FreeTimeSlots> findByUserIdAndDay(Long myId, String day);

}
