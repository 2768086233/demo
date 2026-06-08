package com.medicine.demo1.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medicine.demo1.common.BusinessException;
import com.medicine.demo1.common.ResultCode;
import com.medicine.demo1.dto.MedicineAddDTO;
import com.medicine.demo1.dto.MedicineQueryDTO;
import com.medicine.demo1.dto.MedicineUpdateDTO;
import com.medicine.demo1.dto.QuantityUpdateDTO;
import com.medicine.demo1.entity.Medicine;
import com.medicine.demo1.entity.MedicineUsageLog;
import com.medicine.demo1.mapper.MedicineMapper;
import com.medicine.demo1.mapper.MedicineUsageLogMapper;
import com.medicine.demo1.service.MedicineService;
import com.medicine.demo1.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class MedicineServiceImpl extends ServiceImpl<MedicineMapper, Medicine> implements MedicineService {

    private final MedicineMapper medicineMapper;
    private final MedicineUsageLogMapper usageLogMapper;
    private final NotificationService notificationService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Medicine addMedicine(Long userId, MedicineAddDTO dto) {
        Medicine medicine = new Medicine();
        medicine.setUserId(userId);
        medicine.setName(dto.getName());
        medicine.setGenericName(dto.getGenericName());
        medicine.setBatchNumber(dto.getBatchNumber());
        medicine.setProduceDate(dto.getProduceDate());
        medicine.setExpiryDate(dto.getExpiryDate());
        medicine.setQuantity(dto.getQuantity());
        medicine.setUnit(dto.getUnit());
        medicine.setLocation(dto.getLocation());
        medicine.setManufacturer(dto.getManufacturer());
        medicine.setCategory(dto.getCategory());
        medicine.setRemark(dto.getRemark());
        medicine.setImages(dto.getImages());

        // 计算初始状态
        medicine.setStatus(calculateStatus(dto.getExpiryDate()));

        try {
            medicineMapper.insert(medicine);
        } catch (Exception e) {
            log.error("插入药品失败: {}", e.getMessage(), e);
            throw e;
        }

        // 记录使用日志（新增）
        MedicineUsageLog logEntry = new MedicineUsageLog();
        logEntry.setMedicineId(medicine.getId());
        logEntry.setUserId(userId);
        logEntry.setChangeType(1); // 增加
        logEntry.setQuantityChange(dto.getQuantity());
        logEntry.setBeforeQuantity(0);
        logEntry.setAfterQuantity(dto.getQuantity());
        logEntry.setRemark("新增药品");
        usageLogMapper.insert(logEntry);

        // 新增药品时，如果临期或过期，立即生成通知
        notificationService.generateNotificationForMedicine(medicine);

        return medicine;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Medicine updateMedicine(Long userId, Long medicineId, MedicineUpdateDTO dto) {
        Medicine medicine = getMedicineById(userId, medicineId);

        if (StrUtil.isNotBlank(dto.getName())) medicine.setName(dto.getName());
        if (StrUtil.isNotBlank(dto.getGenericName())) medicine.setGenericName(dto.getGenericName());
        if (StrUtil.isNotBlank(dto.getBatchNumber())) medicine.setBatchNumber(dto.getBatchNumber());
        if (dto.getProduceDate() != null) medicine.setProduceDate(dto.getProduceDate());
        if (dto.getExpiryDate() != null) {
            medicine.setExpiryDate(dto.getExpiryDate());
            medicine.setStatus(calculateStatus(dto.getExpiryDate()));
        }
        if (dto.getQuantity() != null) medicine.setQuantity(dto.getQuantity());
        if (StrUtil.isNotBlank(dto.getUnit())) medicine.setUnit(dto.getUnit());
        if (StrUtil.isNotBlank(dto.getLocation())) medicine.setLocation(dto.getLocation());
        if (StrUtil.isNotBlank(dto.getManufacturer())) medicine.setManufacturer(dto.getManufacturer());
        if (StrUtil.isNotBlank(dto.getCategory())) medicine.setCategory(dto.getCategory());
        if (StrUtil.isNotBlank(dto.getRemark())) medicine.setRemark(dto.getRemark());
        if (StrUtil.isNotBlank(dto.getImages())) medicine.setImages(dto.getImages());

        medicineMapper.updateById(medicine);
        return medicine;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMedicine(Long userId, Long medicineId) {
        Medicine medicine = getMedicineById(userId, medicineId);
        medicineMapper.deleteById(medicine.getId());
        log.info("药品已删除：id={}, name={}", medicine.getId(), medicine.getName());
    }

    @Override
    public Medicine getMedicineById(Long userId, Long medicineId) {
        Medicine medicine = medicineMapper.selectById(medicineId);
        if (medicine == null || medicine.getIsDeleted() == 1) {
            throw new BusinessException(ResultCode.MEDICINE_NOT_EXIST);
        }
        // 数据隔离校验
        if (!medicine.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN, "无权访问该药品");
        }
        return medicine;
    }

    @Override
    public IPage<Medicine> listMedicines(Long userId, MedicineQueryDTO queryDTO) {
        Page<Medicine> page = new Page<>(queryDTO.getPage(), queryDTO.getSize());
        LambdaQueryWrapper<Medicine> wrapper = new LambdaQueryWrapper<>();

        // 数据隔离
        wrapper.eq(Medicine::getUserId, userId);

        // 关键字搜索（名称或通用名）
        if (StrUtil.isNotBlank(queryDTO.getKeyword())) {
            wrapper.and(w -> w
                    .like(Medicine::getName, queryDTO.getKeyword())
                    .or()
                    .like(Medicine::getGenericName, queryDTO.getKeyword()));
        }

        // 状态筛选
        if (queryDTO.getStatus() != null) {
            wrapper.eq(Medicine::getStatus, queryDTO.getStatus());
        }

        // 分类筛选
        if (StrUtil.isNotBlank(queryDTO.getCategory())) {
            wrapper.eq(Medicine::getCategory, queryDTO.getCategory());
        }

        // 位置筛选
        if (StrUtil.isNotBlank(queryDTO.getLocation())) {
            wrapper.eq(Medicine::getLocation, queryDTO.getLocation());
        }

        // 排序
        boolean asc = "asc".equalsIgnoreCase(queryDTO.getSortOrder());
        if ("expiry_date".equals(queryDTO.getSortField())) {
            wrapper.orderBy(true, asc, Medicine::getExpiryDate);
        } else {
            wrapper.orderBy(true, asc, Medicine::getCreateTime);
        }

        return medicineMapper.selectPage(page, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateQuantity(Long userId, Long medicineId, QuantityUpdateDTO dto) {
        Medicine medicine = getMedicineById(userId, medicineId);
        int beforeQuantity = medicine.getQuantity();

        if (dto.getChangeType() == 1) {
            // 增加
            medicine.setQuantity(beforeQuantity + dto.getQuantityChange());
            // 如果之前是用完状态，恢复
            if (medicine.getStatus() == 3) {
                medicine.setStatus(calculateStatus(medicine.getExpiryDate()));
            }
        } else if (dto.getChangeType() == 2) {
            // 减少
            if (beforeQuantity < dto.getQuantityChange()) {
                throw new BusinessException(ResultCode.INSUFFICIENT_QUANTITY,
                        "当前数量为" + beforeQuantity + "，不足" + dto.getQuantityChange());
            }
            int afterQuantity = beforeQuantity - dto.getQuantityChange();
            medicine.setQuantity(afterQuantity);
            if (afterQuantity <= 0) {
                medicine.setStatus(3); // 已用完
            }
        } else {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "无效的变更类型");
        }

        medicineMapper.updateById(medicine);

        // 记录使用日志
        MedicineUsageLog logEntry = new MedicineUsageLog();
        logEntry.setMedicineId(medicineId);
        logEntry.setUserId(userId);
        logEntry.setChangeType(dto.getChangeType());
        logEntry.setQuantityChange(dto.getChangeType() == 1 ? dto.getQuantityChange() : -dto.getQuantityChange());
        logEntry.setBeforeQuantity(beforeQuantity);
        logEntry.setAfterQuantity(medicine.getQuantity());
        logEntry.setRemark(dto.getRemark());
        usageLogMapper.insert(logEntry);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatusByExpiry() {
        LocalDate today = LocalDate.now();

        // 查询所有未删除的药品
        LambdaQueryWrapper<Medicine> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Medicine::getIsDeleted, 0);

        int updatedCount = 0;
        for (Medicine medicine : medicineMapper.selectList(wrapper)) {
            if (medicine.getQuantity() <= 0) {
                if (medicine.getStatus() != 3) {
                    medicine.setStatus(3);
                    medicineMapper.updateById(medicine);
                    updatedCount++;
                }
                continue;
            }
            int newStatus = calculateStatus(medicine.getExpiryDate());
            if (medicine.getStatus() != newStatus) {
                medicine.setStatus(newStatus);
                medicineMapper.updateById(medicine);
                updatedCount++;
            }
        }
        log.info("定时任务：效期状态更新完成，共更新 {} 条记录", updatedCount);
    }

    /**
     * 根据有效期计算药品状态
     */
    private int calculateStatus(LocalDate expiryDate) {
        if (expiryDate == null) return 0;
        LocalDate today = LocalDate.now();
        if (expiryDate.isBefore(today)) {
            return 2; // 过期
        }
        long daysUntilExpiry = ChronoUnit.DAYS.between(today, expiryDate);
        if (daysUntilExpiry <= 30) {
            return 1; // 临期
        }
        return 0; // 正常
    }
}
