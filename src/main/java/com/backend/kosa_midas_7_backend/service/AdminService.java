package com.backend.kosa_midas_7_backend.service;

import com.backend.kosa_midas_7_backend.dto2.request.admin.UpdateDepartment;
import com.backend.kosa_midas_7_backend.dto2.request.admin.UpdatePassword;
import com.backend.kosa_midas_7_backend.dto2.request.admin.UpdatePosition;
import com.backend.kosa_midas_7_backend.entity.user.Role;
import com.backend.kosa_midas_7_backend.entity.user.User;
import com.backend.kosa_midas_7_backend.entity.user.repository.UserRepository;
import com.backend.kosa_midas_7_backend.security.auth.Details;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AdminService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public void updatePassword(UpdatePassword updatePassword) {
        User user = validateAdmin(updatePassword.getAccountId());
        userRepository.save(user.changePassword(passwordEncoder.encode(updatePassword.getPassword())));
    }

    public void updateDepartment(UpdateDepartment updateDepartment) {
        User user = validateAdmin(updateDepartment.getAccountId());
        userRepository.save(user.changeDepartment(updateDepartment.getDepartment()));
    }

    public void updatePosition(UpdatePosition updatePosition) {
        User user = validateAdmin(updatePosition.getAccountId());
        userRepository.save(user.changePosition(updatePosition.getPosition()));
    }

    private User validateAdmin(String accountId) {
        User user = userRepository.findByAccountId(accountId).orElseThrow(RuntimeException::new);
        Details a = (Details) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(a.getUser().getRole() == Role.ADMIN)) {
            throw new RuntimeException("not have access");
        }
        return user;
    }
}
