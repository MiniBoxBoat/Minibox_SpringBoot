package com.minibox.service;

import com.minibox.dao.db.OrderMapper;
import com.minibox.dao.db.SaleMapper;
import com.minibox.dao.dbredis.BoxDao;
import com.minibox.dao.dbredis.GroupDao;
import com.minibox.po.BoxPo;
import com.minibox.po.GroupPo;
import com.minibox.po.OrderPo;
import com.minibox.po.SalePo;
import com.minibox.service.util.JavaWebToken;
import com.minibox.socket.SocketConnection;
import com.minibox.service.util.ServiceExceptionChecking;
import com.minibox.vo.BoxVo;
import com.minibox.vo.GroupVo;
import com.minibox.vo.SaleVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;

@Service
public class SaleService {

    @Resource
    private SaleMapper saleMapper;

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private BoxDao boxDao;

    @Resource
    private GroupDao groupDao;

    @Transactional(rollbackFor = Exception.class)
    public void addSaleInfoAndRemoveOrder(int orderId, double cost){
        OrderPo order = orderMapper.findOrderByOrderId(orderId);
        SalePo salePo = orderPoToSalePo(order, cost);
        ServiceExceptionChecking.checkSqlExcute(saleMapper.insertSaleInfo(salePo) && orderMapper.removeOrder(orderId));
        SocketConnection.sendDataToHardware(order.getGroupId(), orderId);
    }

    private SalePo orderPoToSalePo(OrderPo order, double cost){
        SalePo salePo = new SalePo();
        salePo.setUserId(order.getUserId());
        salePo.setBoxId(order.getBoxId());
        salePo.setGroupId(order.getGroupId());
        salePo.setOrderTime(order.getOrderTime());
        salePo.setCost(cost);
        return salePo;
    }

    public List<SaleVo> getNotPayBoxByUserId(String token){
        int userId = JavaWebToken.getUserIdAndVerifyTakenFromTaken(token);
        List<SalePo> salePos = saleMapper.selectNotPaySaleInfoByUserId(userId);
        return salePosToSaleVos(salePos);
    }

    private List<SaleVo> salePosToSaleVos(List<SalePo> salePos){
        List<SaleVo> saleVos = new LinkedList<>();
        salePos.stream().forEach(salePo -> saleVos.add(salePoToSaleVo(salePo)));
        return saleVos;
    }

    private SaleVo salePoToSaleVo(SalePo sale){
        BoxPo box = boxDao.findBoxByBoxId(sale.getBoxId());
        GroupPo group = groupDao.findGroupByGroupId(sale.getGroupId());
        return SaleVo.builder().saleInfoId(sale.getSaleInfoId())
                .boxSize(box.getBoxSize())
                .boxId(box.getBoxId())
                .orderTime(sale.getOrderTime())
                .cost(sale.getCost())
                .position(group.getPosition())
                .userId(sale.getUserId())
                .build();
    }

    public void updateSalePayFlag(int saleInfoId){
        ServiceExceptionChecking.checkSqlExcute(saleMapper.updateSaleInfoPayFlag(saleInfoId));
    }

}
