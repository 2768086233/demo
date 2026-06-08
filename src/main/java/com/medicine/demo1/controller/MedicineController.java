package com.medicine.demo1.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medicine.demo1.common.Result;
import com.medicine.demo1.dto.*;
import com.medicine.demo1.entity.Medicine;
import com.medicine.demo1.entity.MedicineUsageLog;
import com.medicine.demo1.service.MedicineService;
import com.medicine.demo1.service.MedicineUsageLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "药品管理", description = "药品的增删改查、数量变更、使用记录")
@RestController
@RequestMapping("/api/medicine")
@RequiredArgsConstructor
public class MedicineController {

    private final MedicineService medicineService;
    private final MedicineUsageLogService usageLogService;

    @Operation(summary = "新增药品")
    @PostMapping
    public Result<Medicine> addMedicine(@RequestParam Long userId,
                                        @Valid @RequestBody MedicineAddDTO addDTO) {
        Medicine medicine = medicineService.addMedicine(userId, addDTO);
        return Result.<Medicine>success("新增成功", medicine);
    }

    @Operation(summary = "删除药品")
    @DeleteMapping("/{id}")
    public Result<Void> deleteMedicine(@RequestParam Long userId,
                                       @PathVariable Long id) {
        medicineService.deleteMedicine(userId, id);
        return Result.success();
    }

    @Operation(summary = "更新药品")
    @PutMapping("/{id}")
    public Result<Medicine> updateMedicine(@RequestParam Long userId,
                                           @PathVariable Long id,
                                           @Valid @RequestBody MedicineUpdateDTO updateDTO) {
        Medicine medicine = medicineService.updateMedicine(userId, id, updateDTO);
        return Result.<Medicine>success("更新成功", medicine);
    }

    @Operation(summary = "获取药品详情")
    @GetMapping("/{id}")
    public Result<Medicine> getMedicine(@RequestParam Long userId,
                                        @PathVariable Long id) {
        Medicine medicine = medicineService.getMedicineById(userId, id);
        return Result.success(medicine);
    }

    @Operation(summary = "药品列表（分页+筛选）")
    @GetMapping("/list")
    public Result<PageResult<Medicine>> listMedicines(@RequestParam Long userId,
                                                      @Valid MedicineQueryDTO queryDTO) {
        IPage<Medicine> page = medicineService.listMedicines(userId, queryDTO);
        return Result.success(PageResult.of(page));
    }

    @Operation(summary = "更新药品数量")
    @PutMapping("/{id}/quantity")
    public Result<Void> updateQuantity(@RequestParam Long userId,
                                       @PathVariable Long id,
                                       @Valid @RequestBody QuantityUpdateDTO quantityDTO) {
        medicineService.updateQuantity(userId, id, quantityDTO);
        return Result.success();
    }

    @Operation(summary = "获取药品使用记录")
    @GetMapping("/{id}/logs")
    public Result<PageResult<MedicineUsageLog>> getLogs(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        IPage<MedicineUsageLog> logPage = usageLogService.getLogsByMedicineId(id, page, size);
        return Result.success(PageResult.of(logPage));
    }

    @Operation(summary = "扫码查询药品信息（模拟）")
    @PostMapping("/scan")
    public Result<MedicineAddDTO> scanMedicine(@Parameter(description = "条形码/二维码内容")
                                                 @RequestParam String barcode) {
        // 开发阶段返回模拟数据，生产环境应对接药品数据库API
        MedicineAddDTO mockData = new MedicineAddDTO();
        mockData.setName("扫码药品-" + barcode);
        mockData.setBatchNumber("BN" + System.currentTimeMillis());
        mockData.setQuantity(1);
        return Result.success(mockData);
    }
}
