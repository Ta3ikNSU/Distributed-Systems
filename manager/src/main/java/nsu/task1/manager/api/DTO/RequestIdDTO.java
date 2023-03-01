package nsu.task1.manager.api.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RequestIdDTO {
    @JsonProperty(value = "requestId")
    private String requestId;
}
