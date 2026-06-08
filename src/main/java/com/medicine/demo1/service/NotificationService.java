package com.medicine.demo1.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medicine.demo1.entity.Medicine;
import com.medicine.demo1.entity.NotificationLog;

public interface NotificationService {

    IPage<NotificationLog> listNotifications(Long userId, Integer page, Integer size);

    void markAsRead(Long userId, Long notificationId);

    void deleteNotification(Long userId, Long notificationId);

    int getUnreadCount(Long userId);

    void generateExpiryNotifications();

    void generateOverdueNotifications();

    /**
     * 为指定药品生成即时通知（新增/编辑药品时调用）
     */
    void generateNotificationForMedicine(Medicine medicine);
}
