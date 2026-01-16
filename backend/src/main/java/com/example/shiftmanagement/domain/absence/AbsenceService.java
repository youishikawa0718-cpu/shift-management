package com.example.shiftmanagement.domain.absence;

import com.example.shiftmanagement.domain.shift.Shift;
import com.example.shiftmanagement.domain.shift.ShiftRepository;
import com.example.shiftmanagement.domain.user.User;
import com.example.shiftmanagement.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AbsenceService {

    private final AbsenceRequestRepository absenceRequestRepository;
    private final ShiftRepository shiftRepository;
    private final UserRepository userRepository;

    @Transactional
    public AbsenceResponse create(String email, AbsenceCreateRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("ユーザーが見つかりません"));

        Shift shift = shiftRepository.findById(request.shiftId())
                .orElseThrow(() -> new IllegalArgumentException("シフトが見つかりません"));

        if (!shift.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("自分のシフトのみ欠勤申請できます");
        }

        AbsenceRequest absenceRequest = AbsenceRequest.builder()
                .user(user)
                .shift(shift)
                .reason(request.reason())
                .status(AbsenceRequestStatus.PENDING)
                .build();

        absenceRequestRepository.save(absenceRequest);

        return AbsenceResponse.from(absenceRequest);
    }

    public List<AbsenceResponse> getMyRequests(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("ユーザーが見つかりません"));

        return absenceRequestRepository.findByUserIdOrderByRequestedAtDesc(user.getId())
                .stream()
                .map(AbsenceResponse::from)
                .toList();
    }

    public List<AbsenceResponse> getPendingRequests() {
        return absenceRequestRepository.findByStatusOrderByRequestedAtAsc(AbsenceRequestStatus.PENDING)
                .stream()
                .map(AbsenceResponse::from)
                .toList();
    }

    public List<AbsenceResponse> getAllRequests() {
        return absenceRequestRepository.findAll()
                .stream()
                .map(AbsenceResponse::from)
                .toList();
    }

    @Transactional
    public AbsenceResponse approve(UUID id) {
        AbsenceRequest absenceRequest = absenceRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("欠勤申請が見つかりません"));

        if (absenceRequest.getStatus() != AbsenceRequestStatus.PENDING) {
            throw new IllegalArgumentException("この申請は既に処理されています");
        }

        absenceRequest.setStatus(AbsenceRequestStatus.APPROVED);
        absenceRequest.setReviewedAt(LocalDateTime.now());

        return AbsenceResponse.from(absenceRequest);
    }

    @Transactional
    public AbsenceResponse reject(UUID id) {
        AbsenceRequest absenceRequest = absenceRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("欠勤申請が見つかりません"));

        if (absenceRequest.getStatus() != AbsenceRequestStatus.PENDING) {
            throw new IllegalArgumentException("この申請は既に処理されています");
        }

        absenceRequest.setStatus(AbsenceRequestStatus.REJECTED);
        absenceRequest.setReviewedAt(LocalDateTime.now());

        return AbsenceResponse.from(absenceRequest);
    }
}