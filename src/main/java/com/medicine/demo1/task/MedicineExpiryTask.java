package com.medicine.demo1.task;

import com.medicine.demo1.service.MedicineService;
import com.medicine.demo1.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MedicineExpiryTask {

    private final MedicineService medicineService;
    private final NotificationService notificationService;

    /**
     * 每日 00:30 更新药品效期状态
     */
    @Scheduled(cron = "0 30 0 * * ?")
    public void updateExpiryStatus() {
        log.info("===== 定时任务：开始更新药品效期状态 =====");
        long start = System.currentTimeMillis();
        medicineService.updateStatusByExpiry();
        long cost = System.currentTimeMillis() - start;
        log.info("===== 定时任务：效期状态更新完成，耗时 {}ms =====", cost);
    }

    /**
     * 每日 09:00 推送临期提醒
     */
    @Scheduled(cron = "0 0 9 * * ?")
    public void sendExpiryNotifications() {
        log.info("===== 定时任务：开始推送临期提醒 =====");
        long start = System.currentTimeMillis();
        notificationService.generateExpiryNotifications();
        long cost = System.currentTimeMillis() - start;
        log.info("===== 定时任务：临期提醒推送完成，耗时 {}ms =====", cost);
    }

    /**
     * 每日 09:30 推送过期提醒
     */
    @Scheduled(cron = "0 30 9 * * ?")
    public void sendOverdueNotifications() {
        log.info("===== 定时任务：开始推送过期提醒 =====");
        long start = System.currentTimeMillis();
        notificationService.generateOverdueNotifications();
        long cost = System.currentTimeMillis() - start;
        log.info("===== 定时任务：过期提醒推送完成，耗时 {}ms =====", cost);
    }
}
