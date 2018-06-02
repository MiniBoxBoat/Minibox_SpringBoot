package com.minibox.dao.redisDao.group;

import com.minibox.po.GroupPo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.Optional;
import static org.junit.Assert.*;

public class RedisGroupDaoTest {

    @Autowired
    private RedisGroupDao redisGroupDao;
    private GroupPo group;

    @Before
    public void before(){
        group = new GroupPo();
        group.setGroupId(1);
        group.setPosition("fdsf");
        group.setLat(12);
        group.setLng(13);
        group.setQuantity(10);
        group.setEmpty(5);
    }

    public void insertGroup() {
        redisGroupDao.insertGroup(group);
        Optional<GroupPo> optional = redisGroupDao.findGroupByGroupId(group.getGroupId());
        redisGroupDao.removeGroup(group.getGroupId());
        assertEquals(12, optional.get().getLat(),0.1);
    }
}