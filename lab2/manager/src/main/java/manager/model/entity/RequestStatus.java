package manager.model.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Document("RequestStatus")
public class RequestStatus {
    @Id
    private String requestId;

    private Status status;

    private ArrayList<String> result;

    private Date updated;

    public RequestStatus (String id) {
        this.requestId = id;
        this.status = Status.IN_PROGRESS;
        this.updated = new Date(System.currentTimeMillis());
        result = new ArrayList<>();
    }

    public enum Status {
        IN_PROGRESS,
        READY,
        ERROR
    }
}