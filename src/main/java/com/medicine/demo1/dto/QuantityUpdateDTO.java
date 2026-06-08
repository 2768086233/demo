package com.medicine.demo1.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "数量变更参数")
public class QuantityUpdateDTO {

    @NotNull(message = "变更类型不能为空")
    @Schema(description = "变更类型：1-增加 2-减少", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer changeType;

    @NotNull(message = "变更数量不能为空")
    @Schema(description = "变更数量（正整数）", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer quantityChange;

    @Schema(description = "操作备注")
    private String remark;
}
