package com.minibox.dao.db;

import com.minibox.po.SalePo;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleMapper {

    boolean insertSaleInfo(SalePo sale);

}
