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
public class DeveloperValdationDto {
    private DMakerErrorCode errorCode;
    private String errorMessage;
}
