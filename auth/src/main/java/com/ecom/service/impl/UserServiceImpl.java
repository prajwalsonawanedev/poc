package com.ecom.service.impl;

import com.ecom.entity.User;
import com.ecom.exception.ResourceNotFoundException;
import com.ecom.repository.UsersRepository;
import com.ecom.request.UserRequestDto;
import com.ecom.response.ApiResponse;
import com.ecom.response.UserResponseDto;
import com.ecom.service.UsersService;
import com.ecom.utils.GenericMapper;
import com.ecom.validation.UserValidation;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UsersService {

    private final UsersRepository usersRepository;

    private final GenericMapper genericMapper;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JWTService jwtService;

    private List<String> errorList = new ArrayList<>();

    public UserServiceImpl(UsersRepository usersRepository, GenericMapper genericMapper, BCryptPasswordEncoder bCryptPasswordEncoder, AuthenticationManager authenticationManager, JWTService jwtService) {
        this.usersRepository = usersRepository;
        this.genericMapper = genericMapper;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }


    //register user
    @Override
    public ApiResponse<UserResponseDto> createUser(UserRequestDto userRequestDto) {

        if (validateUser(userRequestDto)) {

            User user = genericMapper.convert(userRequestDto, User.class);

            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            User savedUser = usersRepository.save(user);

            UserResponseDto userResponseDto = genericMapper.convert(savedUser, UserResponseDto.class);

            return ApiResponse.response("User Created Successfully", true, "Validation Done", userResponseDto);
        }

        User user = genericMapper.convert(userRequestDto, User.class);
        User savedUser = usersRepository.save(user);
        UserResponseDto userResponseDto = genericMapper.convert(savedUser, UserResponseDto.class);

        return ApiResponse.response("Unable to create User", false, "Validation Failed", userResponseDto);

    }

    //get all user data
    @Override
    public ApiResponse<List<UserResponseDto>> getAllUsers() {

        List<UserResponseDto> userList = usersRepository.findAll()
                .stream()
                .map(user -> genericMapper.convert(user, UserResponseDto.class))
                .toList();

        return ApiResponse.response("Users Details Found", true, null, userList);
    }

    //get user details by Id
    @Override
    public ApiResponse<UserResponseDto> getUserById(Long userId) {

        User user = usersRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Order Details Not found :%s ", userId)));

        UserResponseDto userResponseDto = genericMapper.convert(user, UserResponseDto.class);

        return ApiResponse.response("Users Details Found", true, null, userResponseDto);

    }

    public String verify(UserRequestDto userRequestDto) {

        User user = genericMapper.convert(userRequestDto, User.class);


        Authentication authentication = null;

        try {
            authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    user.getUsername(), user.getPassword()
                            )
                    );
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception Occured :" + e);
        }


        if (authentication.isAuthenticated()) {
            return jwtService.generateToken(user);
        }
        return "Failed";
    }


    private boolean validateUser(UserRequestDto userRequestDto) {
        errorList = UserValidation.userValidation(userRequestDto);

        if (errorList.isEmpty()) {
            return true;
        }

        return false;
    }
}
