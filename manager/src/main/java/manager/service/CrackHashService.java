package manager.service;

import lombok.extern.slf4j.Slf4j;
import manager.api.DTO.OkResponse;
import manager.api.DTO.RequestStatusDTO;
import manager.model.entity.RequestStatus;
import manager.model.mapper.RequestStatusMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.nsu.ccfit.schema.crack_hash_request.CrackHashManagerRequest;
import ru.nsu.ccfit.schema.crack_hash_response.CrackHashWorkerResponse;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


@Service
@Slf4j
public class CrackHashService {

    private final Map<String, RequestStatus> requests = new ConcurrentHashMap<>();
    private final RequestStatusMapper requestStatusMapper = RequestStatusMapper.INSTANCE;

    private final ArrayDeque<Pair<String, Timestamp>> requestCreated = new ArrayDeque<>();
    private final CrackHashManagerRequest.Alphabet alphabet = new CrackHashManagerRequest.Alphabet();
    @Value("${crackHashService.worker.ip}")
    String workerIp;
    @Value("${crackHashService.worker.port}")
    Integer workerPort;
    @Value("${crackHashService.manager.expireTimeMinutes}")
    Integer expireTimeMinutes;
    @Autowired
    private RestTemplate restTemplate;

    @PostConstruct
    private void init() {
        String alphabetString = "abcdefghijklmnopqrstuvwxyz0123456789";
        List.of(alphabetString.split("")).forEach(alphabet.getSymbols()::add);
    }

    public String crackHash(String hash, int maxLength) {
        String id = UUID.randomUUID().toString().replace("-", "");
        requests.put(id, new RequestStatus());
        CrackHashManagerRequest crackHashManagerRequest = new CrackHashManagerRequest();
        crackHashManagerRequest.setHash(hash);
        crackHashManagerRequest.setMaxLength(maxLength);
        crackHashManagerRequest.setRequestId(id);
        crackHashManagerRequest.setPartNumber(1);
        crackHashManagerRequest.setPartCount(1);
        crackHashManagerRequest.setAlphabet(alphabet);
        try {
            log.info("Sending request to worker: {}", crackHashManagerRequest);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_XML);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));

            restTemplate.exchange(
                    String.format("http://%s:%s/internal/api/worker/hash/crack/task", workerIp, workerPort),
                    HttpMethod.POST,
                    new HttpEntity<>(crackHashManagerRequest, headers),
                    OkResponse.class);
        } catch (Exception e) {
            log.error("Error while sending request to worker", e);
            return null;
        }
        requestCreated.add(Pair.of(id, new Timestamp(System.currentTimeMillis())));
        return id;
    }

    public RequestStatusDTO getStatus(String requestId) {
        return requestStatusMapper.toRequestStatusDTO(requests.get(requestId));
    }

    public void handleWorkerCallback(CrackHashWorkerResponse crackHashWorkerResponse) {
        log.info("Received response from worker: {}", crackHashWorkerResponse);
        RequestStatus requestStatus = requests.get(crackHashWorkerResponse.getRequestId());
        if (requestStatus.getStatus() == RequestStatus.Status.IN_PROGRESS) {
            if (crackHashWorkerResponse.getAnswers() != null) {
                requestStatus.getResult().addAll(crackHashWorkerResponse.getAnswers().getWords());
                requestStatus.setStatus(RequestStatus.Status.READY);
            }
        }
    }

    @Scheduled(fixedDelay = 10000)
    private void expireRequests() {
        requestCreated.removeIf(pair -> {
            if (System.currentTimeMillis() - pair.getSecond().getTime() > expireTimeMinutes * 60 * 1000) {
                requests.computeIfPresent(pair.getFirst(), (s, requestStatus) -> {
                    requestStatus.setStatus(RequestStatus.Status.ERROR);
                    return requestStatus;
                });
                return true;
            }
            return false;
        });
    }
}
