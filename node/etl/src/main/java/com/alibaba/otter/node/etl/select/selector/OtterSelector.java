package com.alibaba.otter.node.etl.select.selector;

import java.util.List;

/**
 * otter同步数据获取
 * 
 * @author jianghang 2012-7-31 下午02:30:33
 */
public interface OtterSelector<T> {

    /**
     * 启动
     */
    public void start();

    /**
     * 是否启动
     */
    public boolean isStart();

    /**
     * 关闭
     */
    public void stop();

    /**
     * 获取一批待处理的数据
     */
    public Message<T> selector() throws InterruptedException;

    /**
     * 返回未被ack的数据
     */
    public List<Long> unAckBatchs();

    /**
     * 反馈一批数据处理失败，需要下次重新被处理
     */
    public void rollback(Long batchId);

    /**
     * 反馈所有的batch数据需要被重新处理
     */
    public void rollback();

    /**
     * 反馈一批数据处理完成
     */
    public void ack(Long batchId);

    /**
     * 返回最后一次entry数据的时间戳
     */
    public Long lastEntryTime();
}
