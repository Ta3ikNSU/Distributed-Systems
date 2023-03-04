package manager.api.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CrackRequestDTO {

    @JsonProperty(value = "hash", required = true)
    private String hash;

    @JsonProperty(value = "maxLength", required = true)
    private int maxLength;
}
