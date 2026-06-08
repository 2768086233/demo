package com.medicine.demo1.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medicine.demo1.dto.MedicineAddDTO;
import com.medicine.demo1.dto.MedicineQueryDTO;
import com.medicine.demo1.dto.MedicineUpdateDTO;
import com.medicine.demo1.dto.QuantityUpdateDTO;
import com.medicine.demo1.entity.Medicine;

public interface MedicineService {

    Medicine addMedicine(Long userId, MedicineAddDTO dto);

    Medicine updateMedicine(Long userId, Long medicineId, MedicineUpdateDTO dto);

    void deleteMedicine(Long userId, Long medicineId);

    Medicine getMedicineById(Long userId, Long medicineId);

    IPage<Medicine> listMedicines(Long userId, MedicineQueryDTO queryDTO);

    void updateQuantity(Long userId, Long medicineId, QuantityUpdateDTO dto);

    void updateStatusByExpiry();
}
