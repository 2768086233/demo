package com.medicine.demo1.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "药品查询参数")
public class MedicineQueryDTO {

    @Schema(description = "页码，默认1")
    private Integer page = 1;

    @Schema(description = "每页条数，默认10")
    private Integer size = 10;

    @Schema(description = "搜索关键字（药品名称/通用名）")
    private String keyword;

    @Schema(description = "状态筛选：0-正常 1-临期 2-过期 3-已用完")
    private Integer status;

    @Schema(description = "分类标签")
    private String category;

    @Schema(description = "存放位置")
    private String location;

    @Schema(description = "排序字段：expiry_date(有效期) / create_time(创建时间)，默认expiry_date")
    private String sortField = "expiry_date";

    @Schema(description = "排序方向：asc/desc，默认asc")
    private String sortOrder = "asc";
}
