package nsu.task1.manager.api.controller;

import nsu.task1.manager.api.DTO.CrackRequestDTO;
import nsu.task1.manager.api.DTO.RequestIdDTO;
import nsu.task1.manager.api.DTO.RequestStatusDTO;
import nsu.task1.manager.service.CrackHashService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller("/api/hash")
public class CrackHashController {

    @Autowired
    CrackHashService crackHashService;

    @PostMapping("/crack")
    public RequestIdDTO crackHash(@RequestBody CrackRequestDTO request) {
        return new RequestIdDTO(crackHashService.crackHash(request.getHash(), request.getMaxLength()));
    }

    @GetMapping("/status/{requestId}")
    public RequestStatusDTO getStatus(@PathVariable String requestId) {
        return crackHashService.getStatus(requestId);
    }
}
