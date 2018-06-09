package com.minibox.dao.db;


import com.minibox.po.BoxPo;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author MEI
 */
@Repository
public interface BoxMapper {

    boolean updateBoxStatus(int boxId);

    BoxPo findBoxByBoxId(int boxId);

    List<BoxPo> findUsingBoxesByUserId(int userId);

    List<BoxPo> findReservingBoxedByUserId(int userId);

    List<BoxPo> findEmptySmallBoxByGroupId(int groupId);

    int findEmptySmallBoxCountByGroupId(int groupId);

    List<BoxPo> findEmptyLargeBoxByGroupId(int groupId);

    int findEmptyLargeBoxCountByGroupId(int groupId);

    List<BoxPo> getUsingBoxesByPersonId(String personId);

}
