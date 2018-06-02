package com.minibox.dao.dbredis;

import com.minibox.dao.db.GroupMapper;
import com.minibox.dao.redisDao.group.RedisGroupDao;
import com.minibox.po.GroupPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class GroupDao {

    @Autowired
    private GroupMapper groupMapper;

    @Autowired
    private RedisGroupDao redisGroupDao;

    public List<GroupPo> findAllGroup() {
        return groupMapper.findAllGroup();
    }

    public List<GroupPo> findGroupsByDestination(String destination) {
        return groupMapper.findGroupsByDestination(destination);
    }

    public List<GroupPo> findGroupsByDestination() {
        return groupMapper.findGroupsByDestination();
    }

    public GroupPo findGroupByGroupId(int groupId) {
        Optional<GroupPo> optionalGroup = redisGroupDao.findGroupByGroupId(groupId);
        return optionalGroup.orElseGet(()-> {
            GroupPo group = groupMapper.findGroupByGroupId(groupId);
            if (group != null){
                redisGroupDao.insertGroup(group);
            }
            return group;
        });
    }

    public boolean reduceGroupBoxNum(int groupId, int num) {
        boolean flag = groupMapper.reduceGroupBoxNum(groupId, num);
        if (flag){
            redisGroupDao.removeGroup(groupId);
            return true;
        }
        return false;
    }

    public boolean increaseGroupBoxNum(int groupId, int num) {
        boolean flag = groupMapper.increaseGroupBoxNum(groupId, num);
        if (flag){
            redisGroupDao.removeGroup(groupId);
            return true;
        }
        return false;
    }


}
