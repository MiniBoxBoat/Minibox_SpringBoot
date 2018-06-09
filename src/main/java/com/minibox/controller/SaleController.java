package com.minibox.controller;

import com.minibox.constants.Constants;
import com.minibox.dto.ResponseEntity;
import com.minibox.service.SaleService;
import com.minibox.vo.SaleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("sale")
public class SaleController {

    @Autowired
    private SaleService saleService;

    @GetMapping("notPaySale")
    public ResponseEntity<List<SaleVo>> showNotPayBox(String token){
        List<SaleVo> saleVos = saleService.getNotPayBoxByUserId(token);
        return new ResponseEntity<>(200, Constants.SUCCESS, saleVos);
    }

    @PatchMapping("payFlag")
    public ResponseEntity<Object> updateSalePayFlag(int saleInfoId){
        saleService.updateSalePayFlag(saleInfoId);
        return new ResponseEntity<>(200, Constants.SUCCESS, null);
    }
}
