package com.fifteenrubles.questionWithOptionsService.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Entity
@Table
@NoArgsConstructor
@Data
public class QuestionWithOptions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Text is mandatory")
    private String text;
    @NotBlank(message = "Right answer id is mandatory")
    private String rightAnswer;
    private Long pollId;
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> options;
}

