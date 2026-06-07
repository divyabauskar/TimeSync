package com.Timezone.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateDto {
    Long id;
    String email;
    String country;
    String password;
    String timezone;
}
