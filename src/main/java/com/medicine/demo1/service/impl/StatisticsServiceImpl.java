package com.medicine.demo1.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.medicine.demo1.entity.Medicine;
import com.medicine.demo1.mapper.MedicineMapper;
import com.medicine.demo1.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final MedicineMapper medicineMapper;

    @Override
    public Map<String, Object> getDashboard(Long userId) {
        List<Medicine> allMedicines = medicineMapper.selectList(
                new LambdaQueryWrapper<Medicine>()
                        .eq(Medicine::getUserId, userId)
                        .eq(Medicine::getIsDeleted, 0));

        int total = allMedicines.size();
        int normal = 0;
        int expiringSoon = 0;
        int expired = 0;
        int usedUp = 0;

        for (Medicine med : allMedicines) {
            switch (med.getStatus()) {
                case 0: normal++; break;
                case 1: expiringSoon++; break;
                case 2: expired++; break;
                case 3: usedUp++; break;
            }
        }

        // 近期待办（7天内到期且未过期）
        LocalDate today = LocalDate.now();
        LocalDate sevenDaysLater = today.plusDays(7);
        List<Map<String, Object>> upcomingExpiries = new ArrayList<>();

        for (Medicine med : allMedicines) {
            if (med.getExpiryDate() != null
                    && med.getQuantity() > 0
                    && !med.getExpiryDate().isBefore(today)
                    && !med.getExpiryDate().isAfter(sevenDaysLater)) {

                Map<String, Object> item = new HashMap<>();
                item.put("id", med.getId());
                item.put("name", med.getName());
                item.put("expiryDate", med.getExpiryDate());
                item.put("daysLeft", ChronoUnit.DAYS.between(today, med.getExpiryDate()));
                item.put("quantity", med.getQuantity());
                upcomingExpiries.add(item);
            }
        }
        // 按有效期升序
        upcomingExpiries.sort(Comparator.comparing(m -> (LocalDate) m.get("expiryDate")));

        Map<String, Object> dashboard = new LinkedHashMap<>();
        dashboard.put("total", total);
        dashboard.put("normal", normal);
        dashboard.put("expiringSoon", expiringSoon);
        dashboard.put("expired", expired);
        dashboard.put("usedUp", usedUp);
        dashboard.put("upcomingExpiries", upcomingExpiries);
        return dashboard;
    }

    @Override
    public Map<String, Object> getExpiryDistribution(Long userId) {
        List<Medicine> allMedicines = medicineMapper.selectList(
                new LambdaQueryWrapper<Medicine>()
                        .eq(Medicine::getUserId, userId)
                        .eq(Medicine::getIsDeleted, 0)
                        .gt(Medicine::getQuantity, 0));

        LocalDate today = LocalDate.now();
        int within7Days = 0;
        int within15Days = 0;
        int within30Days = 0;
        int within90Days = 0;
        int beyond90Days = 0;
        int alreadyExpired = 0;

        for (Medicine med : allMedicines) {
            if (med.getExpiryDate() == null) continue;
            if (med.getExpiryDate().isBefore(today)) {
                alreadyExpired++;
                continue;
            }
            long days = ChronoUnit.DAYS.between(today, med.getExpiryDate());
            if (days <= 7) within7Days++;
            else if (days <= 15) within15Days++;
            else if (days <= 30) within30Days++;
            else if (days <= 90) within90Days++;
            else beyond90Days++;
        }

        Map<String, Object> distribution = new LinkedHashMap<>();
        distribution.put("within7Days", within7Days);
        distribution.put("within15Days", within15Days);
        distribution.put("within30Days", within30Days);
        distribution.put("within90Days", within90Days);
        distribution.put("beyond90Days", beyond90Days);
        distribution.put("alreadyExpired", alreadyExpired);
        return distribution;
    }
}
