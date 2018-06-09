package com.minibox.dao.db;

import com.minibox.po.SalePo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SaleMapper {

    boolean insertSaleInfo(SalePo sale);

    boolean updateSaleInfoPayFlag(int saleInfoId);

    List<SalePo> selectNotPaySaleInfoByUserId(int userId);

}
