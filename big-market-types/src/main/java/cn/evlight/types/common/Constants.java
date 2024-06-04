package cn.evlight.types.common;

public interface Constants {

    interface Split{
        String COMMA = ",";
        String COLON = ":";
        String SPACE = " ";
        String UNDERLINE = "_";
    }

    interface RedisKey {
        //策略
        String STRATEGY_AWARD_LIST_KEY = "strategy:award_list:";
        String STRATEGY_RATE_MAP_KEY = "strategy:rate_map:";
        String STRATEGY_RATE_RANGE_KEY = "strategy:rate_range:";
        String STRATEGY_KEY = "strategy:";
        String STRATEGY_RULE_VALUE_KEY = "strategy:rule_value:";
        String STRATEGY_RULE_TREE_KEY = "strategy:rule_tree:";
        String STRATEGY_AWARD_KEY = "strategy:award:";
        String STRATEGY_ENTITY_MAP_KEY = "strategy:entity_map:";
        String STRATEGY_AWARD_COUNT_QUERY_KEY = "strategy:award_count_query:";

        //活动
        String ACTIVITY_KEY = "activity:";
        String ACTIVITY_COUNT_KEY = "activity:count:";
        String ACTIVITY_SKU_STOCK_KEY = "activity:stock:";
        String ACTIVITY_SKU_COUNT_QUERY_KEY = "activity:sku_count_query:";
    }

    interface ExceptionInfo {
        String INVALID_PARAMS = "参数不合法";
        String DUPLICATE_KEY = "主键冲突";
        String INVALID_ACTIVITY_DATE = "当前时间未在活动日期范围内";
        String INVALID_ACTIVITY_STATE = "活动未开启";
        String ACTIVITY_STOCK_INSUFFICIENT = "活动剩余库存不足";
    }

}
