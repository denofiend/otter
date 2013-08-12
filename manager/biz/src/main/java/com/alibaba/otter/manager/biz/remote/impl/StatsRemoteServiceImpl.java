package com.alibaba.otter.manager.biz.remote.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.alibaba.otter.manager.biz.remote.StatsRemoteService;
import com.alibaba.otter.manager.biz.statistics.delay.DelayStatService;
import com.alibaba.otter.manager.biz.statistics.table.TableStatService;
import com.alibaba.otter.manager.biz.statistics.throughput.ThroughputStatService;
import com.alibaba.otter.shared.common.model.statistics.delay.DelayCount;
import com.alibaba.otter.shared.common.model.statistics.delay.DelayStat;
import com.alibaba.otter.shared.common.model.statistics.table.TableStat;
import com.alibaba.otter.shared.common.model.statistics.throughput.ThroughputStat;
import com.alibaba.otter.shared.common.model.statistics.throughput.ThroughputType;
import com.alibaba.otter.shared.common.utils.thread.NamedThreadFactory;
import com.alibaba.otter.shared.communication.core.CommunicationRegistry;
import com.alibaba.otter.shared.communication.model.statistics.DelayCountEvent;
import com.alibaba.otter.shared.communication.model.statistics.StatisticsEventType;
import com.alibaba.otter.shared.communication.model.statistics.TableStatEvent;
import com.alibaba.otter.shared.communication.model.statistics.ThroughputStatEvent;
import com.google.common.base.Function;
import com.google.common.collect.MapMaker;

/**
 * 统计模块远程接口
 * 
 * @author jianghang 2011-10-21 下午03:04:40
 * @version 4.0.0
 */
public class StatsRemoteServiceImpl implements StatsRemoteService {

    private static final Logger                            logger       = LoggerFactory.getLogger(StatsRemoteServiceImpl.class);
    private static final int                               DEFAULT_POOL = 10;
    private DelayStatService                               delayStatService;
    private TableStatService                               tableStatService;
    private ThroughputStatService                          throughputStatService;
    private Long                                           statUnit     = 60 * 1000L;                                           //统计周期，默认60秒
    private ScheduledThreadPoolExecutor                    scheduler;
    private Map<Long, DelayStat>                           delayStats;
    private Map<Long, Map<ThroughputType, ThroughputStat>> throughputStats;

    public StatsRemoteServiceImpl(){
        // 注册一下事件处理
        CommunicationRegistry.regist(StatisticsEventType.delayCount, this);
        CommunicationRegistry.regist(StatisticsEventType.tableStat, this);
        CommunicationRegistry.regist(StatisticsEventType.throughputStat, this);

        delayStats = new MapMaker().makeMap();
        throughputStats = new MapMaker().makeComputingMap(new Function<Long, Map<ThroughputType, ThroughputStat>>() {

            public Map<ThroughputType, ThroughputStat> apply(Long pipelineId) {
                return new HashMap<ThroughputType, ThroughputStat>();
            }
        });

        scheduler = new ScheduledThreadPoolExecutor(DEFAULT_POOL, new NamedThreadFactory("Otter-Statistics-Server"),
                                                    new ThreadPoolExecutor.CallerRunsPolicy());
        if (statUnit > 0) {
            scheduler.scheduleAtFixedRate(new Runnable() {

                public void run() {
                    try {
                        flushDelayStat();
                    } catch (Exception e) {
                        logger.error("flush delay stat failed!", e);
                    }
                }
            }, statUnit, statUnit, TimeUnit.MILLISECONDS);

            scheduler.scheduleAtFixedRate(new Runnable() {

                public void run() {
                    try {
                        flushThroughputStat();
                    } catch (Exception e) {
                        logger.error("flush Throughput stat failed!", e);
                    }
                }
            }, statUnit, statUnit, TimeUnit.MILLISECONDS);
        }
    }

    public void onDelayCount(DelayCountEvent event) {
        Assert.notNull(event);
        Assert.notNull(event.getCount());

        // 更新delay queue的计数器
        DelayCount count = event.getCount();
        // 构造一次delay stat快照
        DelayStat stat = new DelayStat();
        stat.setPipelineId(count.getPipelineId());
        stat.setDelayNumber(0L); // 不再记录堆积量
        stat.setDelayTime(count.getTime()); // 只记录延迟时间

        if (statUnit <= 0) {
            delayStatService.createDelayStat(stat);
        } else {
            synchronized (delayStats) {
                delayStats.put(count.getPipelineId(), stat);
            }
        }
    }

    public void onThroughputStat(ThroughputStatEvent event) {
        Assert.notNull(event);
        Assert.notNull(event.getStats());
        if (statUnit <= 0) {
            for (ThroughputStat stat : event.getStats()) {
                throughputStatService.createOrUpdateThroughput(stat);
            }
        } else {
            synchronized (throughputStats) {
                for (ThroughputStat stat : event.getStats()) {
                    Map<ThroughputType, ThroughputStat> data = throughputStats.get(stat.getPipelineId());
                    ThroughputStat old = data.get(stat.getType());
                    if (old != null) {
                        //执行合并
                        old.setNumber(stat.getNumber() + old.getNumber());
                        old.setSize(stat.getSize() + old.getSize());
                        if (stat.getEndTime().after(old.getEndTime())) {
                            old.setEndTime(stat.getEndTime());
                        }

                        if (stat.getStartTime().before(stat.getStartTime())) {
                            stat.setStartTime(stat.getStartTime());
                        }
                    } else {
                        data.put(stat.getType(), stat);
                    }
                }
            }
        }
    }

    public void onTableStat(TableStatEvent event) {
        Assert.notNull(event);
        Assert.notNull(event.getStats());
        for (TableStat stat : event.getStats()) {
            tableStatService.updateTableStat(stat);
        }
    }

    private void flushDelayStat() {
        synchronized (delayStats) {
            // 需要做同步，避免delay数据丢失
            Collection<DelayStat> stats = delayStats.values();
            delayStats.clear();
            for (DelayStat stat : stats) {
                delayStatService.createDelayStat(stat);
            }
        }
    }

    private void flushThroughputStat() {
        synchronized (throughputStats) {
            Collection<Map<ThroughputType, ThroughputStat>> stats = throughputStats.values();
            throughputStats.clear();
            for (Map<ThroughputType, ThroughputStat> stat : stats) {
                for (ThroughputStat data : stat.values()) {
                    throughputStatService.createOrUpdateThroughput(data);
                }
            }
        }
    }

    // ===================== setter / getter =====================

    public void setDelayStatService(DelayStatService delayStatService) {
        this.delayStatService = delayStatService;
    }

    public void setTableStatService(TableStatService tableStatService) {
        this.tableStatService = tableStatService;
    }

    public void setThroughputStatService(ThroughputStatService throughputStatService) {
        this.throughputStatService = throughputStatService;
    }

}
