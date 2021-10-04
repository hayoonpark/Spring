package com.fastcam.programming.dmaker.dto;

import com.fastcam.programming.dmaker.entity.Developer;
import com.fastcam.programming.dmaker.exception.DMakerErrorCode;
import com.fastcam.programming.dmaker.type.DeveloperLevel;
import com.fastcam.programming.dmaker.type.DeveloperSkillType;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DMakerErrorResponse {
    private DeveloperLevel developerLevel;
    private DeveloperSkillType developerSkillType;
    private String memberId;

    public  static DMakerErrorResponse fromEntity(Developer developer){
        return DMakerErrorResponse.builder()
                .developerLevel(developer.getDeveloperLevel())
                .developerSkillType(developer.getDeveloperSkillType())
                .memberId(developer.getMemberId())
                .build();
    }



}
