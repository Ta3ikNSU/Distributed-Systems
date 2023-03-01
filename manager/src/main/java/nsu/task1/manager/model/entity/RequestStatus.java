package nsu.task1.manager.model.entity;

import lombok.Data;
import nsu.task1.manager.service.CrackHashService;

import java.util.ArrayList;
import java.util.List;

import static nsu.task1.manager.model.entity.RequestStatus.Status.IN_PROGRESS;

@Data
public class RequestStatus {
    private Status status = IN_PROGRESS;
    private List<String> result = new ArrayList<>();

    public enum Status {
        IN_PROGRESS,
        READY,
        ERROR
    }
}