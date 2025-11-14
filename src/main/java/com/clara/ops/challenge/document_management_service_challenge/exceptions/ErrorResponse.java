package com.clara.ops.challenge.document_management_service_challenge.exceptions;

import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
  private int status;
  private String error;
  private String message;
  private LocalDateTime timestamp;
}
