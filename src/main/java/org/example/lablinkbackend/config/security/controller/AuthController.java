package org.example.lablinkbackend.config.security.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.example.lablinkbackend.common.exception.UserAlreadyExistsException;
import org.example.lablinkbackend.config.security.component.JwtCore;
import org.example.lablinkbackend.config.security.service.AuthService;
import org.example.lablinkbackend.domain.user.model.dto.auth.SignInRequest;
import org.example.lablinkbackend.domain.user.model.dto.auth.SignUpRequest;
import org.example.lablinkbackend.domain.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtCore jwtCore;
    private final AuthService authService;

    public AuthController(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            JwtCore jwtCore,
            AuthService authService
    ) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtCore = jwtCore;
        this.authService = authService;
    }

    @Operation(
            summary = "Авторизация",
            description = "Возвращает JWT токен"
    )
    @PostMapping("/signin")
    ResponseEntity<?> signIn(@RequestBody SignInRequest signInRequest) {
        Authentication authentication = null;

        try {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signInRequest.getUsername(), signInRequest.getPassword()));
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtCore.generateToken(authentication);
        return ResponseEntity.ok(jwt);
    }

    @Operation(
            summary = "Регистрация",
            description = "Возвращает сообщение о успешной регистрации, либо ошибку выполнения"
    )
    @PostMapping("/signup")
    ResponseEntity<?> signUp(@RequestBody SignUpRequest signUpRequest) {
        if (userRepository.existsUserByUsername(signUpRequest.getUsername())) {
            throw new UserAlreadyExistsException("Username '" + signUpRequest.getUsername() + "' is already taken");
        }
        if (userRepository.existsUserByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Choose different email");
        }

        this.authService.register(signUpRequest);

        return ResponseEntity.ok("Registration Completed!");
    }
}
