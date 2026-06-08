package com.medicine.demo1.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("medicine_usage_log")
public class MedicineUsageLog {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long medicineId;

    private Long userId;

    /**
     * 变更类型：1-增加 2-减少 3-删除
     */
    private Integer changeType;

    private Integer quantityChange;

    private Integer beforeQuantity;

    private Integer afterQuantity;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
