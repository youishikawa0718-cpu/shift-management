package com.example.shiftmanagement.domain.shift;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, UUID> {

    List<Shift> findByUserIdOrderByDateDesc(UUID userId);

    List<Shift> findByDateBetweenOrderByDateAsc(LocalDate startDate, LocalDate endDate);

    List<Shift> findByUserIdAndDateBetween(UUID userId, LocalDate startDate, LocalDate endDate);

    List<Shift> findByDateOrderByStartTimeAsc(LocalDate date);
}