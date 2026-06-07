package com.Timezone.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MeetingUpdateDto {
     Long id;
     String title;
     String buddyNames;
     List<Long> participantIds;
    String platform;
}