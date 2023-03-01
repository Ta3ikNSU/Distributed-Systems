package nsu.task1.manager.service;

import nsu.task1.manager.api.DTO.RequestStatusDTO;
import nsu.task1.manager.api.DTO.WorkerResponseDTO;
import nsu.task1.manager.model.entity.RequestStatus;
import nsu.task1.manager.model.mapper.RequestStatusMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


@Service
public class CrackHashService {

    private final Map<String, RequestStatus> requests = new ConcurrentHashMap<>();
    private final RequestStatusMapper requestStatusMapper = RequestStatusMapper.INSTANCE;
    @Value("${crackhash.manager.workerscounter}")
    Integer workersCounter;
    @Autowired
    private RestTemplate restTemplate;

    public String crackHash(String hash, int maxLength) {
        String id = UUID.randomUUID().toString();
        requests.put(id, new RequestStatus());
        return id;
    }

    public RequestStatusDTO getStatus(String requestId) {
        return requestStatusMapper.toRequestStatusDTO(requests.get(requestId));
    }

    public void handleWorkerCallback(WorkerResponseDTO workerResponseDTO) {
        RequestStatus requestStatus = requests.get(workerResponseDTO.getRequestId());
        if (requestStatus.getStatus() == RequestStatus.Status.IN_PROGRESS) {
            if (workerResponseDTO.getResults() != null) {
                requestStatus.getResult().addAll(workerResponseDTO.getResults());
                requestStatus.setStatus(RequestStatus.Status.READY);
            }
        }
    }
}
