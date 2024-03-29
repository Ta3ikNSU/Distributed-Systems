package manager.service;

import lombok.extern.slf4j.Slf4j;
import manager.api.DTO.RequestStatusDTO;
import manager.model.entity.Request;
import manager.model.entity.RequestStatus;
import manager.model.entity.RequestStatus.Status;
import manager.model.mapper.RequestStatusMapper;
import manager.model.repository.RequestStatusRepository;
import manager.model.repository.RequestsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.nsu.ccfit.schema.crack_hash_request.CrackHashManagerRequest;
import ru.nsu.ccfit.schema.crack_hash_response.CrackHashWorkerResponse;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;


@Service
@Slf4j
@Transactional(isolation = Isolation.SERIALIZABLE)
public class CrackHashService {
    private final RequestStatusMapper requestStatusMapper = RequestStatusMapper.INSTANCE;
    private final CrackHashManagerRequest.Alphabet alphabet = new CrackHashManagerRequest.Alphabet();
    @Value("${crackHashService.worker.ip}")
    String workerIp;
    @Value("${crackHashService.worker.port}")
    Integer workerPort;
    @Value("${crackHashService.manager.expireTimeMinutes}")
    Integer expireTimeMinutes;

    @Value("${crackHashService.manager.countWorkers}")
    Integer countOfWorker;

    @Autowired
    private RabbitProducer rabbitProducer;

    @Autowired
    private RequestStatusRepository requestStatusRepository;

    @Autowired
    private RequestsRepository requestsRepository;

    @PostConstruct
    private void init() {
        String alphabetString = "abcdefghijklmnopqrstuvwxyz0123456789";
        List.of(alphabetString.split("")).forEach(alphabet.getSymbols()::add);
    }

    public String crackHash(String hash, int maxLength) {
        RequestStatus entity = requestStatusRepository.insert(new RequestStatus(countOfWorker));
        IntStream.range(0, countOfWorker).forEach(i -> {
            CrackHashManagerRequest crackHashManagerRequest = new CrackHashManagerRequest();
            crackHashManagerRequest.setHash(hash);
            crackHashManagerRequest.setMaxLength(maxLength);
            crackHashManagerRequest.setRequestId(entity.getRequestId());
            crackHashManagerRequest.setPartNumber(i);
            crackHashManagerRequest.setPartCount(countOfWorker);
            crackHashManagerRequest.setAlphabet(alphabet);
            sendTaskToWorker(crackHashManagerRequest);
        });
        return entity.getRequestId();
    }

    public RequestStatusDTO getStatus(String requestId) {
        return requestStatusMapper.toRequestStatusDTO(requestStatusRepository.findByRequestId(requestId));
    }

    public void handleWorkerCallback(CrackHashWorkerResponse crackHashWorkerResponse) {
        RequestStatus requestStatus = requestStatusRepository.findByRequestId(crackHashWorkerResponse.getRequestId());
        if (requestStatus.getStatus() == RequestStatus.Status.IN_PROGRESS) {
            if (crackHashWorkerResponse.getAnswers() != null) {
                requestStatus.getResult().addAll(crackHashWorkerResponse.getAnswers().getWords());
                requestStatus.getNotAnsweredWorkers().remove(crackHashWorkerResponse.getPartNumber());
                if (requestStatus.getNotAnsweredWorkers().isEmpty()) {
                    requestStatus.setStatus(RequestStatus.Status.READY);
                }
                requestStatusRepository.save(requestStatus);
            }
        }
    }

    private void sendTaskToWorker(CrackHashManagerRequest crackHashManagerRequest) {
        if (!rabbitProducer.trySendMessage(crackHashManagerRequest)) {
            requestsRepository.insert(new Request(crackHashManagerRequest));
        }
    }

    @Scheduled(fixedDelay = 10000)
    void expireRequests() {
        requestStatusRepository.findAllByUpdatedBeforeAndStatusEquals(new Date(System.currentTimeMillis() - expireTimeMinutes * 60 * 1000), RequestStatus.Status.IN_PROGRESS).forEach(requestStatus -> {
            requestStatus.setStatus(Status.ERROR);
            requestStatusRepository.save(requestStatus);
        });
    }
}
