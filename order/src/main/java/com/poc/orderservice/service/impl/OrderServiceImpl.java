package com.poc.orderservice.service.impl;

import com.poc.orderservice.entity.Order;
import com.poc.orderservice.enums.OrderStatus;
import com.poc.orderservice.exception.ResourceNotFoundException;
import com.poc.orderservice.feignclient.FeignService;
import com.poc.orderservice.repository.OrderRepository;
import com.poc.orderservice.request.InventoryRequestDto;
import com.poc.orderservice.request.OrderRequestDto;
import com.poc.orderservice.response.ApiResponse;
import com.poc.orderservice.response.OrderResponseDto;
import com.poc.orderservice.service.OrderService;
import com.poc.orderservice.utils.GenericMapper;
import com.poc.orderservice.validation.OrderValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private List<String> errorList = new ArrayList<>();

    private final GenericMapper genericMapper;

    private final FeignService feignService;

    @Override
    @Transactional
    public ApiResponse createOrder(OrderRequestDto orderRequestDto) {

        if (validateOrder(orderRequestDto)) {
            Order order = genericMapper.convert(orderRequestDto, Order.class);

            order.setStatus(OrderStatus.VALIDATION_FAILED.name());
            order = orderRepository.save(order);

            OrderResponseDto orderResponseDto = genericMapper.convert(order, OrderResponseDto.class);

            return ApiResponse.response("Order Reject due to validation", false, OrderStatus.VALIDATION_FAILED.name(), orderResponseDto);
        }

        Order order = genericMapper.convert(orderRequestDto, Order.class);
        order.setStatus(OrderStatus.VALIDATED.name());
        order = orderRepository.save(order);

        InventoryRequestDto inventoryRequestDto = createInventoryRequestDto(order);

        ApiResponse apiResponse = feignService.createInventory(inventoryRequestDto);

        if (apiResponse.isSuccess()) {
            order.setStatus(OrderStatus.SUCCESS.name());
            order = orderRepository.save(order);
            OrderResponseDto orderResponseDto = genericMapper.convert(order, OrderResponseDto.class);

            return ApiResponse.response("Order Create Successfully", true, OrderStatus.SUCCESS.name(), orderResponseDto);
        }

        order.setStatus(OrderStatus.INSUFFICIENT_INVENTORY.name());
        order = orderRepository.save(order);

        OrderResponseDto orderResponseDto = genericMapper.convert(order, OrderResponseDto.class);

        return ApiResponse.response("Order Not Created", false, OrderStatus.INSUFFICIENT_INVENTORY.name(), orderResponseDto);

    }

    @Override
    @Transactional
    public ApiResponse getAllOrders(Pageable pageable) {
        List<OrderResponseDto> orderList = orderRepository.findAll(pageable)
                .stream()
                .map(order -> genericMapper.convert(order, OrderResponseDto.class))
                .toList();

        return ApiResponse.response("Order Details Found", true, null, orderList);
    }

    @Override
    @Transactional
    public ApiResponse getOrderById(Long orderId) {

        if (ObjectUtils.isEmpty(orderId)) {
            return ApiResponse.response("Order Details Not Found", false, "Please Provide Valid OrderId", null);
        }

        Order order = orderRepository.findById(orderId).
                orElseThrow(() -> new ResourceNotFoundException(String.format("Order Details Not found :%s ", orderId)));

        OrderResponseDto orderResponseDto = genericMapper.convert(order, OrderResponseDto.class);
        return ApiResponse.response("Order Details Found", true, "Order Details", orderResponseDto);
    }

    public boolean validateOrder(OrderRequestDto orderRequestDto) {

        errorList = OrderValidation.validateOrder(orderRequestDto);

        return errorList.isEmpty() ? false : true;
    }

    private InventoryRequestDto createInventoryRequestDto(Order order) {

        return InventoryRequestDto
                .builder()
                .quantity(order.getQuantity())
                .totalAmount(order.getTotalAmount())
                .stockId(Long.valueOf(order.getStockId()))
                .status(order.getStatus())
                .userId(order.getUserId())
                .build();

    }
}
