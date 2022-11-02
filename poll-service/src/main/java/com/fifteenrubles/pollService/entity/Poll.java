package com.fifteenrubles.pollService.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Table
@NoArgsConstructor
@Data
public class Poll {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Name must be not null")
    private String name;
    private Long ownerUserId;
    private Boolean isDeleted = false;

}
