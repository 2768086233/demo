package com.medicine.demo1.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("medicine")
public class Medicine {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long userId;

    private String name;

    private String genericName;

    private String batchNumber;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate produceDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expiryDate;

    private Integer quantity;

    private String unit;

    private String location;

    private String manufacturer;

    private String category;

    private String remark;

    private String images;

    /**
     * 状态：0-正常 1-临期 2-过期 3-已用完
     */
    private Integer status;

    @TableLogic
    private Integer isDeleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
