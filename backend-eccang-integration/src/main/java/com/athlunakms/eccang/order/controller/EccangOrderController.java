package com.athlunakms.eccang.order.controller;

import com.athlunakms.common.dto.ApiResponse;
import com.athlunakms.eccang.order.dto.OrderCreateRequest;
import com.athlunakms.eccang.order.dto.OrderCreateResponse;
import com.athlunakms.eccang.order.dto.EccangOrderDto;
import com.athlunakms.eccang.order.dto.SyncProgressDto;
import com.athlunakms.eccang.order.service.EccangOrderService;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

/**
 * Eccang 订单控制器
 * 处理订单查询、同步、创建及草稿管理
 */
@RestController
@RequestMapping(value={"/orders"})
public class EccangOrderController {
    private static final Logger log = LoggerFactory.getLogger(EccangOrderController.class);
    private final EccangOrderService orderService;

    /**
     * 获取所有订单或按店铺过滤
     */
    @GetMapping
    public ApiResponse<List<EccangOrderDto>> getAllOrders(@RequestParam(value="storeId", required=false) Long storeId) {
        List<EccangOrderDto> orders = storeId != null ? this.orderService.getOrdersByStoreId(storeId) : this.orderService.getAllOrders();
        return ApiResponse.success(orders);
    }

    /**
     * 创建订单或保存为草稿
     */
    @PostMapping
    public ApiResponse<OrderCreateResponse> createOrder(@RequestBody OrderCreateRequest request) {
        log.info("请求创建订单: storeId={}, isDraft={}", (Object)request.getStoreId(), (Object)request.getIsDraft());
        OrderCreateResponse response = this.orderService.createOrder(request);
        return response.isSuccess() ? ApiResponse.success(response) : ApiResponse.error(400, "创建失败", response);
    }

    /**
     * 更新订单草稿
     */
    @PutMapping(value={"/{id}"})
    public ApiResponse<OrderCreateResponse> updateOrder(@PathVariable(value="id") Long id, @RequestBody OrderCreateRequest request) {
        log.info("请求更新草稿订单: id={}, storeId={}", (Object)id, (Object)request.getStoreId());
        OrderCreateResponse response = this.orderService.updateDraftOrder(id, request);
        return response.isSuccess() ? ApiResponse.success(response) : ApiResponse.error(400, "更新失败", response);
    }

    /**
     * 确认草稿并转为正式订单
     */
    @PostMapping(value={"/{id}/confirm"})
    public ApiResponse<OrderCreateResponse> confirmDraftOrder(@PathVariable(value="id") Long id) {
        log.info("请求确认草稿订单: id={}", (Object)id);
        OrderCreateResponse response = this.orderService.confirmDraftOrder(id);
        return response.isSuccess() ? ApiResponse.success(response) : ApiResponse.error(400, "确认失败", response);
    }

    /**
     * 触发全量同步
     */
    @PostMapping(value={"/sync"})
    public ApiResponse<Map<String, Object>> syncOrders(@RequestParam(value="storeId") Long storeId, @RequestParam(value="startTime", required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime, @RequestParam(value="endTime", required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        this.orderService.syncOrders(storeId, startTime, endTime);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "订单同步任务已启动");
        return ApiResponse.success(response);
    }

    /**
     * 获取同步进度
     */
    @GetMapping(value={"/sync/progress"})
    public ApiResponse<SyncProgressDto> getSyncProgress(@RequestParam(value="storeId") Long storeId) {
        SyncProgressDto progress = this.orderService.getSyncProgress(storeId);
        if (progress == null) {
            return ApiResponse.success(SyncProgressDto.builder().status("IDLE").build());
        }
        return ApiResponse.success(progress);
    }

    /**
     * 清理同步进度缓存
     */
    @PostMapping(value={"/sync/clear"})
    public ApiResponse<Map<String, Object>> clearSyncProgress(@RequestParam(value="storeId") Long storeId) {
        this.orderService.clearSyncProgress(storeId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        return ApiResponse.success(response);
    }

    /**
     * 取消同步
     */
    @PostMapping("/{storeId}/sync/cancel")
    public ApiResponse<Boolean> cancelSync(@PathVariable Long storeId) {
        boolean result = this.orderService.cancelSync(storeId);
        return ApiResponse.success(result);
    }

    /**
     * 获取同步状态
     */
    @GetMapping(value={"/sync/status"})
    public ApiResponse<Map<String, Object>> getSyncStatus(@RequestParam(value="storeId") Long storeId) {
        Map<String, Object> response = new HashMap<>();
        boolean running = this.orderService.isSyncRunning(storeId);
        response.put("running", running);
        return ApiResponse.success(response);
    }

    /**
     * 删除草稿订单
     */
    @DeleteMapping(value={"/{id}"})
    public ApiResponse<Map<String, Object>> deleteOrder(@PathVariable(value="id") Long id) {
        log.info("请求删除草稿订单: id={}", (Object)id);
        this.orderService.deleteDraftOrder(id);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        return ApiResponse.success(response);
    }

    public EccangOrderController(EccangOrderService orderService) {
        this.orderService = orderService;
    }
}


