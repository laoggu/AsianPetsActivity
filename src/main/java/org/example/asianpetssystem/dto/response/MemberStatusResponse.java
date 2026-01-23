package org.example.asianpetssystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.asianpetssystem.common.enums.MemberLevel;
import org.example.asianpetssystem.common.enums.MemberStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberStatusResponse {
    private MemberStatus status;
    private MemberLevel level;
    private LocalDateTime expireAt;
    private String companyName;
    private String creditCode;
}
