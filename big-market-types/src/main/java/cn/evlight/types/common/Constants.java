package cn.evlight.types.common;

public interface Constants {

    interface Split{
        String COMMA = ",";
        String COLON = ":";
        String SPACE = " ";
    }

    interface RedisKey {
        String STRATEGY_AWARD_KEY = "strategy:award:";
        String STRATEGY_RATE_MAP_KEY = "strategy:rate_map:";
        String STRATEGY_RATE_RANGE_KEY = "strategy:rate_range:";
        String STRATEGY_RULE_KEY = "strategy:entity:";
        String STRATEGY_RULE_VALUE_KEY = "strategy:rule_value:";
    }

}
