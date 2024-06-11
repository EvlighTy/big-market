package cn.evlight.domain.strategy.service.ruleFilter.impl.after;

import cn.evlight.domain.strategy.model.entity.RuleFilterParamEntity;
import cn.evlight.domain.strategy.model.valobj.RuleFilterStateVO;
import cn.evlight.domain.strategy.model.valobj.StrategyAwardStockKeyVO;
import cn.evlight.domain.strategy.repository.IStrategyRepository;
import cn.evlight.domain.strategy.service.armory.IUserStrategyArmory;
import cn.evlight.domain.strategy.service.ruleFilter.AbstractAfterRuleFilter;
import cn.evlight.domain.strategy.service.ruleFilter.factory.after.DefaultRuleFilterTreeFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Description: 扣减库存过滤器
 * @Author: evlight
 * @Date: 2024/5/26
 */

@Slf4j
@Component("rule_stock")
public class RuleStockRuleFilter extends AbstractAfterRuleFilter {

    @Autowired
    private IStrategyRepository strategyRepository;

    @Autowired
    private IUserStrategyArmory userStrategyArmory;

    @Override
    public DefaultRuleFilterTreeFactory.Result doFilter(RuleFilterParamEntity ruleFilterParamEntity) {
        log.info("[后置规则过滤] 奖品库存");
        boolean success = userStrategyArmory.subtractStrategyAwardStock(ruleFilterParamEntity.getStrategyId(),
                ruleFilterParamEntity.getAwardId(),
                ruleFilterParamEntity.getEndDateTime());
        if(!success){
            log.info("奖品领取失败");
            return DefaultRuleFilterTreeFactory.Result.builder()
                    .state(RuleFilterStateVO.ALLOW)
                    .build();
        }
        //写入数据库库存更新延迟队列
        strategyRepository.sendToStrategyAwardConsumeQueue(StrategyAwardStockKeyVO.builder()
                        .strategyId(ruleFilterParamEntity.getStrategyId())
                        .awardId(ruleFilterParamEntity.getAwardId())
                .build());
        return DefaultRuleFilterTreeFactory.Result.builder()
                .state(RuleFilterStateVO.TAKE_OVER)
                .data(DefaultRuleFilterTreeFactory.ResultData.builder()
                        .awardId(ruleFilterParamEntity.getAwardId())
                        .build())
                .build();
    }

    @Override
    protected String ruleModel() {
        return "rule_stock";
    }

}
