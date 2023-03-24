package manager.api.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.lang.Nullable;

@Data
@AllArgsConstructor
public class RequestIdDTO {
    @JsonProperty(value = "requestId")
    @Nullable
    private String requestId;
}
