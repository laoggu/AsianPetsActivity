package org.example.asianpetssystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.example.asianpetssystem.entity.AttachmentType;

import java.util.List;

@Data
public class MemberApplyRequest {

    @NotBlank(message = "公司名称不能为空")
    @Size(max = 200, message = "公司名称长度不能超过200个字符")
    private String companyName;

    @NotBlank(message = "统一社会信用代码不能为空")
    @Pattern(regexp = "^[0-9A-HJ-NPQRTUWXY]{2}[0-9]{6}[0-9A-HJ-NPQRTUWXY]{10}$",
             message = "统一社会信用代码格式不正确")
    private String creditCode;

    @NotNull(message = "联系人信息不能为空")
    @Size(min = 1, message = "至少需要一个联系人")
    private List<ContactRequest> contacts;

    @Size(min = 1, message = "至少需要上传营业执照")
    private List<AttachmentRequest> attachments;

    @Data
    public static class ContactRequest {
        @NotBlank(message = "联系人姓名不能为空")
        @Size(max = 50, message = "联系人姓名长度不能超过50个字符")
        private String name;

        @NotBlank(message = "手机号不能为空")
        @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
        private String mobile;

        @NotBlank(message = "邮箱不能为空")
        @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
                 message = "邮箱格式不正确")
        private String email;

        private Boolean isPrimary;
    }

    @Data
    public static class AttachmentRequest {
        @NotNull(message = "附件类型不能为空")
        private AttachmentType type;

        @NotBlank(message = "附件存储路径不能为空")
        private String ossKey;
    }
}
