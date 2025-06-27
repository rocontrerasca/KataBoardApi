package com.kataboard.services.implement;

import com.kataboard.dtos.AuthRequest;
import com.kataboard.dtos.AuthResponse;
import com.kataboard.dtos.RefreshRequest;
import com.kataboard.dtos.RegisterRequest;
import com.kataboard.exceptions.ForbiddenException;
import com.kataboard.exceptions.NotFoundException;
import com.kataboard.exceptions.UnAuthorizedException;
import com.kataboard.models.User;
import com.kataboard.repositories.UserRepository;
import com.kataboard.security.JwtUtil;
import com.kataboard.services.interfaces.IAuthService;
import com.kataboard.util.Roles;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Value("${kataboard.jwt.expiration}")
    private long jwtExpiration;

    @Override
    public AuthResponse register(RegisterRequest request) {
        var user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setActive(true);
        user.setRoles(Set.of(Roles.USER.name()));

        userRepository.save(user);

        String accessToken = jwtUtil.generateTokenWithRoles(user, jwtExpiration);       // 1 hora
        String refreshToken = jwtUtil.generateTokenWithRoles(user, 24 * 60 * 60 * 1000); // 1 día

        return new AuthResponse(accessToken, refreshToken, jwtExpiration);
    }

    @Override
    public AuthResponse login(AuthRequest request) {
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        if (!user.getActive()) {
            throw new ForbiddenException("El usuario no está activo");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnAuthorizedException("Credenciales inválidas");
        }

        String accessToken = jwtUtil.generateTokenWithRoles(user, jwtExpiration);       // 1 hora
        String refreshToken = jwtUtil.generateTokenWithRoles(user, 24 * 60 * 60 * 1000); // 1 día

        return new AuthResponse(accessToken, refreshToken, jwtExpiration);
    }

    @Override
    public AuthResponse refreshToken(RefreshRequest request) {
        if (!jwtUtil.isValid(request.getRefreshToken())) {
            throw new UnAuthorizedException("Token expirado");
        }

        String email = jwtUtil.extractUsername(request.getRefreshToken());
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        String accessToken = jwtUtil.generateTokenWithRoles(user, 60 * 60 * 1000);       // 1 hora
        String refreshToken = jwtUtil.generateTokenWithRoles(user, 24 * 60 * 60 * 1000); // 1 día

        return new AuthResponse(accessToken, refreshToken, jwtExpiration);
    }
}