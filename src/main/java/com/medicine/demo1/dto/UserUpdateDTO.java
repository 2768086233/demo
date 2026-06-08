package com.medicine.demo1.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户信息更新参数")
public class UserUpdateDTO {

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "头像URL")
    private String avatarUrl;

    @Schema(description = "手机号")
    private String phone;
}
