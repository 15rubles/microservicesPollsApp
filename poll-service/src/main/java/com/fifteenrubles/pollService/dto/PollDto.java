package com.fifteenrubles.pollService.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class PollDto {
    private Long id;
    @NotBlank(message = "Name is mandatory")
    private String name;
    private Long ownerUserId;
}
