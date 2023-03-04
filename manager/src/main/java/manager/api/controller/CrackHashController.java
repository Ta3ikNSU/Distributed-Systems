package manager.api.controller;

import lombok.extern.slf4j.Slf4j;
import manager.api.DTO.CrackRequestDTO;
import manager.api.DTO.RequestIdDTO;
import manager.api.DTO.RequestStatusDTO;
import manager.service.CrackHashService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Slf4j
@Controller
@RequestMapping("/api/hash")
public class CrackHashController {

    @Autowired
    CrackHashService crackHashService;

    @PostMapping("/crack")
    public ResponseEntity<RequestIdDTO> crackHash(@RequestBody CrackRequestDTO request) {
        log.info("Received request to crack hash: {}", request);
        return new ResponseEntity<>(
                new RequestIdDTO(crackHashService.crackHash(request.getHash(), request.getMaxLength())), HttpStatus.OK);
    }

    @GetMapping("/status/{requestId}")
    public ResponseEntity<RequestStatusDTO> getStatus(@PathVariable String requestId) {
        log.info("Received request to get status of request: {}", requestId);
        return new ResponseEntity<>(crackHashService.getStatus(requestId), HttpStatus.OK);
    }
}
