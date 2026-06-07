package com.Timezone.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompareDto {
    Long myId;
    List<Long> buddyIds;
    String day;

}
