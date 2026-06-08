package com.medicine.demo1.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "微信登录参数")
public class LoginDTO {

    @NotBlank(message = "code不能为空")
    @Schema(description = "微信登录code", requiredMode = Schema.RequiredMode.REQUIRED)
    private String code;
}
