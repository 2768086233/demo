package com.medicine.demo1.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medicine.demo1.entity.MedicineUsageLog;

public interface MedicineUsageLogService {

    IPage<MedicineUsageLog> getLogsByMedicineId(Long medicineId, Integer page, Integer size);
}
