package com.example.shiftmanagement.domain.shiftrequest;

import com.example.shiftmanagement.domain.user.User;
import com.example.shiftmanagement.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShiftRequestService {

    private final ShiftRequestRepository shiftRequestRepository;
    private final UserRepository userRepository;

    @Transactional
    public ShiftRequestResponse create(String email, ShiftRequestCreateRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("ユーザーが見つかりません"));

        if (request.endTime().isBefore(request.startTime()) || request.endTime().equals(request.startTime())) {
            throw new IllegalArgumentException("終了時刻は開始時刻より後にしてください");
        }

        if (request.date().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("過去の日付には申請できません");
        }

        ShiftRequest shiftRequest = ShiftRequest.builder()
                .user(user)
                .date(request.date())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .status(ShiftRequestStatus.PENDING)
                .build();

        shiftRequestRepository.save(shiftRequest);

        return ShiftRequestResponse.from(shiftRequest);
    }

    public List<ShiftRequestResponse> getMyRequests(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("ユーザーが見つかりません"));

        return shiftRequestRepository.findByUserIdOrderByDateDesc(user.getId())
                .stream()
                .map(ShiftRequestResponse::from)
                .toList();
    }

    public List<ShiftRequestResponse> getPendingRequests() {
        return shiftRequestRepository.findByStatusOrderByRequestedAtAsc(ShiftRequestStatus.PENDING)
                .stream()
                .map(ShiftRequestResponse::from)
                .toList();
    }

    public List<ShiftRequestResponse> getAllRequests() {
        return shiftRequestRepository.findAll()
                .stream()
                .map(ShiftRequestResponse::from)
                .toList();
    }

    @Transactional
    public ShiftRequestResponse approve(UUID id) {
        ShiftRequest shiftRequest = shiftRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("シフト申請が見つかりません"));

        if (shiftRequest.getStatus() != ShiftRequestStatus.PENDING) {
            throw new IllegalArgumentException("この申請は既に処理されています");
        }

        shiftRequest.setStatus(ShiftRequestStatus.APPROVED);
        shiftRequest.setReviewedAt(LocalDateTime.now());

        return ShiftRequestResponse.from(shiftRequest);
    }

    @Transactional
    public ShiftRequestResponse reject(UUID id) {
        ShiftRequest shiftRequest = shiftRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("シフト申請が見つかりません"));

        if (shiftRequest.getStatus() != ShiftRequestStatus.PENDING) {
            throw new IllegalArgumentException("この申請は既に処理されています");
        }

        shiftRequest.setStatus(ShiftRequestStatus.REJECTED);
        shiftRequest.setReviewedAt(LocalDateTime.now());

        return ShiftRequestResponse.from(shiftRequest);
    }

    @Transactional
    public void delete(String email, UUID id) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("ユーザーが見つかりません"));

        ShiftRequest shiftRequest = shiftRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("シフト申請が見つかりません"));

        if (!shiftRequest.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("他のユーザーの申請は削除できません");
        }

        if (shiftRequest.getStatus() != ShiftRequestStatus.PENDING) {
            throw new IllegalArgumentException("処理済みの申請は削除できません");
        }

        shiftRequestRepository.delete(shiftRequest);
    }
}