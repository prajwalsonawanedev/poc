package com.ecom.controller;

import com.ecom.dto.UserDetailsDto;
import com.ecom.request.UserRequestDto;
import com.ecom.response.ApiResponse;
import com.ecom.response.UserResponseDto;
import com.ecom.service.UsersService;
import com.ecom.service.impl.JWTService;
import com.ecom.utils.ObjectConverter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UsersController {

    private final UsersService userService;

    private final JWTService jwtService;

    private final UserDetailsService userDetailsService;

    private final ObjectConverter objectConverter;


    public UsersController(UsersService userService, JWTService jwtService, UserDetailsService userDetailsService, ObjectConverter objectConverter) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.objectConverter = objectConverter;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponseDto>> createUser(@RequestBody UserRequestDto userRequestDto) {
        return new ResponseEntity<>(userService.createUser(userRequestDto), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponseDto>>> getAllUsers() {
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponseDto>> getUserById(@PathVariable("userId") Long userId) {
        return new ResponseEntity<>(userService.getUserById(userId), HttpStatus.OK);
    }

    @PostMapping("/login")
    public String login(@RequestBody UserRequestDto userRequestDto) {
        return userService.verify(userRequestDto);
    }

    @PostMapping("/validateToken")
    public UsernamePasswordAuthenticationToken validateToken(HttpServletRequest request) {


        final String authHeader = request.getHeader("Authorization");


        final String jwt = authHeader.substring(7);
        final String username = jwtService.extractUsername(jwt);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (username != null && authentication == null) {
            // Authenticate
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (jwtService.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                        = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                usernamePasswordAuthenticationToken.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                return usernamePasswordAuthenticationToken;
            }
        }
        return null;

    }

    @PostMapping("/validateStringToken")
    public ResponseEntity<String> validateToken(@RequestHeader(value = "Authorization", required = false) String header) {
        String userDetails = null;


        if (header == null || !header.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(userDetails);
        }

        String jwt = header.substring(7);
        String username = jwtService.extractUsername(jwt);
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(userDetails);
        }

        UserDetails userDetail = userDetailsService.loadUserByUsername(username);
        UserDetailsDto dto = UserDetailsDto.fromUserDetails(userDetail);

        userDetails = objectConverter.writeValue(dto);

        if (jwtService.isTokenValid(jwt, userDetail)) {
            return ResponseEntity.status(HttpStatus.OK).body(userDetails);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(userDetails);
    }
}
