package com.minibox.dao.redisDao.box;

import com.minibox.po.BoxPo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;
import static org.junit.Assert.assertEquals;

public class RedisBoxDaoTest {

    @Autowired
    private RedisBoxDao redisBoxDao;
    private BoxPo box;

    @Before
    public void before(){
        box = new BoxPo();
        box.setBoxId(1);
        box.setGroupId(1);
        box.setBoxStatus(0);
        box.setBoxSize("小");
    }

    public void findBoxByBoxId() {
        redisBoxDao.insertBox(box);
        Optional<BoxPo> boxPo = redisBoxDao.findBoxByBoxId(1);
        assertEquals(1, boxPo.get().getGroupId());
    }
}