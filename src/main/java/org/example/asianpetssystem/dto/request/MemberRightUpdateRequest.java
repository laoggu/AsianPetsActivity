package org.example.asianpetssystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MemberRightUpdateRequest {

    @NotBlank(message = "会员等级不能为空")
    @Size(max = 20, message = "会员等级长度不能超过20")
    private String level;

    @NotBlank(message = "权益标题不能为空")
    @Size(max = 100, message = "权益标题长度不能超过100")
    private String title;

    @Size(max = 500, message = "权益描述长度不能超过500")
    private String description;

    @Size(max = 100, message = "图标长度不能超过100")
    private String icon;

    private Integer sortOrder;

    private Boolean isActive;
}
