package worker.api.controller;

import worker.api.dto.OkResponse;
import worker.service.CrackService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.nsu.ccfit.schema.crack_hash_request.CrackHashManagerRequest;


@Slf4j
@Controller
@RequestMapping("/internal/api/worker")
public class WorkerInternalApiController {

    @Autowired
    private CrackService crackService;

    @PostMapping(value = "/hash/crack/task", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<OkResponse> getTask(@RequestBody CrackHashManagerRequest request) {
        log.info("getTask() : {}", request);
        crackService.putTask(request);
        return ResponseEntity.status(HttpStatus.OK).body(new OkResponse());
    }
}
