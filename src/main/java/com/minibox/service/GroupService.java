package com.minibox.service;

import com.minibox.dao.db.BoxMapper;
import com.minibox.dao.db.GroupMapper;
import com.minibox.dao.dbredis.BoxDao;
import com.minibox.dao.dbredis.GroupDao;
import com.minibox.po.GroupPo;
import com.minibox.util.Distance;
import com.minibox.vo.GroupVo;
import com.minibox.constants.ExceptionMessage;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class GroupService {

    @Resource
    private GroupDao groupDao;

    @Resource
    private BoxDao boxDao;

    public List<GroupVo> getGroupByDestination(String destination) {
        String destinationSql = "%" + destination + "%";
        List<GroupPo> groupPos = groupDao.findGroupsByDestination(destinationSql);
        return groupPosToGroupVos(groupPos);
    }

    public List<GroupVo> getGroupAround(double lat, double lng) {
        List<GroupPo> groupPos = groupDao.findAllGroup();
        List<GroupPo> filerGroupPos = groupPos.stream().filter(groupPo -> Distance.GetDistance(lat,lng, groupPo.getLat(),
                groupPo.getLng())<5).collect(Collectors.toList());
        return groupPosToGroupVos(filerGroupPos);
    }

    public GroupVo getGroupByGroupId(int groupId) {
        GroupPo groupPo = groupDao.findGroupByGroupId(groupId);
        Objects.requireNonNull(groupPo, ExceptionMessage.RESOURCE_NOT_FOUND);
        return  groupPoToGroupVo(groupPo);
    }

    private List<GroupVo> groupPosToGroupVos(List<GroupPo> groupPos){
        List<GroupVo> groupVos = new ArrayList<>();
        groupPos.forEach(groupPo -> groupVos.add(groupPoToGroupVo(groupPo)));
        return groupVos;
    }

    private GroupVo groupPoToGroupVo(GroupPo groupPo){
        int emptySmallBoxCount = boxDao.findEmptySmallBoxCountByGroupId(groupPo.getGroupId());
        return GroupVo.builder()
                .position(groupPo.getPosition())
                .quantity(groupPo.getQuantity())
                .lng(groupPo.getLng())
                .lat(groupPo.getLat())
                .groupId(groupPo.getGroupId())
                .emptySmallBoxNum(emptySmallBoxCount)
                .emptyLargeBoxNum(boxDao.findEmptyLargeBoxCountByGroupId(groupPo.getGroupId()))
                .build();
    }




}
