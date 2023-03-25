package manager.service;

import lombok.extern.slf4j.Slf4j;
import manager.api.DTO.OkResponse;
import manager.api.DTO.RequestStatusDTO;
import manager.model.entity.RequestStatus;
import manager.model.entity.RequestStatus.Status;
import manager.model.mapper.RequestStatusMapper;
import manager.model.repository.RequestStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.nsu.ccfit.schema.crack_hash_request.CrackHashManagerRequest;
import ru.nsu.ccfit.schema.crack_hash_response.CrackHashWorkerResponse;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;
import java.util.UUID;


@Service
@Slf4j
@EnableScheduling
public class CrackHashService {
    private final RequestStatusMapper requestStatusMapper = RequestStatusMapper.INSTANCE;
    private final CrackHashManagerRequest.Alphabet alphabet = new CrackHashManagerRequest.Alphabet();
    @Value("${crackHashService.worker.ip}")
    String workerIp;
    @Value("${crackHashService.worker.port}")
    Integer workerPort;
    @Value("${crackHashService.manager.expireTimeMinutes}")
    Integer expireTimeMinutes;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private RequestStatusRepository requestStatusRepository;

    @PostConstruct
    private void init() {
        String alphabetString = "abcdefghijklmnopqrstuvwxyz0123456789";
        List.of(alphabetString.split("")).forEach(alphabet.getSymbols()::add);
    }

    public String crackHash(String hash, int maxLength) {
        RequestStatus entity = requestStatusRepository.insert(new RequestStatus(UUID.randomUUID().toString()));
        CrackHashManagerRequest crackHashManagerRequest = new CrackHashManagerRequest();
        crackHashManagerRequest.setHash(hash);
        crackHashManagerRequest.setMaxLength(maxLength);
        crackHashManagerRequest.setRequestId(entity.getRequestId());
        crackHashManagerRequest.setPartNumber(1);
        crackHashManagerRequest.setPartCount(1);
        crackHashManagerRequest.setAlphabet(alphabet);
        try {
            sendTaskToWorker(crackHashManagerRequest);
        } catch (Exception e) {
            log.error("Error while sending request to worker", e);
            return null;
        }
        return entity.getRequestId();
    }

    public RequestStatusDTO getStatus(String requestId) {
        return requestStatusMapper.toRequestStatusDTO(requestStatusRepository.findByRequestId(requestId));
    }

    public void handleWorkerCallback(CrackHashWorkerResponse crackHashWorkerResponse) {
        log.info("Received response from worker: {}", crackHashWorkerResponse);
        RequestStatus requestStatus = requestStatusRepository.findByRequestId(crackHashWorkerResponse.getRequestId());
        if (requestStatus.getStatus() == RequestStatus.Status.IN_PROGRESS) {
            if (crackHashWorkerResponse.getAnswers() != null) {
                requestStatus.getResult().addAll(crackHashWorkerResponse.getAnswers().getWords());
                requestStatus.setStatus(RequestStatus.Status.READY);
            }
        }
    }

    private void sendTaskToWorker(CrackHashManagerRequest crackHashManagerRequest) {
        log.info("Sending request to worker: {}", crackHashManagerRequest);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        restTemplate.exchange(
                String.format("https://%s:%s/internal/api/worker/hash/crack/task", workerIp, workerPort),
                HttpMethod.POST,
                new HttpEntity<>(crackHashManagerRequest, headers),
                OkResponse.class);
    }

    @Scheduled(fixedDelay = 10000)
    void expireRequests() {
        requestStatusRepository.findAllByUpdatedBeforeAndStatusEquals(
                        new Date(System.currentTimeMillis() - expireTimeMinutes * 60 * 1000),
                        RequestStatus.Status.IN_PROGRESS)
                .forEach(requestStatus -> {
                    requestStatus.setStatus(Status.ERROR);
                    requestStatusRepository.save(requestStatus);
                });
    }
}
