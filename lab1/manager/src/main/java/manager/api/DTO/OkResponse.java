package manager.api.DTO;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@JsonDeserialize(builder = OkResponse.OkResponseBuilder.class)
@AllArgsConstructor(onConstructor = @__(@JsonCreator))
@Builder(toBuilder = true)
public class OkResponse {
}
