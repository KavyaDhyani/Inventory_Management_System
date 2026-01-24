package com.example.order_service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalesOrderCreateDTO {

    @NotBlank(message = "Customer name is required")
    @Size(max = 255, message = "Customer name must not exceed 255 characters")
    private String customerName;

    @NotEmpty(message = "At least one item is required")
    @Valid
    private List<SalesItemDTO> items;
}
