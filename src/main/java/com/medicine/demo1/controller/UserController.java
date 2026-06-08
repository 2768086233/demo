package com.medicine.demo1.controller;

import com.medicine.demo1.common.Result;
import com.medicine.demo1.dto.LoginDTO;
import com.medicine.demo1.dto.UserUpdateDTO;
import com.medicine.demo1.entity.User;
import com.medicine.demo1.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户管理", description = "用户登录、信息管理")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "微信登录")
    @PostMapping("/login")
    public Result<User> login(@Valid @RequestBody LoginDTO loginDTO) {
        User user = userService.login(loginDTO.getCode());
        return Result.success(user);
    }

    @Operation(summary = "获取用户信息")
    @GetMapping("/info")
    public Result<User> getUserInfo(@RequestParam Long userId) {
        User user = userService.getUserById(userId);
        return Result.success(user);
    }

    @Operation(summary = "更新用户信息")
    @PutMapping("/info")
    public Result<User> updateUserInfo(@RequestParam Long userId,
                                       @Valid @RequestBody UserUpdateDTO updateDTO) {
        User user = userService.updateUser(userId, updateDTO);
        return Result.success(user);
    }
}
