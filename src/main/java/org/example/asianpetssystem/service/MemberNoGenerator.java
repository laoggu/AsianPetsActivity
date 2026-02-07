package org.example.asianpetssystem.service;

/**
 * 会员编号生成器
 * 生成规则：APM{年份}{4位序号}，如APM20240001
 */
public interface MemberNoGenerator {

    /**
     * 生成会员编号
     * @return 会员编号
     */
    String generateMemberNo();
}
