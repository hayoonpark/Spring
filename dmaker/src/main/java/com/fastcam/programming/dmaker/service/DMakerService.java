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

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

import static com.fastcam.programming.dmaker.constant.DMakerConstant.MAX_JUNIOR_EXPERIENCE_YEARS;
import static com.fastcam.programming.dmaker.constant.DMakerConstant.MIN_SENIOR_EXPERIENCE_YEARS;
import static com.fastcam.programming.dmaker.exception.DMakerErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class DMakerService {
    private final DeveloperRepository developerRepository;
    private final RetiredDeveloperRepository retiredDeveloperRepository;

    public CreateDeveloper.Response createDeveloper(CreateDeveloper.Request request) {
        validateCreateDeveloperRequest(request);

//        Developer developer = createDeveloperFromRequest(request); -> 1회성 지역성 변수
//        developerRepository.save(developer);
//        return CreateDeveloper.Response.fromEntity(developer);
        return CreateDeveloper.Response. fromEntity(
                developerRepository.save(
                        createDeveloperFromRequest(request)
                )
        );
    }

    private Developer createDeveloperFromRequest(CreateDeveloper.Request request){
        return  Developer.builder()
                .developerLevel(request.getDeveloperLevel())
                .developerSkillType(request.getDeveloperSkillType())
                .experienceYears(request.getExperienceYears())
                .memberId(request.getMemberId())
                .status(StatusCode.EMPLOYED)
                .name(request.getName())
                .age(request.getAge())
                .build();

    }

    private void validateCreateDeveloperRequest(
            @NotNull CreateDeveloper.Request request
    ) {
        request.getDeveloperLevel()
                .validateExperienceYears(request.getExperienceYears());

//        validateDeveloperLevel(
//                request.getDeveloperLevel(),
//                request.getExperienceYears()
//        );

        developerRepository.findByMemberId(request.getMemberId())
                .ifPresent((developer -> {
                    throw new DMakerException(DUPLICATED_MEMBER_ID);
                }));

        //business validation
//        DeveloperValdationDto developerValdationDto = null;
//
//        developerValdationDto = validateDeveloperLevel(
//                request.getDeveloperLevel(),
//                request.getExperienceYears()
//        );
//
//        try {
//            if (developerRepository.findByMemberId(request.getMemberId()).isPresent()) {
//                developerValdationDto = new DeveloperValdationDto(
//                        DUPLICATED_MEMBER_ID,
//                        DUPLICATED_MEMBER_ID.getMessage());
//            }
//        }catch (Exception e){
//            log.error(e.getMessage());
//            developerValdationDto = new DeveloperValdationDto(
//                    INTERNAL_SERVER_ERROR,
//                    INTERNAL_SERVER_ERROR.getMessage()
//            );
//        }
////        .ifPresent((developer -> {
////            throw new DMakerException(DMakerErrorCode.DUPLICATED_MEMBER_ID);
////        }));
//
//        return developerValdationDto;
    }

    @Transactional(readOnly = true)
    public List<DeveloperDto> getAllEmployedDevelopers() {
        return developerRepository.findDevelopersByStatusEquals(StatusCode.EMPLOYED)
                .stream().map(DeveloperDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DeveloperDetailDto getDeveloper(String memberId) {
        return DeveloperDetailDto.fromEntity(getDeveloperByMemberId(memberId));
    }

    private Developer getDeveloperByMemberId(String memberId){
        return developerRepository.findByMemberId(memberId)
                .orElseThrow(
                () -> new DMakerException(NO_DEVELOPER)
        );
    }

    @Transactional
    public DeveloperDetailDto editDeveloper(
            String memberId, EditDeveloper.Request request
    ) {
        request.getDeveloperLevel().validateExperienceYears(
                request.getExperienceYears()
        );

//        validateDeveloperLevel(request.getDeveloperLevel(),
//                request.getExperienceYears());

//        Developer developer = getDeveloperByMemberId(memberId);
//        setDeveloperFromRequest(request, developer);
//        return DeveloperDetailDto.fromEntity(developer);

        return DeveloperDetailDto.fromEntity(
                getUpdatedDeveloperFromRequest(
                        request,
                        getDeveloperByMemberId(memberId)
                )
        );
    }

    private Developer getUpdatedDeveloperFromRequest(EditDeveloper.Request request, Developer developer) {
        developer.setDeveloperLevel(request.getDeveloperLevel());
        developer.setDeveloperSkillType(request.getDeveloperSkillType());
        developer.setExperienceYears(request.getExperienceYears());

        return developer;
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


//    public void validateDeveloperLevel(
//            DeveloperLevel developerLevel, Integer experienceYears
//    ){
//        developerLevel.validateExperienceYears(experienceYears);
//
////        if(experienceYears < developerLevel.getMinExperienceYears() ||
////                experienceYears > developerLevel.getMaxExperienceYears()){
////            throw new DMakerException(LEVEL_AND_EXPERIENCE_YEARS_NOT_MATCH);
////        }
//
//
////        if(developerLevel == DeveloperLevel.SENIOR
////                && experienceYears < MIN_SENIOR_EXPERIENCE_YEARS){
////
////            throw new DMakerException(LEVEL_AND_EXPERIENCE_YEARS_NOT_MATCH);
////        }
////
////        if(developerLevel ==  DeveloperLevel.JUNIOR
////                && (experienceYears < MAX_JUNIOR_EXPERIENCE_YEARS
////                || experienceYears > MIN_SENIOR_EXPERIENCE_YEARS)){
////            throw  new DMakerException(LEVEL_AND_EXPERIENCE_YEARS_NOT_MATCH);
////        }
////        if(developerLevel == DeveloperLevel.JUNIOR
////                && experienceYears > MAX_JUNIOR_EXPERIENCE_YEARS){
////            throw new DMakerException(LEVEL_AND_EXPERIENCE_YEARS_NOT_MATCH);
////        }
//////        return new DeveloperValdationDto(LEVEL_AND_EXPERIENCE_YEARS_NOT_MATCH,
//////                LEVEL_AND_EXPERIENCE_YEARS_NOT_MATCH.getMessage());
//    }

}