package com.minibox.controller;

import com.minibox.dto.ResponseEntity;
import com.minibox.po.Transportation;
import com.minibox.service.TransportationService;
import com.minibox.constants.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("transportationInfos")
public class TransportationController {

    @Autowired
    private TransportationService transportationService;

    @PostMapping
    public ResponseEntity<Object> submitTransportationOrder(Transportation transportation, String token){
        transportationService.addTransportation(transportation, token);
        return new ResponseEntity<>(200, Constants.SUCCESS, null);
    }

    @PatchMapping("/{transportationId}/finishFlag")
    public ResponseEntity<Object> assureGetGoods(@PathVariable int transportationId){
        transportationService.updateFinishFlag(transportationId);
        return new ResponseEntity<>(200, Constants.SUCCESS, null);
    }

    @PatchMapping("/{transportationId}/score")
    public ResponseEntity<Object> serviceAssess(@PathVariable int transportationId, int score){
        transportationService.updateScore(transportationId, score);
        return new ResponseEntity<>(200, Constants.SUCCESS, null);
    }

    @PutMapping("/{transportationId}")
    public ResponseEntity<Object> updateTransportationInfo(@PathVariable int transportationId,
            Transportation transportation, String token){
        transportationService.updateTransportation(transportation, token);
        return new ResponseEntity<>(200, Constants.SUCCESS, null);
    }

    @GetMapping("{transportationId}")
    public ResponseEntity<Object> showTransportationInfo(@PathVariable int transportationId){
        Transportation transportation = transportationService.getTransportation(transportationId);
        return new ResponseEntity<>(200, Constants.SUCCESS, transportation);
    }

    @GetMapping("/using")
    public ResponseEntity<Object> showUsingTransportationInfos(String token){
        List<Transportation> transportationInfos = transportationService.getUsingTransportationInfos(token);
        return new ResponseEntity<>(200, Constants.SUCCESS, transportationInfos);
    }

    @GetMapping("/used")
    public  ResponseEntity<Object> showUsedTransporationInfos(String token){
        List<Transportation> transportationInfos = transportationService.getUsedTransportationInfos(token);
        return new ResponseEntity<>(200, Constants.SUCCESS, transportationInfos);
    }


}
