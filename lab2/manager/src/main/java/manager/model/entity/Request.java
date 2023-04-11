package manager.model.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import ru.nsu.ccfit.schema.crack_hash_request.CrackHashManagerRequest;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Document(collection = "requests")
public class Request {
    @Id
    private String id;
    private CrackHashManagerRequest request;

    public Request(CrackHashManagerRequest request) {
        this.id = request.getRequestId();
        this.request = request;
    }
}
