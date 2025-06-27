package com.kataboard.services.implement;

import com.kataboard.dtos.user.UserProfileDto;
import com.kataboard.exceptions.BadRequestException;
import com.kataboard.exceptions.NotFoundException;
import com.kataboard.models.User;
import com.kataboard.repositories.UserRepository;
import com.kataboard.services.interfaces.IUserService;
import com.kataboard.util.Roles;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    Set<String> VALID_ROLES = Set.of(Roles.USER.name(), Roles.ADMIN.name());

    @Override
    public UserProfileDto getCurrentUserProfile() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        return new UserProfileDto(
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getActive()
        );
    }

    @Override
    public List<String> getAllUserEmailsExcept(String currentEmail) {
        return userRepository.findAll()
                .stream()
                .map(User::getEmail)
                .filter(email -> !email.equalsIgnoreCase(currentEmail)) // 👈 excluir el actual
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getAllUserEmails() {
        return userRepository.findAll()
                .stream()
                .map(User::getEmail)
                .collect(Collectors.toList());
    }

    @Override
    public void updateStatus(Long userId, Boolean active) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentEmail = auth.getName();

        User currentUser = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        if (Objects.equals(currentUser.getId(), userId)) {
            throw new BadRequestException("No puedes inactivar tu propio usuario");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        user.setActive(active);
        userRepository.save(user);
    }

    @Override
    public void updateRoles(Long userId, Set<String> roles) {
        if (roles == null || roles.isEmpty() || !VALID_ROLES.containsAll(roles)) {
            throw new BadRequestException("Roles inválidos: solo se permiten ADMIN y USER");
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentEmail = auth.getName();

        User currentUser = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        if (Objects.equals(currentUser.getId(), userId) && !roles.contains("ADMIN")) {
            throw new BadRequestException("No puedes quitarte tu propio rol ADMIN");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        user.setRoles(roles);
        userRepository.save(user);
    }
}