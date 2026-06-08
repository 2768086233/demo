package com.medicine.demo1.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medicine.demo1.entity.MedicineUsageLog;
import com.medicine.demo1.mapper.MedicineUsageLogMapper;
import com.medicine.demo1.service.MedicineUsageLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MedicineUsageLogServiceImpl implements MedicineUsageLogService {

    private final MedicineUsageLogMapper usageLogMapper;

    @Override
    public IPage<MedicineUsageLog> getLogsByMedicineId(Long medicineId, Integer pageNum, Integer size) {
        Page<MedicineUsageLog> page = new Page<>(pageNum, size);
        LambdaQueryWrapper<MedicineUsageLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicineUsageLog::getMedicineId, medicineId);
        wrapper.orderByDesc(MedicineUsageLog::getCreateTime);
        return usageLogMapper.selectPage(page, wrapper);
    }
}
