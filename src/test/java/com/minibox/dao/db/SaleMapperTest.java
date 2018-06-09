package com.minibox.dao.db;

import com.minibox.po.SalePo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("dev")
public class SaleMapperTest {

    @Autowired
    private SaleMapper saleMapper;
    private SalePo sale;

    @Before
    public void before(){
        sale = SalePo.builder()
                .userId(131)
                .boxId(101)
                .groupId(1)
                .payTime("2018-2-15 10:00:00")
                .orderTime("2018-2-15 10:00:01")
                .cost(5.00)
                .build();
    }

    @Test
    public void insertSaleInfoTest(){
        boolean flag = saleMapper.insertSaleInfo(sale);
        Assert.assertEquals(true, flag);

    }

    @Test
    public void selectNotPaySaleInfoByUserIdTest(){
        List<SalePo> salePos = saleMapper.selectNotPaySaleInfoByUserId(136);
        assertEquals(1, salePos.size());
    }

    @Test
    public void updateSaleInfoPayFlagTest(){
        boolean flag = saleMapper.updateSaleInfoPayFlag(2);
        assertTrue(flag);
    }

}