package com.alibaba.otter.manager.web.home.module.screen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.alibaba.citrus.turbine.Context;
import com.alibaba.citrus.turbine.dataresolver.Param;
import com.alibaba.citrus.util.Paginator;
import com.alibaba.otter.manager.biz.config.datamedia.DataMediaService;
import com.alibaba.otter.manager.biz.config.datamediasource.DataMediaSourceService;
import com.alibaba.otter.manager.web.common.model.SeniorDataMediaSource;
import com.alibaba.otter.shared.common.model.config.data.DataMedia;
import com.alibaba.otter.shared.common.model.config.data.DataMediaSource;
import com.alibaba.otter.shared.common.model.config.data.db.DbMediaSource;
import com.alibaba.otter.shared.common.model.config.data.mq.MqMediaSource;

public class DataSourceList {

    @Resource(name = "dataMediaSourceService")
    private DataMediaSourceService dataMediaSourceService;

    @Resource(name = "dataMediaService")
    private DataMediaService       dataMediaService;

    public void execute(@Param("pageIndex") int pageIndex, @Param("searchKey") String searchKey, Context context)
                                                                                                                 throws Exception {
        @SuppressWarnings("unchecked")
        Map<String, Object> condition = new HashMap<String, Object>();
        if ("请输入关键字(目前支持DataSource的ID、名字搜索)".equals(searchKey)) {
            searchKey = "";
        }
        condition.put("searchKey", searchKey);

        int count = dataMediaSourceService.getCount(condition);
        Paginator paginator = new Paginator();
        paginator.setItems(count);
        paginator.setPage(pageIndex);

        condition.put("offset", paginator.getOffset());
        condition.put("length", paginator.getLength());

        List<DataMediaSource> dataMediaSources = dataMediaSourceService.listByCondition(condition);
        List<SeniorDataMediaSource> seniorDataMediaSources = new ArrayList<SeniorDataMediaSource>();
        for (DataMediaSource dataMediaSource : dataMediaSources) {

            SeniorDataMediaSource seniorDataMediaSource = new SeniorDataMediaSource();
            seniorDataMediaSource.setEncode(dataMediaSource.getEncode());
            seniorDataMediaSource.setGmtCreate(dataMediaSource.getGmtCreate());
            seniorDataMediaSource.setGmtModified(dataMediaSource.getGmtModified());
            seniorDataMediaSource.setId(dataMediaSource.getId());
            seniorDataMediaSource.setName(dataMediaSource.getName());
            seniorDataMediaSource.setType(dataMediaSource.getType());
            if (dataMediaSource instanceof DbMediaSource) {
                seniorDataMediaSource.setDriver(((DbMediaSource) dataMediaSource).getDriver());
                seniorDataMediaSource.setUrl(((DbMediaSource) dataMediaSource).getUrl());
                seniorDataMediaSource.setUsername(((DbMediaSource) dataMediaSource).getUsername());
            } else if (dataMediaSource instanceof MqMediaSource) {
                seniorDataMediaSource.setUrl(((MqMediaSource) dataMediaSource).getUrl());
                seniorDataMediaSource.setStorePath(((MqMediaSource) dataMediaSource).getStorePath());
            }
            List<DataMedia> dataMedia = dataMediaService.listByDataMediaSourceId(dataMediaSource.getId());
            seniorDataMediaSource.setDataMedias(dataMedia);
            if (dataMedia.size() < 1) {
                seniorDataMediaSource.setUsed(false);
            } else {
                seniorDataMediaSource.setUsed(true);
            }
            seniorDataMediaSources.add(seniorDataMediaSource);

        }

        context.put("sources", seniorDataMediaSources);
        context.put("paginator", paginator);
        context.put("searchKey", searchKey);
    }
}
