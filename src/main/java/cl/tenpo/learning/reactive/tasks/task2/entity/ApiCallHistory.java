package cl.tenpo.learning.reactive.tasks.task2.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "api_call_history")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiCallHistory {
  @Id private String id;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime timestamp;
  private String endpoint;
  private String method;
  private String queryParams;
  private String requestBody;
  private String responseBody;
  private int httpStatus;
  private String error;
}
