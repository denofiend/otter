package com.alibaba.otter.shared.common.model.config.pipeline;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.alibaba.otter.shared.common.model.config.data.DataMediaPair;
import com.alibaba.otter.shared.common.model.config.node.Node;
import com.alibaba.otter.shared.common.utils.OtterToStringStyle;

/**
 * 同步任务数据对象
 * 
 * @author jianghang 2011-8-31 下午07:35:38
 */
public class Pipeline implements Serializable {

    private static final long   serialVersionUID = 5055655233043393285L;
    private Long                id;
    private Long                channelId;                                 // 对应关联的channel唯一标示id
    private String              name;
    private String              description;                               // 描述信息
    private List<Node>          selectNodes;
    private List<Node>          extractNodes;
    private List<Node>          loadNodes;
    private List<DataMediaPair> pairs;
    private Date                gmtCreate;
    private Date                gmtModified;
    private PipelineParameter   parameters       = new PipelineParameter();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Node> getSelectNodes() {
        return selectNodes;
    }

    public void setSelectNodes(List<Node> selectNodes) {
        this.selectNodes = selectNodes;
    }

    public List<Node> getExtractNodes() {
        return extractNodes;
    }

    public void setExtractNodes(List<Node> extractNodes) {
        this.extractNodes = extractNodes;
    }

    public List<Node> getLoadNodes() {
        return loadNodes;
    }

    public void setLoadNodes(List<Node> loadNodes) {
        this.loadNodes = loadNodes;
    }

    public List<DataMediaPair> getPairs() {
        return pairs;
    }

    public void setPairs(List<DataMediaPair> pairs) {
        this.pairs = pairs;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    public PipelineParameter getParameters() {
        return parameters;
    }

    public void setParameters(PipelineParameter parameters) {
        this.parameters = parameters;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, OtterToStringStyle.DEFAULT_STYLE);
    }
}
