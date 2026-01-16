package com.example.shiftmanagement.domain.absence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AbsenceRequestRepository extends JpaRepository<AbsenceRequest, UUID> {

    List<AbsenceRequest> findByUserIdOrderByRequestedAtDesc(UUID userId);

    List<AbsenceRequest> findByStatusOrderByRequestedAtAsc(AbsenceRequestStatus status);
}