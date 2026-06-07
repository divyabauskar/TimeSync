package com.Timezone.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileDto {
     String username;
     String email;
     String country;
     String timezone;
     String fullName;
}
