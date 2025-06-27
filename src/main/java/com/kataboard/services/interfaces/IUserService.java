package com.kataboard.services.interfaces;

import com.kataboard.dtos.user.UserProfileDto;

import java.util.List;
import java.util.Set;

public interface IUserService {
    UserProfileDto getCurrentUserProfile();

    List<String> getAllUserEmailsExcept(String currentEmail);
    List<String> getAllUserEmails();

    void updateStatus(Long userId, Boolean active);
    void updateRoles(Long userId, Set<String> roles);
}
