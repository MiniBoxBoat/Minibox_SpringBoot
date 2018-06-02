package com.minibox.dao.redisDao.group;

import com.minibox.po.GroupPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.minibox.constants.Redis_Schema.GROUP_PREFIX;

@Component
public class RedisGroupDao {

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    public Optional<GroupPo> findGroupByGroupId(int groupId) {
        return redisTemplate.execute(redisConnection -> {
            GroupPo group = (GroupPo) redisTemplate.opsForValue().get(GROUP_PREFIX + groupId);
            return Optional.ofNullable(group);
        }, false, true);
    }

    public void removeGroup(int groupId) {
        redisTemplate.delete(GROUP_PREFIX + groupId);
    }

    public void insertGroup(GroupPo group) {
        redisTemplate.opsForValue().set(GROUP_PREFIX + group.getGroupId(), group);
    }
}
