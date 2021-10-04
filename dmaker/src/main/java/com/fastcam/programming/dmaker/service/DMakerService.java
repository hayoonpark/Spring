package com.fastcam.programming.dmaker.service;

import com.fastcam.programming.dmaker.code.StatusCode;
import com.fastcam.programming.dmaker.dto.*;
import com.fastcam.programming.dmaker.entity.Developer;
import com.fastcam.programming.dmaker.entity.RetiredDeveloper;
import com.fastcam.programming.dmaker.exception.DMakerErrorCode;
import com.fastcam.programming.dmaker.exception.DMakerException;
import com.fastcam.programming.dmaker.repository.DeveloperRepository;
import com.fastcam.programming.dmaker.repository.RetiredDeveloperRepository;
import com.fastcam.programming.dmaker.type.DeveloperLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.fastcam.programming.dmaker.exception.DMakerErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class DMakerService {
    private final DeveloperRepository developerRepository;
    private final RetiredDeveloperRepository retiredDeveloperRepository;

    public CreateDeveloper.Response createDeveloper(CreateDeveloper.Request request) {

        
        Developer developer = Developer.builder()
                .developerLevel(request.getDeveloperLevel())
                .developerSkillType(request.getDeveloperSkillType())
                .experienceYears(request.getExperienceYears())
                .memberId(request.getMemberId())
                .status(StatusCode.EMPLOYED)
                .name(request.getName())
                .age(request.getAge())
                .build();
        developerRepository.save(developer);
        return CreateDeveloper.Response.fromEntity(developer);
    }

    public DeveloperValdationDto ValidateCreateDeveloperRequest(CreateDeveloper.Request request) {
        //business validation
        DeveloperValdationDto developerValdationDto = null;

        developerValdationDto = validateDeveloperLevel(
                request.getDeveloperLevel(),
                request.getExperienceYears()
        );

        try {
            if (developerRepository.findByMemberId(request.getMemberId()).isPresent()) {
                developerValdationDto = new DeveloperValdationDto(
                        DUPLICATED_MEMBER_ID,
                        DUPLICATED_MEMBER_ID.getMessage());
            }
        }catch (Exception e){
            log.error(e.getMessage());
            developerValdationDto = new DeveloperValdationDto(
                    INTERNAL_SERVER_ERROR,
                    INTERNAL_SERVER_ERROR.getMessage()
            );
        }
//        .ifPresent((developer -> {
//            throw new DMakerException(DMakerErrorCode.DUPLICATED_MEMBER_ID);
//        }));

        return developerValdationDto;
    }

    public DeveloperValdationDto validateDeveloperLevel(DeveloperLevel developerLevel, Integer experienceYears){
        if(developerLevel == DeveloperLevel.SENIOR
                && experienceYears < 10){

//            throw new DMakerException(LEVEL_AND_EXPERIENCE_YEARS_NOT_MATCH);
        }
        return new DeveloperValdationDto(LEVEL_AND_EXPERIENCE_YEARS_NOT_MATCH,
                LEVEL_AND_EXPERIENCE_YEARS_NOT_MATCH.getMessage());
//        if(developerLevel ==  DeveloperLevel.JUNIOR
//                && (experienceYears < 4 || experienceYears > 10)){
//            throw  new DMakerException(LEVEL_AND_EXPERIENCE_YEARS_NOT_MATCH);
//        }
//        if(developerLevel == DeveloperLevel.JUNIOR && experienceYears > 4){
//            throw new DMakerException(LEVEL_AND_EXPERIENCE_YEARS_NOT_MATCH);
//        }
    }

    public List<DeveloperDto> getAllEmployedDevelopers() {
        return developerRepository.findDevelopersByStatusEquals(StatusCode.EMPLOYED)
                .stream().map(DeveloperDto::fromEntity)
                .collect(Collectors.toList());
    }

    public DeveloperDetailDto getDeveloper(String memberId) {
        return developerRepository.findByMemberId(memberId)
                .map(DeveloperDetailDto::fromEntity)
                .orElseThrow(
                        () -> new DMakerException(DMakerErrorCode.NO_DEVELOPER)
                );
    }

    @Transactional
    public DeveloperDetailDto editDeveloper(
            String memberId, EditDeveloper.Request request
    ) {
        Developer developer = developerRepository.findByMemberId(memberId)
                .orElseThrow(
                        () -> new DMakerException(DMakerErrorCode.NO_DEVELOPER)
                );
        developer.setDeveloperLevel(request.getDeveloperLevel());
        developer.setDeveloperSkillType(request.getDeveloperSkillType());
        developer.setExperienceYears(request.getExperienceYears());
        developer.setName(request.getName());
        developer.setAge(request.getAge());

        return DeveloperDetailDto.fromEntity(developer);
    }

    @Transactional
    public DeveloperDetailDto deleteDeveloper(
            String memberId
    ) {
        Developer developer = developerRepository.findByMemberId(memberId)
                .orElseThrow(
                        () -> new DMakerException(DMakerErrorCode.NO_DEVELOPER)
                );

        developer.setStatus(StatusCode.RETIRED);

        RetiredDeveloper retiredDeveloper = RetiredDeveloper.builder()
                .memberId(developer.getMemberId())
                .name(developer.getName())
                .build();
        retiredDeveloperRepository.save(retiredDeveloper);
        return DeveloperDetailDto.fromEntity(developer);
    }
}