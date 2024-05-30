package cn.evlight.types.common;

public interface Constants {

    interface Split{
        String COMMA = ",";
        String COLON = ":";
        String SPACE = " ";
        String UNDERLINE = "_";
    }

    interface RedisKey {
        String STRATEGY_AWARD_LIST_KEY = "strategy:award_list:";
        String STRATEGY_RATE_MAP_KEY = "strategy:rate_map:";
        String STRATEGY_RATE_RANGE_KEY = "strategy:rate_range:";
        String STRATEGY_KEY = "strategy:";
        String STRATEGY_RULE_VALUE_KEY = "strategy:rule_value:";
        String STRATEGY_RULE_TREE_KEY = "strategy:rule_tree:";
        String STRATEGY_AWARD_KEY = "strategy:award:";
        String STRATEGY_ENTITY_MAP_KEY = "strategy:entity_map:";
    }

}
