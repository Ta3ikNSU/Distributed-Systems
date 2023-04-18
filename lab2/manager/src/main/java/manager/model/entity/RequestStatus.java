package manager.model.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.UUID;

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

    private HashSet<Integer> notAnsweredWorkers;

    private Date updated;

    public RequestStatus (int workersCount) {
        this.requestId = UUID.randomUUID().toString();
        this.status = Status.IN_PROGRESS;
        this.updated = new Date(System.currentTimeMillis());
        result = new ArrayList<>();
        notAnsweredWorkers = new HashSet<>();
        for (int i = 0; i < workersCount; i++) {
            notAnsweredWorkers.add(i);
        }
    }

    public enum Status {
        IN_PROGRESS,
        READY,
        ERROR
    }
}