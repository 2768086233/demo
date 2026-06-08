package com.medicine.demo1.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.medicine.demo1.common.BusinessException;
import com.medicine.demo1.common.ResultCode;
import com.medicine.demo1.dto.UserUpdateDTO;
import com.medicine.demo1.entity.User;
import com.medicine.demo1.mapper.UserMapper;
import com.medicine.demo1.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    @Override
    public User login(String code) {
        // 开发环境：用 code 模拟 openid（生产环境应调微信接口）
        String openid = "mock_openid_" + code;

        // 查找是否已注册
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getOpenid, openid));

        if (user != null) {
            log.info("用户已存在：openid={}", openid);
            return user;
        }

        // 首次登录，自动注册
        user = new User();
        user.setOpenid(openid);
        user.setNickname("用户" + StrUtil.subWithLength(openid, openid.length() - 6, 6));
        userMapper.insert(user);
        log.info("新用户注册：openid={}", openid);
        return user;
    }

    @Override
    public User getUserById(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST);
        }
        return user;
    }

    @Override
    public User updateUser(Long userId, UserUpdateDTO dto) {
        User user = getUserById(userId);
        if (StrUtil.isNotBlank(dto.getNickname())) {
            user.setNickname(dto.getNickname());
        }
        if (StrUtil.isNotBlank(dto.getAvatarUrl())) {
            user.setAvatarUrl(dto.getAvatarUrl());
        }
        if (StrUtil.isNotBlank(dto.getPhone())) {
            user.setPhone(dto.getPhone());
        }
        userMapper.updateById(user);
        return user;
    }
}
