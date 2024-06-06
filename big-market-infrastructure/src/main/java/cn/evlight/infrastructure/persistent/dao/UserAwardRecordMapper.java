package cn.evlight.infrastructure.persistent.dao;

import cn.evlight.infrastructure.persistent.po.UserAwardRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户中奖记录表 Mapper 接口
 * </p>
 *
 * @author evlight
 * @since 2024-06-06
 */
@Mapper
public interface UserAwardRecordMapper extends BaseMapper<UserAwardRecord> {

}
