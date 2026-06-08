package com.medicine.demo1.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDate;

@Data
@Schema(description = "更新药品参数")
public class MedicineUpdateDTO {

    @Schema(description = "药品名称")
    private String name;

    @Schema(description = "通用名")
    private String genericName;

    @Schema(description = "生产批号")
    private String batchNumber;

    @Schema(description = "生产日期")
    private LocalDate produceDate;

    @Schema(description = "有效期至")
    private LocalDate expiryDate;

    @Schema(description = "剩余数量")
    private Integer quantity;

    @Schema(description = "单位")
    private String unit;

    @Schema(description = "存放位置")
    private String location;

    @Schema(description = "厂商")
    private String manufacturer;

    @Schema(description = "分类标签")
    private String category;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "药品照片URL，JSON数组")
    private String images;
}
