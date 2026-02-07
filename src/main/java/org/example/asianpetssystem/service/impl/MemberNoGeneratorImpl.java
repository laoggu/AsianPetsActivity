package org.example.asianpetssystem.service.impl;

import org.example.asianpetssystem.service.MemberNoGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

@Service
public class MemberNoGeneratorImpl implements MemberNoGenerator {

    private static final Logger logger = LoggerFactory.getLogger(MemberNoGeneratorImpl.class);
    private static final String MEMBER_NO_PREFIX = "APM";
    private static final String REDIS_KEY = "member:no:seq:";

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public String generateMemberNo() {
        String year = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"));
        String redisKey = REDIS_KEY + year;
        
        // 获取自增序号
        Long sequence = redisTemplate.opsForValue().increment(redisKey);
        
        // 设置过期时间（3年后过期）
        if (sequence != null && sequence == 1) {
            redisTemplate.expire(redisKey, 1095, TimeUnit.DAYS);
        }
        
        // 格式化为4位数字
        String seqStr = String.format("%04d", sequence);
        String memberNo = MEMBER_NO_PREFIX + year + seqStr;
        
        logger.info("生成会员编号 - memberNo={}", memberNo);
        return memberNo;
    }
}
