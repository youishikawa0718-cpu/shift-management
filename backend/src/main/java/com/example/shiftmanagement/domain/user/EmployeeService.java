package com.example.shiftmanagement.domain.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<EmployeeResponse> getAllEmployees() {
        return userRepository.findAll()
                .stream()
                .filter(user -> user.getRole() == Role.EMPLOYEE)
                .map(EmployeeResponse::from)
                .toList();
    }

    public EmployeeResponse getEmployee(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("従業員が見つかりません"));
        return EmployeeResponse.from(user);
    }

    @Transactional
    public EmployeeResponse createEmployee(EmployeeCreateRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("このメールアドレスは既に登録されています");
        }

        User user = User.builder()
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .name(request.name())
                .role(Role.EMPLOYEE)
                .isActive(true)
                .build();

        userRepository.save(user);

        return EmployeeResponse.from(user);
    }

    @Transactional
    public EmployeeResponse updateEmployee(UUID id, EmployeeUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("従業員が見つかりません"));

        if (request.name() != null) {
            user.setName(request.name());
        }
        if (request.isActive() != null) {
            user.setIsActive(request.isActive());
        }

        return EmployeeResponse.from(user);
    }

    @Transactional
    public void deleteEmployee(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("従業員が見つかりません"));

        if (user.getRole() == Role.MANAGER) {
            throw new IllegalArgumentException("管理者は削除できません");
        }

        userRepository.delete(user);
    }
}