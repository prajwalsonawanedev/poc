package com.ecom.validation;

import com.ecom.request.UserRequestDto;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public interface UserValidation {

    static List<String> userValidation(UserRequestDto usersDto) {
        List<String> errorList = new ArrayList<>();

        if (ObjectUtils.isEmpty(usersDto)) {
            errorList.add("Please provide valid user details");
            return errorList;
        }

        if (!StringUtils.hasText(usersDto.getEmail())) {
            errorList.add("Please provide a valid email ID");
        }

        if (!StringUtils.hasText(usersDto.getPassword())) {
            errorList.add("Please provide a valid password");
        }

        return errorList;
    }
}
