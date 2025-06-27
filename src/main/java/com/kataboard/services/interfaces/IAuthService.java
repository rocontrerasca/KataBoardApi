package com.kataboard.services.interfaces;


import com.kataboard.dtos.AuthRequest;
import com.kataboard.dtos.AuthResponse;
import com.kataboard.dtos.RefreshRequest;
import com.kataboard.dtos.RegisterRequest;

public interface IAuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(AuthRequest request);

    AuthResponse refreshToken(RefreshRequest request);
}
