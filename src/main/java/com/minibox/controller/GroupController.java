package com.minibox.controller;

import com.minibox.dto.ResponseEntity;
import com.minibox.service.GroupService;
import com.minibox.vo.GroupVo;
import com.minibox.constants.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("group")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @PostMapping("showBoxGroup.do")
    public ResponseEntity<List<GroupVo>> showBoxGroupByKeyWord(String destination) {
        List<GroupVo> groupVos = groupService.getGroupByDestination(destination);
        return new ResponseEntity<>(200, Constants.SUCCESS, groupVos);
    }

    @PostMapping("showBoxGroupAround.do")
    public ResponseEntity<List<GroupVo>> showBoxGroupAround(double lat, double lng) {
        List<GroupVo> groupVos = groupService.getGroupAround(lat, lng);
        return new ResponseEntity<>(200, Constants.SUCCESS, groupVos);
    }

    @GetMapping("showBoxGroup.do")
    public ResponseEntity<GroupVo> showBoxGroup(int groupId) {
        GroupVo groupVo = groupService.getGroupByGroupId(groupId);
        return new ResponseEntity<>(200, Constants.SUCCESS, groupVo);
    }

}
