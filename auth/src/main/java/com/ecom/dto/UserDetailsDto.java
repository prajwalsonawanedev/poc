package com.ecom.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDetailsDto implements Serializable {

    private String username;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private boolean enabled;

    public static UserDetailsDto fromUserDetails(UserDetails userDetails) {
        return UserDetailsDto.builder()
                .username(userDetails.getUsername())
                .password(userDetails.getPassword())
                .authorities(userDetails.getAuthorities())
                .accountNonExpired(userDetails.isAccountNonExpired())
                .accountNonLocked(userDetails.isAccountNonLocked())
                .credentialsNonExpired(userDetails.isCredentialsNonExpired())
                .enabled(userDetails.isEnabled())
                .build();
    }
}
