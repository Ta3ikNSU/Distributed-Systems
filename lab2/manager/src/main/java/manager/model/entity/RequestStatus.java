package manager.model.entity;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static manager.model.entity.RequestStatus.Status.IN_PROGRESS;

@AllArgsConstructor
@RequiredArgsConstructor
@Entity
public class RequestStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long requestId;

    @Column(name = "status")
    private Status status = IN_PROGRESS;

    @ElementCollection
    @CollectionTable(name = "result", joinColumns = @JoinColumn(name = "request_id"))
    private List<String> result = new ArrayList<>();

    public enum Status {
        IN_PROGRESS,
        READY,
        ERROR
    }
}