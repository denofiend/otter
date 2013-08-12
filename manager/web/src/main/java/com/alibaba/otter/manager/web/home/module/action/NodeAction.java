package com.alibaba.otter.manager.web.home.module.action;

import java.util.Arrays;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;

import com.alibaba.citrus.service.form.CustomErrors;
import com.alibaba.citrus.service.form.Group;
import com.alibaba.citrus.turbine.Navigator;
import com.alibaba.citrus.turbine.dataresolver.FormField;
import com.alibaba.citrus.turbine.dataresolver.FormGroup;
import com.alibaba.citrus.turbine.dataresolver.Param;
import com.alibaba.citrus.webx.WebxException;
import com.alibaba.otter.manager.biz.common.exceptions.RepeatConfigureException;
import com.alibaba.otter.manager.biz.config.node.NodeService;
import com.alibaba.otter.manager.biz.config.pipeline.PipelineService;
import com.alibaba.otter.manager.web.common.WebConstant;
import com.alibaba.otter.shared.common.model.config.node.Node;
import com.alibaba.otter.shared.common.model.config.node.NodeParameter;

public class NodeAction extends AbstractAction {

    @Resource(name = "nodeService")
    private NodeService     nodeService;

    @Resource(name = "pipelineService")
    private PipelineService pipelineService;

    public void doAdd(@FormGroup("nodeInfo") Group nodeInfo, @FormGroup("nodeParameterInfo") Group nodeParameterInfo,
                      @FormField(name = "formNodeError", group = "nodeInfo") CustomErrors err, Navigator nav)
                                                                                                             throws Exception {
        Node node = new Node();
        NodeParameter parameter = new NodeParameter();
        nodeInfo.setProperties(node);
        nodeParameterInfo.setProperties(parameter);
        String storeClustersString = nodeParameterInfo.getField("storeClusters").getStringValue();
        String zkClustersString = nodeParameterInfo.getField("zkClusters").getStringValue();

        String[] storeClusters = StringUtils.split(storeClustersString, ";");
        String[] zkClusters = StringUtils.split(zkClustersString, ";");

        parameter.setStoreClusters(Arrays.asList(storeClusters));
        parameter.setZkClusters(Arrays.asList(zkClusters));

        node.setParameters(parameter);
        try {
            nodeService.create(node);
        } catch (RepeatConfigureException rce) {
            err.setMessage("invalidNode");
            return;
        }
        nav.redirectTo(WebConstant.NODE_LIST_LINK);
    }

    /**
     * 修改Node
     */
    public void doEdit(@FormGroup("nodeInfo") Group nodeInfo, @FormGroup("nodeParameterInfo") Group nodeParameterInfo,
                       @Param("pageIndex") int pageIndex, @Param("searchKey") String searchKey,
                       @FormField(name = "formNodeError", group = "nodeInfo") CustomErrors err, Navigator nav)
                                                                                                              throws Exception {
        Node node = new Node();
        NodeParameter parameter = new NodeParameter();
        nodeInfo.setProperties(node);
        nodeParameterInfo.setProperties(parameter);
        String storeClustersString = nodeParameterInfo.getField("storeClusters").getStringValue();
        String zkClustersString = nodeParameterInfo.getField("zkClusters").getStringValue();

        String[] storeClusters = StringUtils.split(storeClustersString, ";");
        String[] zkClusters = StringUtils.split(zkClustersString, ";");

        parameter.setStoreClusters(Arrays.asList(storeClusters));
        parameter.setZkClusters(Arrays.asList(zkClusters));

        node.setParameters(parameter);
        try {
            nodeService.modify(node);
        } catch (RepeatConfigureException rce) {
            err.setMessage("invalidNode");
            return;
        }

        nav.redirectToLocation("nodeList.htm?pageIndex=" + pageIndex + "&searchKey=" + urlEncode(searchKey));
    }

    /**
     * 删除node
     * 
     * @param nodeId
     * @throws WebxException
     */
    public void doDelete(@Param("nodeId") Long nodeId, @Param("pageIndex") int pageIndex,
                         @Param("searchKey") String searchKey, Navigator nav) throws WebxException {

        if (pipelineService.hasRelation(nodeId) || nodeService.findById(nodeId).getStatus().isStart()) {
            nav.redirectTo(WebConstant.ERROR_FORBIDDEN_Link);
            return;
        } else {
            nodeService.remove(nodeId);
        }

        nav.redirectToLocation("nodeList.htm?pageIndex=" + pageIndex + "&searchKey=" + urlEncode(searchKey));
    }
}
