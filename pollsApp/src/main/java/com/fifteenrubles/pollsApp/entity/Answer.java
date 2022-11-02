package com.fifteenrubles.pollsApp.entity;


import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Table
@NoArgsConstructor
@Data
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "text is mandatory")
    private String text;
    private Long pollId;
    private Long questionId;
    private Long userId;
    private Boolean withOptions;

}
