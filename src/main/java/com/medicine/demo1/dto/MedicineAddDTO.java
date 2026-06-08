package com.medicine.demo1.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
@Schema(description = "新增药品参数")
public class MedicineAddDTO {

    @NotBlank(message = "药品名称不能为空")
    @Schema(description = "药品名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "通用名")
    private String genericName;

    @NotBlank(message = "生产批号不能为空")
    @Schema(description = "生产批号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String batchNumber;

    @Schema(description = "生产日期")
    private LocalDate produceDate;

    @NotNull(message = "有效期不能为空")
    @Schema(description = "有效期至", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate expiryDate;

    @NotNull(message = "数量不能为空")
    @Schema(description = "剩余数量", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer quantity;

    @Schema(description = "单位（盒/瓶/片/支）")
    private String unit;

    @Schema(description = "存放位置（如：客厅药箱、冰箱）")
    private String location;

    @Schema(description = "厂商")
    private String manufacturer;

    @Schema(description = "分类标签（感冒药、肠胃药等）")
    private String category;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "药品照片URL，JSON数组")
    private String images;
}
