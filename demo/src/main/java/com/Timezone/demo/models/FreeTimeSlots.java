package com.Timezone.demo.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FreeTimeSlots {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    Long userId;
    @Column(nullable = false)
    String day;
    @Column(nullable = false)
    String startTime;
    @Column(nullable = false)
    String endTime;
}
