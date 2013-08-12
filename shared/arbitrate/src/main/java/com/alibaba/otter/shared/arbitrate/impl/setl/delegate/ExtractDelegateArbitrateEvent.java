package com.alibaba.otter.shared.arbitrate.impl.setl.delegate;

import java.util.Map;

import com.alibaba.otter.shared.arbitrate.impl.setl.ExtractArbitrateEvent;
import com.alibaba.otter.shared.arbitrate.model.EtlEventData;
import com.alibaba.otter.shared.common.model.config.pipeline.PipelineParameter.ArbitrateMode;

/**
 * extract delegate实现
 * 
 * @author jianghang 2012-9-28 上午10:36:38
 * @version 4.1.0
 */
public class ExtractDelegateArbitrateEvent extends AbstractDelegateArbitrateEvent implements ExtractArbitrateEvent {

    private Map<ArbitrateMode, ExtractArbitrateEvent> delegate;

    public EtlEventData await(Long pipelineId) throws InterruptedException {
        return delegate.get(chooseMode(pipelineId)).await(pipelineId);
    }

    public void single(EtlEventData data) {
        delegate.get(chooseMode(data.getPipelineId())).single(data);
    }

    public void setDelegate(Map<ArbitrateMode, ExtractArbitrateEvent> delegate) {
        this.delegate = delegate;
    }

}
