package com.medicine.demo1.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("notification_log")
public class NotificationLog {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long userId;

    private Long medicineId;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 类型：1-临期提醒 2-过期提醒 3-补充提醒
     */
    private Integer type;

    private LocalDateTime sendTime;

    /**
     * 是否已读：0-未读 1-已读
     */
    private Integer isRead;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
