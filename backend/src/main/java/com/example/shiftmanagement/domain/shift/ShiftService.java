package com.example.shiftmanagement.domain.shift;

import com.example.shiftmanagement.domain.shiftrequest.ShiftRequest;
import com.example.shiftmanagement.domain.shiftrequest.ShiftRequestRepository;
import com.example.shiftmanagement.domain.shiftrequest.ShiftRequestStatus;
import com.example.shiftmanagement.domain.user.User;
import com.example.shiftmanagement.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShiftService {

    private final ShiftRepository shiftRepository;
    private final ShiftRequestRepository shiftRequestRepository;
    private final UserRepository userRepository;

    @Transactional
    public ShiftResponse createFromRequest(UUID shiftRequestId) {
        ShiftRequest shiftRequest = shiftRequestRepository.findById(shiftRequestId)
                .orElseThrow(() -> new IllegalArgumentException("シフト申請が見つかりません"));

        if (shiftRequest.getStatus() != ShiftRequestStatus.APPROVED) {
            throw new IllegalArgumentException("承認済みの申請のみ確定シフトに変換できます");
        }

        Shift shift = Shift.builder()
                .user(shiftRequest.getUser())
                .date(shiftRequest.getDate())
                .startTime(shiftRequest.getStartTime())
                .endTime(shiftRequest.getEndTime())
                .build();

        shiftRepository.save(shift);

        return ShiftResponse.from(shift);
    }

    public List<ShiftResponse> getMyShifts(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("ユーザーが見つかりません"));

        return shiftRepository.findByUserIdOrderByDateDesc(user.getId())
                .stream()
                .map(ShiftResponse::from)
                .toList();
    }

    public List<ShiftResponse> getShiftsByDateRange(LocalDate startDate, LocalDate endDate) {
        return shiftRepository.findByDateBetweenOrderByDateAsc(startDate, endDate)
                .stream()
                .map(ShiftResponse::from)
                .toList();
    }

    public List<ShiftResponse> getShiftsByDate(LocalDate date) {
        return shiftRepository.findByDateOrderByStartTimeAsc(date)
                .stream()
                .map(ShiftResponse::from)
                .toList();
    }

    @Transactional
    public ShiftResponse updateActual(UUID id, ShiftActualRequest request) {
        Shift shift = shiftRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("シフトが見つかりません"));

        if (request.actualStartTime() != null) {
            shift.setActualStartTime(request.actualStartTime());
        }
        if (request.actualEndTime() != null) {
            shift.setActualEndTime(request.actualEndTime());
        }

        return ShiftResponse.from(shift);
    }
}