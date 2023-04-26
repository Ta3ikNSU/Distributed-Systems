package worker.service;

import lombok.extern.slf4j.Slf4j;
import org.paukov.combinatorics.CombinatoricsFactory;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.nsu.ccfit.schema.crack_hash_request.CrackHashManagerRequest;
import ru.nsu.ccfit.schema.crack_hash_response.CrackHashWorkerResponse;
import ru.nsu.ccfit.schema.crack_hash_response.CrackHashWorkerResponse.Answers;
import worker.api.dto.OkResponse;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.paukov.combinatorics.CombinatoricsFactory.createMultiCombinationGenerator;
import static org.paukov.combinatorics.CombinatoricsFactory.createPermutationWithRepetitionGenerator;

@Service
@Slf4j
public class CrackService {

    @Value("${crackHashService.manager.ip}")
    String managerIp;
    @Value("${crackHashService.manager.port}")
    Integer managerPort;

    ExecutorService executorService = Executors.newFixedThreadPool(10);

    @Autowired
    RabbitProducer rabbitProducer;

    public void putTask(CrackHashManagerRequest request) {
//        executorService.execute(() -> {
            crackCode(request);
//        });
    }

    public CrackHashWorkerResponse crackCode(CrackHashManagerRequest request) {
        log.info("progress task: {}", request);
        ICombinatoricsVector<String> vector = CombinatoricsFactory.createVector(request.getAlphabet().getSymbols());
        List<String> answers = new ArrayList<>();
        for (int i = 1; i <= request.getMaxLength(); i++) {
            Generator<String> gen = createPermutationWithRepetitionGenerator(vector, i);
            long countPermutations = gen.getNumberOfGeneratedObjects();
            long startIndex = countPermutations / request.getPartCount() * request.getPartNumber();
            if (countPermutations % request.getPartCount() != 0 ) {
                if (request.getPartNumber() < countPermutations % request.getPartCount()){
                    startIndex += request.getPartNumber();
                } else if (request.getPartNumber() >= countPermutations % request.getPartCount()) {
                    startIndex += countPermutations % request.getPartCount();
                }
            }
            for (var string : gen.generateObjectsRange(startIndex, startIndex + countPermutations / request.getPartCount())) {
                MessageDigest md5 = null;
                try {
                    md5 = MessageDigest.getInstance("MD5");
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                }
                String inputString = String.join("", string.getVector());
                String hash = (new HexBinaryAdapter()).marshal(md5.digest(inputString.getBytes()));
                if (request.getHash().equalsIgnoreCase(hash)) {
                    answers.add(String.join("", string.getVector()));
                    log.info("added answer : {}", String.join("", string.getVector()));
                }
            }
        }
        return buildResponse(request.getRequestId(), request.getPartNumber(), answers);
    }

    private CrackHashWorkerResponse buildResponse(String requestId, int partNumber, List<String> answers) {
        Answers answer = new Answers();
        answer.getWords().addAll(answers);
        CrackHashWorkerResponse response = new CrackHashWorkerResponse();
        response.setRequestId(requestId);
        response.setPartNumber(partNumber);
        response.setAnswers(answer);
        return response;
    }
}
