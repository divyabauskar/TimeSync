package com.Timezone.demo.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Meeting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
     Long id;
     Long hostId;
     String hostUsername;
     String title;
     String platform;
     String day;
     String date;
     String startTime;
     String endTime;
     String hostTimezone;
     String buddyNames;

    @ElementCollection
    private List<Long> participantIds;
}