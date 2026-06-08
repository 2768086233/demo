package com.medicine.demo1.service;

import com.medicine.demo1.dto.UserUpdateDTO;
import com.medicine.demo1.entity.User;

public interface UserService {

    User login(String code);

    User getUserById(Long userId);

    User updateUser(Long userId, UserUpdateDTO dto);
}
