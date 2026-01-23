package org.example.asianpetssystem.service;
import org.example.asianpetssystem.dto.request.MemberApplyRequest;
import org.example.asianpetssystem.dto.request.UpdateProfileRequest;
import org.example.asianpetssystem.dto.response.MemberStatusResponse;
import org.example.asianpetssystem.dto.response.ProfileResponse;
import org.example.asianpetssystem.entity.AttachmentType;
import org.springframework.web.multipart.MultipartFile;

public interface MemberService {

    /**
     * 提交会员申请
     */
    void applyForMember(MemberApplyRequest request);

    /**
     * 获取会员状态
     */
    MemberStatusResponse getMemberStatus(Long id);

    /**
     * 获取会员资料
     */
    ProfileResponse getMemberProfile(String username);

    /**
     * 更新会员资料
     */
    void updateMemberProfile(UpdateProfileRequest request, String username);

    /**
     * 上传文件
     */
    String uploadFile(MultipartFile file, AttachmentType type);


}
