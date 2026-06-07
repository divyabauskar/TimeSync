package com.Timezone.demo.dto;

import com.Timezone.demo.models.YourBuddy;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleDto {
    Long hostId;
    String title;
    String day;
    String date;
    String startTime;
    String endTime;
    String buddyNames;
    List<Long> participantIds;
    String platform;
    String hostTimezone;
    String hostUsername;
}
