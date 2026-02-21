package org.example.lablinkbackend.config.security.controller;

import org.example.lablinkbackend.config.security.component.JwtCore;
import org.example.lablinkbackend.domain.user.model.dto.auth.SignInRequest;
import org.example.lablinkbackend.domain.user.model.dto.auth.SignUpRequest;
import org.example.lablinkbackend.domain.user.model.entity.User;
import org.example.lablinkbackend.domain.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private JwtCore jwtCore;

    public AuthController(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtCore jwtCore
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtCore = jwtCore;
    }

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

    @PostMapping("/signup")
    ResponseEntity<?> signUp(@RequestBody SignUpRequest signUpRequest) {
        if (userRepository.existsUserByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Choose different name");
        }
        if (userRepository.existsUserByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Choose different email");
        }

        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        userRepository.save(user);

        return ResponseEntity.ok("Registration Completed!");
    }
}
