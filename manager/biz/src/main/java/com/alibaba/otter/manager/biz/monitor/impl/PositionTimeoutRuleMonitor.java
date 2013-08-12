package com.alibaba.otter.manager.biz.monitor.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import com.alibaba.otter.manager.biz.config.pipeline.PipelineService;
import com.alibaba.otter.manager.biz.monitor.MonitorRuleExplorerRegisty;
import com.alibaba.otter.shared.arbitrate.ArbitrateManageService;
import com.alibaba.otter.shared.arbitrate.ArbitrateViewService;
import com.alibaba.otter.shared.arbitrate.model.PositionEventData;
import com.alibaba.otter.shared.common.model.config.alarm.AlarmRule;
import com.alibaba.otter.shared.common.model.config.alarm.MonitorName;
import com.alibaba.otter.shared.common.model.config.channel.ChannelStatus;
import com.alibaba.otter.shared.common.model.config.pipeline.Pipeline;

/**
 * 位点超时监控
 * 
 * @author jianghang 2012-12-12 上午10:33:12
 * @version 4.1.3
 */
public class PositionTimeoutRuleMonitor extends AbstractRuleMonitor {

    private PipelineService        pipelineService;
    private ArbitrateViewService   arbitrateViewService;
    private ArbitrateManageService arbitrateManageService;
    private static final String    TIME_OUT_MESSAGE = "pid:%s position %s seconds no update";

    PositionTimeoutRuleMonitor(){
        MonitorRuleExplorerRegisty.register(MonitorName.POSITIONTIMEOUT, this);
    }

    @Override
    public void explore(List<AlarmRule> rules) {
        if (CollectionUtils.isEmpty(rules)) {
            return;
        }
        Long pipelineId = rules.get(0).getPipelineId();
        Pipeline pipeline = pipelineService.findById(pipelineId);
        PositionEventData data = arbitrateViewService.getCanalCursor(pipeline.getParameters().getDestinationName(),
                                                                     pipeline.getParameters().getMainstemClientId());
        // 如果处于stop状态，则忽略报警
        ChannelStatus status = arbitrateManageService.channelEvent().status(pipeline.getChannelId());
        if (status == null || status.isStop()) {
            return;
        }

        long latestSyncTime = 0L;
        if (data != null && data.getModifiedTime() != null) {
            Date modifiedDate = data.getModifiedTime();
            latestSyncTime = modifiedDate.getTime();
        }

        long now = System.currentTimeMillis();
        long elapsed = now - latestSyncTime;
        boolean flag = false;
        for (AlarmRule rule : rules) {
            flag |= checkTimeout(rule, elapsed);
        }

        if (flag) {
            logRecordAlarm(pipelineId, MonitorName.POSITIONTIMEOUT, String.format(TIME_OUT_MESSAGE, pipelineId,
                                                                                  (elapsed / 1000)));
        }
    }

    private boolean checkTimeout(AlarmRule rule, long elapsed) {
        if (!inPeriod(rule)) {
            return false;
        }

        String matchValue = rule.getMatchValue();
        matchValue = StringUtils.substringBeforeLast(matchValue, "@");
        Long maxSpentTime = Long.parseLong(StringUtils.trim(matchValue));
        // sinceLastSync是毫秒，而 maxSpentTime 是秒
        if (elapsed >= (maxSpentTime * 1000)) {
            sendAlarm(rule, String.format(TIME_OUT_MESSAGE, rule.getPipelineId(), (elapsed / 1000)));
            return true;
        }
        return false;
    }

    public void setPipelineService(PipelineService pipelineService) {
        this.pipelineService = pipelineService;
    }

    public void setArbitrateViewService(ArbitrateViewService arbitrateViewService) {
        this.arbitrateViewService = arbitrateViewService;
    }

    public void setArbitrateManageService(ArbitrateManageService arbitrateManageService) {
        this.arbitrateManageService = arbitrateManageService;
    }

}