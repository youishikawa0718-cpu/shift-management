package com.example.shiftmanagement.domain.shiftrequest;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface ShiftRequestRepository extends JpaRepository<ShiftRequest, UUID> {

    List<ShiftRequest> findByUserIdOrderByDateDesc(UUID userId);

    List<ShiftRequest> findByStatusOrderByRequestedAtAsc(ShiftRequestStatus status);

    List<ShiftRequest> findByDateBetweenOrderByDateAsc(LocalDate startDate, LocalDate endDate);

    List<ShiftRequest> findByUserIdAndDateBetween(UUID userId, LocalDate startDate, LocalDate endDate);

    boolean existsByUserIdAndDate(UUID userId, LocalDate date);
}