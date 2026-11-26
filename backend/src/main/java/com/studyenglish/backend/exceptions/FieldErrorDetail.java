package com.studyenglish.backend.exceptions;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FieldErrorDetail {
    private String field;
    private String message;
}
