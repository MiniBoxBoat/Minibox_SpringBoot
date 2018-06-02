package com.minibox.dao.redisDao.box;

import com.minibox.po.BoxPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.minibox.constants.Redis_Schema.BOX_PREFIX;

@Component
public class RedisBoxDao {

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    public Optional<BoxPo> findBoxByBoxId(int boxId) {
        return redisTemplate.execute(redisConnection -> {
            BoxPo box = (BoxPo) redisTemplate.opsForValue().get(BOX_PREFIX + boxId);
            return Optional.ofNullable(box);
        },false, true);
    }

    public void removeBoxByBoxId(int boxId) {
         redisTemplate.delete(BOX_PREFIX + boxId);
    }

    public void insertBox(BoxPo box) {
        redisTemplate.opsForValue().set(BOX_PREFIX + box.getBoxId(), box);
    }

}
