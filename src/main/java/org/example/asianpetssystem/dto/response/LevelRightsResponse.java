package org.example.asianpetssystem.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class LevelRightsResponse {

    private String level;
    private String levelName;
    private List<MemberRightResponse> rights;
}
