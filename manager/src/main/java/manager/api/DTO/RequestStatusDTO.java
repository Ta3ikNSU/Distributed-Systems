package manager.api.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;


@Data
@AllArgsConstructor
public class RequestStatusDTO {
    @JsonProperty(value = "status")
    private String status;

    @JsonProperty(value = "result")
    private List<String> result;
}
