package com.fastcam.programming.dmaker.controller;

import com.fastcam.programming.dmaker.dto.*;
import com.fastcam.programming.dmaker.exception.DMakerException;
import com.fastcam.programming.dmaker.service.DMakerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class DMakerController {
    private final DMakerService dMakerService;

    @PostMapping("/create-developer")
    public CreateDeveloper.Response createDeveloper(
            @Valid @RequestBody final CreateDeveloper.Request request
    ) {
        log.info("request : {}", request);

//        DeveloperValdationDto developerValdationDto =
//                dMakerService.validateCreateDeveloperRequest(request);
//
//        if(developerValdationDto != null){
//            return CreateDeveloper.Response.builder()
//                    .errorCode(developerValdationDto.getErrorCode())
//                    .errorMessage(developerValdationDto.getErrorMessage())
//                    .build();
//        }
        return  dMakerService.createDeveloper(request);
    }

    @GetMapping("/developers")
    public ResponseEntity<List<DeveloperDto>> getDevelopers() {
        return ResponseEntity.ok(
                dMakerService.getAllEmployedDevelopers()
        );
    }

    @GetMapping("/developer/{memberId}")
    public ResponseEntity<DeveloperDetailDto> getDeveloper(
            @PathVariable final String memberId
    ) {
        return ResponseEntity.ok(
                dMakerService.getDeveloper(memberId)
        );
    }

    @PutMapping("/developer/{memberId}")
    public ResponseEntity<DeveloperDetailDto> updateDeveloper(
            @PathVariable final String memberId,
            @RequestBody final EditDeveloper.Request request
    ) {
        log.info("GET /developers HTTP/1.1");

        return ResponseEntity.ok(
                dMakerService.editDeveloper(memberId, request)
        );
    }

    @DeleteMapping("/developer/{memberId}")
    public ResponseEntity<DeveloperDetailDto> deleteDeveloper(
            @PathVariable final String memberId
    ) {
        return ResponseEntity.ok(
                dMakerService.deleteDeveloper(memberId)
        );
    }


}
