package com.alibaba.otter.shared.common.model.config.data.db;

import java.util.Properties;

import com.alibaba.otter.shared.common.model.config.data.DataMediaSource;

/**
 * 基于db的source信息
 * 
 * @author jianghang
 */
public class DbMediaSource extends DataMediaSource {

    private static final long serialVersionUID = 2840851954936715456L;
    // private static final String MYSQL_DRIVER = "com.mysql.jdbc.Driver";
    // private static final String ORACLE_DRIVER_PROXY = "com.alibaba.china.jdbc.SimpleDriver";
    // private static final String ORACLE_DRIVER = "oracle.jdbc.driver.OracleDriver";
    private String            url;
    private String            username;
    private String            password;
    private String            driver;
    private Properties        properties;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDriver() {
        // DataMediaType type = getType();
        // if (type != null) {
        // if (type.isMysql()) {
        // return MYSQL_DRIVER;
        // } else if (type.isOracle()) {
        // return ORACLE_DRIVER_PROXY;
        // }
        // }
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

}
