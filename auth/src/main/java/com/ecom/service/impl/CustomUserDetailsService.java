package com.ecom.service.impl;

import com.ecom.entity.CustomUserDetails;
import com.ecom.entity.User;
import com.ecom.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Objects;


@Component
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsersRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username);

        if (Objects.isNull(user)) {
            System.out.println("User Not Found");
            throw new UsernameNotFoundException("UserName Details Not Found");
        }

        return new CustomUserDetails(user);
    }
}
