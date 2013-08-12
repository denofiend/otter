package com.alibaba.otter.shared.arbitrate.setl.monitor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import com.alibaba.otter.shared.arbitrate.impl.setl.ArbitrateFactory;
import com.alibaba.otter.shared.arbitrate.impl.setl.monitor.MonitorScheduler;
import com.alibaba.otter.shared.arbitrate.impl.setl.rpc.monitor.ProcessListener;
import com.alibaba.otter.shared.arbitrate.impl.setl.rpc.monitor.ProcessMonitor;
import com.alibaba.otter.shared.arbitrate.setl.BaseStageTest;

public class ProcessMonitorTest extends BaseStageTest {

    private ProcessMonitor monitor;

    @AfterMethod
    public void dispose() {
        MonitorScheduler.unRegister(monitor);
    }

    @Test
    public void testProcess_static() {// 测试静态的process获取
        final List<Long> initProcessIds = new ArrayList<Long>();
        try {
            Long p1 = initProcess();
            Long p2 = initProcess();
            initProcessIds.add(p1);
            initProcessIds.add(p2);
            monitor = ArbitrateFactory.getInstance(pipelineId, ProcessMonitor.class);
            List<Long> processIds = monitor.getCurrentProcessIds();
            want.collection(processIds).isEqualTo(initProcessIds);

            MonitorScheduler.unRegister(monitor);
        } finally {
            for (Long processId : initProcessIds) {
                destoryProcess(processId);
            }

        }
    }

    @Test
    public void testProcess_dynamic() {// 测试动态的process获取
        final List<Long> initProcessIds = new ArrayList<Long>();
        try {
            Long p1 = initProcess();
            Long p2 = initProcess();
            initProcessIds.add(p1);
            initProcessIds.add(p2);

            monitor = ArbitrateFactory.getInstance(pipelineId, ProcessMonitor.class);
            final CountDownLatch count = new CountDownLatch(1);
            monitor.addListener(new ProcessListener() {

                public void processChanged(List<Long> processIds) {
                    want.collection(processIds).isEqualTo(initProcessIds);
                    count.countDown();
                }

            });
            // 产生一些变化
            Long p3 = initProcess();
            initProcessIds.add(p3);
            destoryProcess(p1);
            initProcessIds.remove(p1);

            sleep();
            List<Long> processIds = monitor.getCurrentProcessIds();
            want.collection(processIds).isEqualTo(initProcessIds);

            try {
                count.await();// 等待listener响应
            } catch (InterruptedException e) {
                want.fail();
            }
        } finally {
            for (Long processId : initProcessIds) {
                destoryProcess(processId);
            }
        }
    }
}
