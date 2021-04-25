package com.example.aman.officehoursportal.security;

import com.example.aman.officehoursportal.dao.user.UserRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public CustomUserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        return userRepository.findByUserName(userName)
                .map(CustomUserDetails::create)
                .orElseThrow(() -> new UsernameNotFoundException("Invalid username or password!"));
    }
}
