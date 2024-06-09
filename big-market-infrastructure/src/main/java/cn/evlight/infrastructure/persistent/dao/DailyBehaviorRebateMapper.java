package cn.evlight.infrastructure.persistent.dao;

import cn.evlight.infrastructure.persistent.po.DailyBehaviorRebate;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 日常行为返利活动配置 Mapper 接口
 * </p>
 *
 * @author evlight
 * @since 2024-06-09
 */
@Mapper
public interface DailyBehaviorRebateMapper extends BaseMapper<DailyBehaviorRebate> {

    List<DailyBehaviorRebate> getDailyBehaviorRebateConfig(DailyBehaviorRebate dailyBehaviorRebate);
}
