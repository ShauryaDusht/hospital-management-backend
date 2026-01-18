package com.shaurya.hospitalManagement.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OnBoardDoctorRequestDto {
    private String userId;
    private String name;
    private String specialization;
}
