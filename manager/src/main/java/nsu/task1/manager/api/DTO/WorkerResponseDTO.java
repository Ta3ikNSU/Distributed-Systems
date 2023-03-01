package nsu.task1.manager.api.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class WorkerResponseDTO {
    @JsonProperty(value = "requestId", required = true)
    private String requestId;

    @JsonProperty(value = "results")
    private List<String> results;
}
