package com.fifteenrubles.questionService.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Table
@NoArgsConstructor
@Data
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Text is mandatory")
    private String text;
    @NotBlank(message = "Right answer is mandatory")
    private String rightAnswer;
    private Long pollId;
}
