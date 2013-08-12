package com.alibaba.otter.manager.biz.config.channel.dal.ibatis;

import java.util.List;
import java.util.Map;

import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.alibaba.otter.shared.common.utils.Assert;
import com.alibaba.otter.manager.biz.config.channel.dal.ChannelDAO;
import com.alibaba.otter.manager.biz.config.channel.dal.dataobject.ChannelDO;

/**
 * Channel的DAO层，ibatis的实现，主要是CRUD操作。
 * 
 * @author simon
 */
public class IbatisChannelDAO extends SqlMapClientDaoSupport implements ChannelDAO {

    public ChannelDO insert(ChannelDO entityObj) {
        Assert.assertNotNull(entityObj);
        getSqlMapClientTemplate().insert("insertChannel", entityObj);
        return entityObj;
    }

    public void delete(Long identity) {
        Assert.assertNotNull(identity);
        getSqlMapClientTemplate().delete("deleteChannelById", identity);
    }

    public void update(ChannelDO entityObj) {
        Assert.assertNotNull(entityObj);
        getSqlMapClientTemplate().update("updateChannel", entityObj);
    }

    public boolean checkUnique(ChannelDO entityObj) {
        Assert.assertNotNull(entityObj);
        int count = (Integer) getSqlMapClientTemplate().queryForObject("checkChannelUnique", entityObj);
        return count == 0 ? true : false;
    }

    public List<ChannelDO> listAll() {
        List<ChannelDO> channels = getSqlMapClientTemplate().queryForList("listChannels");
        return channels;
    }

    public List<ChannelDO> listChannelPks() {
        List<ChannelDO> channels = getSqlMapClientTemplate().queryForList("listChannelPks");
        return channels;
    }

    public List<ChannelDO> listByCondition(Map condition) {

        List<ChannelDO> channelDos = getSqlMapClientTemplate().queryForList("listChannels", condition);
        return channelDos;
    }

    public int getCount() {
        Integer count = (Integer) getSqlMapClientTemplate().queryForObject("getChannelCount");
        return count.intValue();
    }

    public int getCount(Map condition) {
        Integer count = (Integer) getSqlMapClientTemplate().queryForObject("getChannelCount", condition);
        return count.intValue();
    }

    public List<ChannelDO> listByMultiId(Long... identities) {
        List<ChannelDO> channelDos = getSqlMapClientTemplate().queryForList("listChannelByIds", identities);
        return channelDos;
    }

    public ChannelDO findById(Long identity) {
        Assert.assertNotNull(identity);
        return (ChannelDO) getSqlMapClientTemplate().queryForObject("findChannelById", identity);
    }

}
