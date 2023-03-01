package nsu.task1.manager.api.controller;

import nsu.task1.manager.api.DTO.OkResponse;
import nsu.task1.manager.api.DTO.WorkerResponseDTO;
import nsu.task1.manager.service.CrackHashService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller("/api/internal")
public class InternalApiController {

    @Autowired
    CrackHashService crackHashService;

    @PatchMapping("/manager/hash/crack/request")
    public OkResponse workerCallbackHandler(@RequestBody WorkerResponseDTO workerResponseDTO) {
        crackHashService.handleWorkerCallback(workerResponseDTO);
        return new OkResponse();
    }
}
