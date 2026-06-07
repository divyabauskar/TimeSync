package com.Timezone.demo.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class YourBuddy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
     Long id;

     Long senderId;
     String senderUsername;

     Long receiverId;
     String receiverUsername;
     String receiverCountry;

     String senderCountry;
     String senderEmail;
     String status;
}
