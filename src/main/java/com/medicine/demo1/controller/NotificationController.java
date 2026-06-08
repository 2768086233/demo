package com.medicine.demo1.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medicine.demo1.common.Result;
import com.medicine.demo1.dto.PageResult;
import com.medicine.demo1.entity.NotificationLog;
import com.medicine.demo1.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "消息通知", description = "提醒消息列表、已读标记")
@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "通知列表")
    @GetMapping("/list")
    public Result<PageResult<NotificationLog>> listNotifications(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        IPage<NotificationLog> pageResult = notificationService.listNotifications(userId, page, size);
        return Result.success(PageResult.of(pageResult));
    }

    @Operation(summary = "标记通知已读")
    @PutMapping("/{id}/read")
    public Result<Void> markAsRead(@RequestParam Long userId, @PathVariable Long id) {
        notificationService.markAsRead(userId, id);
        return Result.success();
    }

    @Operation(summary = "删除通知")
    @DeleteMapping("/{id}")
    public Result<Void> deleteNotification(@RequestParam Long userId, @PathVariable Long id) {
        notificationService.deleteNotification(userId, id);
        return Result.success();
    }

    @Operation(summary = "获取未读通知数量")
    @GetMapping("/unread-count")
    public Result<Map<String, Integer>> getUnreadCount(@RequestParam Long userId) {
        int count = notificationService.getUnreadCount(userId);
        Map<String, Integer> data = new HashMap<>();
        data.put("unreadCount", count);
        return Result.success(data);
    }
}
