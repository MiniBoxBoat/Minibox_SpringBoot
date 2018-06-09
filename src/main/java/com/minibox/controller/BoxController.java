package com.minibox.controller;

import com.minibox.dto.ResponseEntity;
import com.minibox.service.BoxService;
import com.minibox.vo.BoxVo;
import com.minibox.constants.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author MEI
 */
@RestController
@RequestMapping("box")
public class BoxController {

    @Autowired
    private BoxService boxService;

    @GetMapping("showUserUsingBoxes.do")
    public ResponseEntity<List<BoxVo>> showUserUsingBoxes(String taken) {
        List<BoxVo> boxes = boxService.getUsingBoxes(taken);
        return new ResponseEntity<>(200, Constants.SUCCESS, boxes);
    }

    @GetMapping("showUserReservingBoxes.do")
    public ResponseEntity<List<BoxVo>> showReservingBoxes(String taken) {
        List<BoxVo> boxes = boxService.getReservingBoxes(taken);
        return new ResponseEntity<>(200, Constants.SUCCESS, boxes);
    }

    @GetMapping("usingBoxes")
    public ResponseEntity<List<BoxVo>> showUsingBoxesByPersonId(String personId){
        List<BoxVo> boxVos = boxService.getUsingBoxesByPersonId(personId);
        return new ResponseEntity<>(200, Constants.SUCCESS, boxVos);
    }

    @GetMapping("openBox")
    public ResponseEntity<Object> openBox(int[] boxIds){
        boxService.openBoxes(boxIds);
        return new ResponseEntity<>(200, Constants.SUCCESS, null);
    }
}
