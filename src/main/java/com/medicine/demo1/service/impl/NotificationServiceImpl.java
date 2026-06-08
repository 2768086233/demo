package com.medicine.demo1.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medicine.demo1.entity.Medicine;
import com.medicine.demo1.entity.NotificationLog;
import com.medicine.demo1.mapper.MedicineMapper;
import com.medicine.demo1.mapper.NotificationLogMapper;
import com.medicine.demo1.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationLogMapper notificationLogMapper;
    private final MedicineMapper medicineMapper;

    @Override
    public IPage<NotificationLog> listNotifications(Long userId, Integer pageNum, Integer size) {
        Page<NotificationLog> page = new Page<>(pageNum, size);
        LambdaQueryWrapper<NotificationLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NotificationLog::getUserId, userId);
        wrapper.orderByDesc(NotificationLog::getSendTime);
        return notificationLogMapper.selectPage(page, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAsRead(Long userId, Long notificationId) {
        NotificationLog logEntry = notificationLogMapper.selectById(notificationId);
        if (logEntry != null && logEntry.getUserId().equals(userId)) {
            logEntry.setIsRead(1);
            notificationLogMapper.updateById(logEntry);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteNotification(Long userId, Long notificationId) {
        NotificationLog logEntry = notificationLogMapper.selectById(notificationId);
        if (logEntry != null && logEntry.getUserId().equals(userId)) {
            notificationLogMapper.deleteById(notificationId);
        }
    }

    @Override
    public int getUnreadCount(Long userId) {
        Long count = notificationLogMapper.selectCount(
                new LambdaQueryWrapper<NotificationLog>()
                        .eq(NotificationLog::getUserId, userId)
                        .eq(NotificationLog::getIsRead, 0));
        return count.intValue();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void generateExpiryNotifications() {
        LocalDate today = LocalDate.now();
        LocalDate todayPlus30 = today.plusDays(30);
        LocalDate todayPlus15 = today.plusDays(15);
        LocalDate todayPlus7 = today.plusDays(7);

        // 查询所有未删除且状态为"正常"或"临期"的药品
        List<Medicine> medicines = medicineMapper.selectList(
                new LambdaQueryWrapper<Medicine>()
                        .eq(Medicine::getIsDeleted, 0)
                        .in(Medicine::getStatus, 0, 1)
                        .gt(Medicine::getQuantity, 0));

        for (Medicine med : medicines) {
            if (med.getExpiryDate() == null) continue;

            long daysUntilExpiry = ChronoUnit.DAYS.between(today, med.getExpiryDate());

            // 判断是否刚好到达提醒节点（精确匹配该日期）
            boolean shouldRemind = false;
            int notifyType = 0;

            if (daysUntilExpiry == 30) {
                shouldRemind = true;
                notifyType = 1;
            } else if (daysUntilExpiry == 15) {
                shouldRemind = true;
                notifyType = 1;
            } else if (daysUntilExpiry == 7) {
                shouldRemind = true;
                notifyType = 1;
            }

            if (shouldRemind && !hasNotifiedToday(med.getId(), notifyType)) {
                insertNotification(med.getUserId(), med.getId(), notifyType,
                        "药品「" + med.getName() + "」将在 " + daysUntilExpiry + " 天后过期（" + med.getExpiryDate() + "）");
                log.info("临期提醒已生成：药品={}, 有效期={}, 剩余{}天", med.getName(), med.getExpiryDate(), daysUntilExpiry);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void generateOverdueNotifications() {
        LocalDate today = LocalDate.now();

        List<Medicine> medicines = medicineMapper.selectList(
                new LambdaQueryWrapper<Medicine>()
                        .eq(Medicine::getIsDeleted, 0)
                        .eq(Medicine::getStatus, 2)
                        .gt(Medicine::getQuantity, 0));

        for (Medicine med : medicines) {
            // 精确匹配过期的第一天: 有效期 = 昨天
            if (med.getExpiryDate() != null
                    && med.getExpiryDate().plusDays(1).equals(today)
                    && !hasNotifiedToday(med.getId(), 2)) {

                insertNotification(med.getUserId(), med.getId(), 2,
                        "药品「" + med.getName() + "」已于 " + med.getExpiryDate() + " 过期，请及时处理");
                log.info("过期提醒已生成：药品={}, 有效期至={}", med.getName(), med.getExpiryDate());
            }
        }
    }

    /**
     * 检查今天是否已生成过该药品的同类型通知
     */
    private boolean hasNotifiedToday(Long medicineId, int type) {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(23, 59, 59);

        Long count = notificationLogMapper.selectCount(
                new LambdaQueryWrapper<NotificationLog>()
                        .eq(NotificationLog::getMedicineId, medicineId)
                        .eq(NotificationLog::getType, type)
                        .between(NotificationLog::getSendTime, startOfDay, endOfDay));
        return count > 0;
    }

    private void insertNotification(Long userId, Long medicineId, int type, String content) {
        NotificationLog logEntry = new NotificationLog();
        logEntry.setUserId(userId);
        logEntry.setMedicineId(medicineId);
        logEntry.setType(type);
        logEntry.setContent(content);
        logEntry.setSendTime(LocalDateTime.now());
        logEntry.setIsRead(0);
        notificationLogMapper.insert(logEntry);
    }

    /**
     * 为指定药品生成即时通知（用于新增/编辑药品时）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void generateNotificationForMedicine(Medicine medicine) {
        if (medicine.getExpiryDate() == null || medicine.getQuantity() <= 0) return;

        LocalDate today = LocalDate.now();
        String name = medicine.getName();

        if (medicine.getExpiryDate().isBefore(today)) {
            // 已过期
            insertNotification(medicine.getUserId(), medicine.getId(), 2,
                    "药品「" + name + "」已于 " + medicine.getExpiryDate() + " 过期，请及时处理");
            log.info("即时通知：药品「{}」已过期", name);
        } else {
            long daysUntilExpiry = ChronoUnit.DAYS.between(today, medicine.getExpiryDate());
            if (daysUntilExpiry <= 7) {
                insertNotification(medicine.getUserId(), medicine.getId(), 1,
                        "药品「" + name + "」将在 " + daysUntilExpiry + " 天后过期（" + medicine.getExpiryDate() + "），请尽快使用");
                log.info("即时通知：药品「{}」将在{}天后过期", name, daysUntilExpiry);
            } else if (daysUntilExpiry <= 15) {
                insertNotification(medicine.getUserId(), medicine.getId(), 1,
                        "药品「" + name + "」将在 " + daysUntilExpiry + " 天后过期（" + medicine.getExpiryDate() + "）");
            } else if (daysUntilExpiry <= 30) {
                insertNotification(medicine.getUserId(), medicine.getId(), 1,
                        "药品「" + name + "」将在 " + daysUntilExpiry + " 天内过期（" + medicine.getExpiryDate() + "）");
            }
        }
    }
}
