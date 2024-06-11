package cn.evlight.infrastructure.persistent.repository;

import cn.bugstack.middleware.db.router.strategy.IDBRouterStrategy;
import cn.evlight.domain.activity.event.ActivitySkuStockZeroMessageEvent;
import cn.evlight.domain.activity.model.aggregate.CreatePartakeOrderAggregate;
import cn.evlight.domain.activity.model.aggregate.CreateQuotaOrderAggregate;
import cn.evlight.domain.activity.model.entity.*;
import cn.evlight.domain.activity.model.valobj.ActivitySkuStockKeyVO;
import cn.evlight.domain.activity.model.valobj.ActivityStateVO;
import cn.evlight.domain.activity.model.valobj.UserRaffleOrderStateVO;
import cn.evlight.domain.activity.repository.IActivityRepository;
import cn.evlight.infrastructure.event.EventPublisher;
import cn.evlight.infrastructure.persistent.dao.*;
import cn.evlight.infrastructure.persistent.po.*;
import cn.evlight.infrastructure.persistent.redis.IRedisService;
import cn.evlight.types.common.Constants;
import cn.evlight.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Description: 活动仓储
 * @Author: evlight
 * @Date: 2024/6/1
 */

@Slf4j
@Repository
public class ActivityRepository implements IActivityRepository {

    @Autowired
    private IRedisService redisService;

    @Autowired
    private IRaffleActivityDao raffleActivityDao;

    @Autowired
    private IRaffleActivitySkuDao raffleActivitySkuDao;

    @Autowired
    private IRaffleActivityCountDao raffleActivityCountDao;

    @Autowired
    private IRaffleActivityOrderDao raffleActivityOrderDao;

    @Autowired
    private IRaffleActivityAccountDao raffleActivityAccountDao;

    @Autowired
    private UserRaffleOrderMapper userRaffleOrderMapper;

    @Autowired
    private RaffleActivityAccountMonthMapper raffleActivityAccountMonthMapper;

    @Autowired
    private RaffleActivityAccountDayMapper raffleActivityAccountDayMapper;

    @Autowired
    private IDBRouterStrategy dbRouter;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private EventPublisher eventPublisher;

    @Autowired
    private ActivitySkuStockZeroMessageEvent activitySkuStockZeroMessageEvent;

    @Override
    public ActivitySkuEntity queryActivitySkuBySku(Long sku) {
        RaffleActivitySku raffleActivitySku = raffleActivitySkuDao.queryActivitySku(sku);
        return ActivitySkuEntity.builder()
                .sku(raffleActivitySku.getSku())
                .activityId(raffleActivitySku.getActivityId())
                .activityCountId(raffleActivitySku.getActivityCountId())
                .stockCount(raffleActivitySku.getStockCount())
                .stockCountSurplus(raffleActivitySku.getStockCountSurplus())
                .build();
    }

    @Override
    public ActivityEntity queryRaffleActivityByActivityId(Long activityId) {
        //先查询redis
        String cacheKey = Constants.RedisKey.ACTIVITY_KEY + activityId;
        ActivityEntity activityEntity = redisService.getValue(cacheKey);
        if (null != activityEntity){
            return activityEntity;
        }
        //再查询数据库
        RaffleActivity raffleActivity = raffleActivityDao.queryRaffleActivityByActivityId(activityId);
        activityEntity = ActivityEntity.builder()
                .activityId(raffleActivity.getActivityId())
                .activityName(raffleActivity.getActivityName())
                .activityDesc(raffleActivity.getActivityDesc())
                .beginDateTime(raffleActivity.getBeginDateTime())
                .endDateTime(raffleActivity.getEndDateTime())
                .strategyId(raffleActivity.getStrategyId())
                .state(ActivityStateVO.valueOf(raffleActivity.getState()))
                .build();
        redisService.setValue(cacheKey, activityEntity);
        return activityEntity;
    }

    @Override
    public ActivityCountEntity queryRaffleActivityCountByActivityCountId(Long activityCountId) {
        //先查询redis
        String cacheKey = Constants.RedisKey.ACTIVITY_COUNT_KEY + activityCountId;
        ActivityCountEntity activityCountEntity = redisService.getValue(cacheKey);
        if (null != activityCountEntity){
            return activityCountEntity;
        }
        //再查询数据库
        RaffleActivityCount raffleActivityCount = raffleActivityCountDao.queryRaffleActivityCountByActivityCountId(activityCountId);
        activityCountEntity = ActivityCountEntity.builder()
                .activityCountId(raffleActivityCount.getActivityCountId())
                .totalCount(raffleActivityCount.getTotalCount())
                .dayCount(raffleActivityCount.getDayCount())
                .monthCount(raffleActivityCount.getMonthCount())
                .build();
        redisService.setValue(cacheKey, activityCountEntity);
        return activityCountEntity;
    }

    @Override
    public void saveOrder(CreateQuotaOrderAggregate createQuotaOrderAggregate) {
        try {
            //订单对象
            ActivityOrderEntity activityOrderEntity = createQuotaOrderAggregate.getActivityOrderEntity();
            RaffleActivityOrder raffleActivityOrder = RaffleActivityOrder.builder()
                        .userId(activityOrderEntity.getUserId())
                        .sku(activityOrderEntity.getSku())
                        .activityId(activityOrderEntity.getActivityId())
                        .activityName(activityOrderEntity.getActivityName())
                        .strategyId(activityOrderEntity.getStrategyId())
                        .orderId(activityOrderEntity.getOrderId())
                        .orderTime(activityOrderEntity.getOrderTime())
                        .totalCount(activityOrderEntity.getTotalCount())
                        .dayCount(activityOrderEntity.getDayCount())
                        .monthCount(activityOrderEntity.getMonthCount())
                        .state(activityOrderEntity.getState().getCode())
                        .outBusinessNo(activityOrderEntity.getOutBusinessNo())
                        .build();
            //账户对象
            //总额度账户
            RaffleActivityAccount raffleActivityAccount = RaffleActivityAccount.builder()
                        .userId(createQuotaOrderAggregate.getUserId())
                        .activityId(createQuotaOrderAggregate.getActivityId())
                        .totalCount(activityOrderEntity.getTotalCount())
                        .totalCountSurplus(activityOrderEntity.getTotalCount())
                        .dayCount(activityOrderEntity.getDayCount())
                        .dayCountSurplus(activityOrderEntity.getDayCount())
                        .monthCount(activityOrderEntity.getMonthCount())
                        .monthCountSurplus(activityOrderEntity.getMonthCount())
                        .build();
            //月额度账户
            RaffleActivityAccountMonth raffleActivityAccountMonth = RaffleActivityAccountMonth.builder()
                        .userId(activityOrderEntity.getUserId())
                        .activityId(activityOrderEntity.getActivityId())
                        .month(RaffleActivityAccountMonth.getCurrentMonth())
                        .monthCount(activityOrderEntity.getMonthCount())
                        .monthCountSurplus(activityOrderEntity.getMonthCount())
                        .build();
            //日额度账户
            RaffleActivityAccountDay raffleActivityAccountDay = RaffleActivityAccountDay.builder()
                        .userId(activityOrderEntity.getUserId())
                        .activityId(activityOrderEntity.getActivityId())
                        .day(RaffleActivityAccountDay.getCurrentDay())
                        .dayCount(activityOrderEntity.getDayCount())
                        .dayCountSurplus(activityOrderEntity.getDayCount())
                        .build();
            //事务
            dbRouter.doRouter(createQuotaOrderAggregate.getUserId());
            transactionTemplate.execute(status -> {
                try {
                    //保存订单
                    raffleActivityOrderDao.insert(raffleActivityOrder);
                    //更新总额度账户
                    int count = raffleActivityAccountDao.updateAccountQuota(raffleActivityAccount);
                    if (count == 0) {
                        //总额度账户账户不存在 创建
                        raffleActivityAccountDao.insert(raffleActivityAccount);
                    }
                    //更新月额度账户
                    raffleActivityAccountMonthMapper.addQuota(raffleActivityAccountMonth);
                    //更新日额度账户
                    raffleActivityAccountDayMapper.addQuota(raffleActivityAccountDay);
                    return 1;
                } catch (DuplicateKeyException e) {
                    status.setRollbackOnly();
                    log.error("写入订单记录，唯一索引冲突 userId: {} activityId: {} sku: {}", activityOrderEntity.getUserId(), activityOrderEntity.getActivityId(), activityOrderEntity.getSku(), e);
                    throw new AppException(Constants.ExceptionInfo.DUPLICATE_KEY);
                }
            });
        } finally {
            dbRouter.clear();
        }
    }

    @Override
    public boolean cacheActivitySkuStockCount(String cacheKey, Integer stockCount) {
        if(redisService.isExists(cacheKey)){
            return false;
        }
        redisService.setAtomicLong(cacheKey, stockCount);
        return true;
    }

    @Override
    public boolean subtractActivitySkuStock(String cacheKey, Long sku, LocalDateTime endDate) {
        if(!redisService.isExists(cacheKey)){
            //商品不存在或未进行缓存预热
            log.info("商品不存在或未进行缓存预热");
            return false;
        }
        long stockCount = redisService.decr(cacheKey);
        if(stockCount < 0){
            //库存不足
            return false;
        }
        if(stockCount == 0){
            //库存耗尽，发送MQ消息
            log.info("库存耗尽");
            eventPublisher.publish(activitySkuStockZeroMessageEvent.topic(), activitySkuStockZeroMessageEvent.buildEventMessage(sku));
        }
        String lockKey = cacheKey + Constants.Split.COLON + (stockCount + 1);
        long expired = endDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() - System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1);
        Boolean locked = redisService.setNx(lockKey, expired, TimeUnit.MILLISECONDS);
        if(!locked){
            //加锁失败
            return false;
        }
        return true;
    }

    @Override
    public void sendToActivityStockConsumeQueue(ActivitySkuStockKeyVO activitySkuStockKeyVO) {
        String cacheKey = Constants.RedisKey.ACTIVITY_SKU_COUNT_QUERY_KEY;
        RBlockingQueue<ActivitySkuStockKeyVO> blockingQueue = redisService.getBlockingQueue(cacheKey);
        RDelayedQueue<ActivitySkuStockKeyVO> delayedQueue = redisService.getDelayedQueue(blockingQueue);
        delayedQueue.offer(activitySkuStockKeyVO, 3, TimeUnit.SECONDS);
    }

    @Override
    public ActivitySkuStockKeyVO takeQueueValue() {
        String cacheKey = Constants.RedisKey.ACTIVITY_SKU_COUNT_QUERY_KEY;
        RBlockingQueue<ActivitySkuStockKeyVO> blockingQueue = redisService.getBlockingQueue(cacheKey);
        return blockingQueue.poll();
    }

    @Override
    public void clearQueueValue(Long sku) {
        String cacheKey = Constants.RedisKey.ACTIVITY_SKU_COUNT_QUERY_KEY /*+ sku*/;
        RBlockingQueue<ActivitySkuStockKeyVO> blockingQueue = redisService.getBlockingQueue(cacheKey);
        blockingQueue.delete();
    }

    @Override
    public void updateActivitySkuStock(Long sku) {
        raffleActivitySkuDao.updateActivitySkuStock(sku);
    }

    @Override
    public void clearActivitySkuStock(Long sku) {
        //缓存标志位表示当前sku库存已清空
        String cacheKey = Constants.RedisKey.ACTIVITY_SKU_STOCK_ZERO_KEY + sku;
        redisService.setNx(cacheKey);
        //清空库存
        raffleActivitySkuDao.clearActivitySkuStock(sku);
    }

    @Override
    public UserRaffleOrderEntity queryUnUsedRaffleOrder(Long activityId, String userId) {
        UserRaffleOrder userRaffleOrder = userRaffleOrderMapper.queryUnUsedRaffleOrder(UserRaffleOrder.builder()
                .activityId(activityId)
                .userId(userId)
                .build());
        if (userRaffleOrder == null){
            return null;
        }
        return UserRaffleOrderEntity.builder()
                .userId(userRaffleOrder.getUserId())
                .activityId(userRaffleOrder.getActivityId())
                .activityName(userRaffleOrder.getActivityName())
                .strategyId(userRaffleOrder.getStrategyId())
                .orderId(userRaffleOrder.getOrderId())
                .orderTime(userRaffleOrder.getOrderTime())
                .orderState(UserRaffleOrderStateVO.valueOf(userRaffleOrder.getOrderState()))
                .build();
    }

    @Override
    public RaffleActivityAccountEntity queryRaffleActivityAccount(Long activityId, String userId) {
        RaffleActivityAccount raffleActivityAccount = raffleActivityAccountDao.queryRaffleActivityAccount(RaffleActivityAccount.builder()
                .activityId(activityId)
                .userId(userId)
                .build());
        if(raffleActivityAccount == null){
            return null;
        }
        return RaffleActivityAccountEntity.builder()
                .userId(raffleActivityAccount.getUserId())
                .activityId(raffleActivityAccount.getActivityId())
                .totalCount(raffleActivityAccount.getTotalCount())
                .totalCountSurplus(raffleActivityAccount.getTotalCountSurplus())
                .dayCount(raffleActivityAccount.getDayCount())
                .dayCountSurplus(raffleActivityAccount.getDayCountSurplus())
                .monthCount(raffleActivityAccount.getMonthCount())
                .monthCountSurplus(raffleActivityAccount.getMonthCountSurplus())
                .build();
    }

    @Override
    public RaffleActivityAccountMonthEntity queryRaffleActivityAccountMonth(Long activityId, String userId, String monthDateTime) {
        RaffleActivityAccountMonth raffleActivityAccountMonth = raffleActivityAccountMonthMapper.queryRaffleActivityAccountMonth(RaffleActivityAccountMonth.builder()
                .activityId(activityId)
                .userId(userId)
                .month(monthDateTime)
                .build());
        if(raffleActivityAccountMonth == null){
            return null;
        }
        return RaffleActivityAccountMonthEntity.builder()
                .userId(raffleActivityAccountMonth.getUserId())
                .activityId(raffleActivityAccountMonth.getActivityId())
                .month(raffleActivityAccountMonth.getMonth())
                .monthCount(raffleActivityAccountMonth.getMonthCount())
                .monthCountSurplus(raffleActivityAccountMonth.getMonthCountSurplus())
                .build();
    }

    @Override
    public RaffleActivityAccountDayEntity queryRaffleActivityAccountDay(Long activityId, String userId, String dayDateTime) {
        RaffleActivityAccountDay raffleActivityAccountDay = raffleActivityAccountDayMapper.queryRaffleActivityAccountDay(RaffleActivityAccountDay.builder()
                .activityId(activityId)
                .userId(userId)
                .day(dayDateTime)
                .build());
        if(raffleActivityAccountDay == null){
            return null;
        }
        return RaffleActivityAccountDayEntity.builder()
                .userId(raffleActivityAccountDay.getUserId())
                .activityId(raffleActivityAccountDay.getActivityId())
                .day(raffleActivityAccountDay.getDay())
                .dayCount(raffleActivityAccountDay.getDayCount())
                .dayCountSurplus(raffleActivityAccountDay.getDayCountSurplus())
                .build();
    }

    @Override
    public void savePartakeOrderAggregate(CreatePartakeOrderAggregate createPartakeOrderAggregate) {
        try {
            String userId = createPartakeOrderAggregate.getUserId();
            Long activityId = createPartakeOrderAggregate.getActivityId();
            RaffleActivityAccountEntity raffleActivityAccount = createPartakeOrderAggregate.getRaffleActivityAccount();
            RaffleActivityAccountMonthEntity raffleActivityAccountMonthEntity = createPartakeOrderAggregate.getRaffleActivityAccountMonthEntity();
            RaffleActivityAccountDayEntity raffleActivityAccountDayEntity = createPartakeOrderAggregate.getRaffleActivityAccountDayEntity();
            UserRaffleOrderEntity userRaffleOrderEntity = createPartakeOrderAggregate.getUserRaffleOrderEntity();
            //统一切换路由，以下事务内的所有操作，都走一个路由
            dbRouter.doRouter(userId);
            transactionTemplate.execute(status -> {
                try {
                    //更新总账户
                    int updatedAccount = raffleActivityAccountDao.raffleOrderConsume(
                            RaffleActivityAccount.builder()
                                    .userId(userId)
                                    .activityId(activityId)
                                    .build());
                    if (updatedAccount != 1) {
                        //更新总额度失败
                        status.setRollbackOnly();
                        log.warn("保存抽奖活动参与聚合对象 更新总账户额度失败 异常信息:userId: {} activityId: {}", userId, activityId);
                        throw new AppException(Constants.DataBaseExceptionInfo.RAFFLE_ACTIVITY_ACCOUNT_UPDATE_FAILED);
                    }
                    //更新 或 插入 月额度账户
                    if (createPartakeOrderAggregate.isExistAccountMonth()) {
                        //更新
                        int updateAccountMonth = raffleActivityAccountMonthMapper.raffleOrderConsume(
                                RaffleActivityAccountMonth.builder()
                                        .userId(userId)
                                        .activityId(activityId)
                                        .month(raffleActivityAccountMonthEntity.getMonth())
                                        .build());
                        if (updateAccountMonth != 1) {
                            //更新月额度失败
                            status.setRollbackOnly();
                            log.warn("保存抽奖活动参与聚合对象 更新月账户额度失败 异常信息:userId: {} activityId: {} month: {}", userId, activityId, raffleActivityAccountMonthEntity.getMonth());
                            throw new AppException(Constants.DataBaseExceptionInfo.RAFFLE_ACTIVITY_ACCOUNT_MONTH_UPDATE_FAILED);
                        }
                    } else {
                        //插入
                        raffleActivityAccountMonthMapper.save(RaffleActivityAccountMonth.builder()
                                .userId(raffleActivityAccountMonthEntity.getUserId())
                                .activityId(raffleActivityAccountMonthEntity.getActivityId())
                                .month(raffleActivityAccountMonthEntity.getMonth())
                                .monthCount(raffleActivityAccountMonthEntity.getMonthCount())
                                .monthCountSurplus(raffleActivityAccountMonthEntity.getMonthCountSurplus() - 1)
                                .build());
                        //更新总账表中月镜像额度
                        raffleActivityAccountDao.updateAccountMonth(RaffleActivityAccount.builder()
                                .userId(userId)
                                .activityId(activityId)
                                .monthCountSurplus(raffleActivityAccountMonthEntity.getMonthCountSurplus())
                                .build());
                    }
                    //创建 或 更新 日账户
                    if (createPartakeOrderAggregate.isExistAccountDay()) {
                        //更新
                        int updateAccountDay = raffleActivityAccountDayMapper.raffleOrderConsume(RaffleActivityAccountDay.builder()
                                .userId(userId)
                                .activityId(activityId)
                                .day(raffleActivityAccountDayEntity.getDay())
                                .build());
                        if (updateAccountDay != 1) {
                            //日账户更新失败
                            status.setRollbackOnly();
                            log.warn("保存抽奖活动参与聚合对象 更新日账户额度失败 异常信息:userId: {} activityId: {} day: {}", userId, activityId, raffleActivityAccountDayEntity.getDay());
                            throw new AppException(Constants.DataBaseExceptionInfo.RAFFLE_ACTIVITY_ACCOUNT_DAY_UPDATE_FAILED);
                        }
                    } else {
                        //插入
                        raffleActivityAccountDayMapper.save(RaffleActivityAccountDay.builder()
                                .userId(raffleActivityAccountDayEntity.getUserId())
                                .activityId(raffleActivityAccountDayEntity.getActivityId())
                                .day(raffleActivityAccountDayEntity.getDay())
                                .dayCount(raffleActivityAccountDayEntity.getDayCount())
                                .dayCountSurplus(raffleActivityAccountDayEntity.getDayCountSurplus() - 1)
                                .build());
                        //新创建日账户，则更新总账表中日镜像额度
                        raffleActivityAccountDao.updateAccountDay(RaffleActivityAccount.builder()
                                .userId(userId)
                                .activityId(activityId)
                                .dayCountSurplus(raffleActivityAccountDayEntity.getDayCountSurplus())
                                .build());
                    }
                    //保存抽奖单
                    userRaffleOrderMapper.save(UserRaffleOrder.builder()
                            .userId(userRaffleOrderEntity.getUserId())
                            .activityId(userRaffleOrderEntity.getActivityId())
                            .activityName(userRaffleOrderEntity.getActivityName())
                            .strategyId(userRaffleOrderEntity.getStrategyId())
                            .orderId(userRaffleOrderEntity.getOrderId())
                            .orderTime(userRaffleOrderEntity.getOrderTime())
                            .orderState(userRaffleOrderEntity.getOrderState().getCode())
                            .build());
                    return 1;
                } catch (DuplicateKeyException e) {
                    //保存失败
                    status.setRollbackOnly();
                    log.error("写入创建参与活动记录，唯一索引冲突 userId: {} activityId: {}", userId, activityId, e);
                    throw new AppException(Constants.ExceptionInfo.DUPLICATE_KEY);
                }
            });
        } finally {
            dbRouter.clear();
        }
    }

    @Override
    public List<ActivitySkuEntity> queryActivitySkuByActivityId(Long activityId) {
        List<RaffleActivitySku> raffleActivitySkus = raffleActivitySkuDao.queryActivitySkuListByActivityId(activityId);
        ArrayList<ActivitySkuEntity> activitySkuEntities = new ArrayList<>(raffleActivitySkus.size());
        for (RaffleActivitySku raffleActivitySku : raffleActivitySkus) {
            ActivitySkuEntity activitySkuEntity = ActivitySkuEntity.builder()
                        .sku(raffleActivitySku.getSku())
                        .activityId(raffleActivitySku.getActivityId())
                        .activityCountId(raffleActivitySku.getActivityCountId())
                        .stockCount(raffleActivitySku.getStockCount())
                        .stockCountSurplus(raffleActivitySku.getStockCountSurplus())
                        .build();
            activitySkuEntities.add(activitySkuEntity);
        }
        return activitySkuEntities;
    }

    @Override
    public Integer getUserRaffleCountToday(Long activityId, String userId) {
        RaffleActivityAccountDay raffleActivityAccountDay = raffleActivityAccountDayMapper.queryRaffleActivityAccountDay(RaffleActivityAccountDay.builder()
                .activityId(activityId)
                .userId(userId)
                .day(RaffleActivityAccountDay.getCurrentDay())
                .build());
        return raffleActivityAccountDay == null ? 0 : raffleActivityAccountDay.getDayCount() - raffleActivityAccountDay.getDayCountSurplus();
    }

    @Override
    public boolean SkuStockIsZero(String cacheKey) {
        return redisService.isExists(cacheKey);
    }

    @Override
    public RaffleActivityAccountEntity getUserAccountQuota(Long activityId, String userId) {
        //总额度账户
        RaffleActivityAccount raffleActivityAccount = raffleActivityAccountDao.getUserAccountQuota(RaffleActivityAccount.builder()
                .activityId(activityId)
                .userId(userId)
                .build());
        if (raffleActivityAccount == null){
            //总额度账户不存在
            return RaffleActivityAccountEntity.builder()
                    .userId(userId)
                    .activityId(activityId)
                    .totalCount(0)
                    .totalCountSurplus(0)
                    .dayCount(0)
                    .dayCountSurplus(0)
                    .monthCount(0)
                    .monthCountSurplus(0)
                    .build();
        }
        RaffleActivityAccountEntity raffleActivityAccountEntity = RaffleActivityAccountEntity.builder()
                .userId(raffleActivityAccount.getUserId())
                .activityId(raffleActivityAccount.getActivityId())
                .totalCount(raffleActivityAccount.getTotalCount())
                .totalCountSurplus(raffleActivityAccount.getTotalCountSurplus())
                .dayCount(raffleActivityAccount.getDayCount())
                .dayCountSurplus(raffleActivityAccount.getDayCountSurplus())
                .monthCount(raffleActivityAccount.getMonthCount())
                .monthCountSurplus(raffleActivityAccount.getMonthCountSurplus())
                .build();
        //月额度账户
        RaffleActivityAccountMonth raffleActivityAccountMonth = raffleActivityAccountMonthMapper.getUserAccountQuota(RaffleActivityAccountMonth.builder()
                .activityId(activityId)
                .userId(userId)
                .month(RaffleActivityAccountMonth.getCurrentMonth())
                .build());
        if (raffleActivityAccountMonth != null){
            raffleActivityAccountEntity.setMonthCount(raffleActivityAccountMonth.getMonthCount());
            raffleActivityAccountEntity.setMonthCountSurplus(raffleActivityAccountMonth.getMonthCountSurplus());
        }

        //日额度账户
        RaffleActivityAccountDay raffleActivityAccountDay = raffleActivityAccountDayMapper.getUserAccountQuota(RaffleActivityAccountDay.builder()
                .activityId(activityId)
                .userId(userId)
                .day(RaffleActivityAccountDay.getCurrentDay())
                .build());
        if (raffleActivityAccountDay != null){
            raffleActivityAccountEntity.setMonthCount(raffleActivityAccountDay.getDayCount());
            raffleActivityAccountEntity.setMonthCountSurplus(raffleActivityAccountDay.getDayCountSurplus());
        }
        return raffleActivityAccountEntity;
    }

    @Override
    public Integer getUserRaffleCount(Long activityId, String userId) {
        RaffleActivityAccount raffleActivityAccount = raffleActivityAccountDao.getUserAccountQuota(RaffleActivityAccount.builder()
                .activityId(activityId)
                .userId(userId)
                .build());
        return raffleActivityAccount.getTotalCount() - raffleActivityAccount.getTotalCountSurplus();
    }

}
