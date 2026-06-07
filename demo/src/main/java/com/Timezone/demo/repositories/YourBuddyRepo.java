package com.Timezone.demo.repositories;

import com.Timezone.demo.models.YourBuddy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface YourBuddyRepo extends JpaRepository<YourBuddy, Long> {

    // 1. For Notifications: Only fetch requests that are STILL Pending
    // This is the fix to stop "Accepted" buttons from reappearing in Notifications
    @Query("SELECT b FROM YourBuddy b WHERE b.receiverId = :userId AND b.status = 'Pending'")
    List<YourBuddy> findAllPendingByReceiverId(@Param("userId") Long userId);

    // 2. For the Buddy List: Find all "Accepted" relations
    @Query("SELECT b FROM YourBuddy b WHERE (b.senderId = :userId OR b.receiverId = :userId) AND b.status = 'Accepted'")
    List<YourBuddy> findAllAcceptedByUserId(@Param("userId") Long userId);

    // 3. To Prevent Duplicates
    @Query("SELECT b FROM YourBuddy b WHERE " +
            "(b.senderId = :id1 AND b.receiverId = :id2) OR " +
            "(b.senderId = :id2 AND b.receiverId = :id1)")
    Optional<YourBuddy> findExistingRelation(@Param("id1") Long id1, @Param("id2") Long id2);

    // Keep this if your existing code calls it, but the Query above is more accurate for notifications
    List<YourBuddy> findAllByReceiverId(Long userId);
}