package org.example.asianpetssystem.entity;

import lombok.Getter;

@Getter
public enum AttachmentType {
    BUSINESS_LICENSE("BUSINESS_LICENSE", "营业执照"),
    ORGANIZATION_CODE("ORGANIZATION_CODE", "组织机构代码证"),
    TAX_REGISTRATION("TAX_REGISTRATION", "税务登记证"),
    OTHER("OTHER", "其他");

    private final String code;
    private final String description;

    AttachmentType(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
