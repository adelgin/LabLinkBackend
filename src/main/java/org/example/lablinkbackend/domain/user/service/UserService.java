package org.example.lablinkbackend.domain.user.service;

import org.example.lablinkbackend.config.security.auth.UserDetailsImpl;
import org.example.lablinkbackend.domain.user.model.entity.User;
import org.example.lablinkbackend.domain.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(username).orElseThrow(() -> new UsernameNotFoundException(
           String.format("User '%s' not found", username)
        ));

        return UserDetailsImpl.build(user);
    }
}
