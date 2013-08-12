package com.alibaba.otter.shared.common.model.config.channel;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.alibaba.otter.shared.common.model.config.ConfigException;
import com.alibaba.otter.shared.common.utils.OtterToStringStyle;

/**
 * channel相关的参数对象
 * 
 * @author jianghang 2011-9-6 下午12:35:05
 */
public class ChannelParameter implements Serializable {

    private static final long serialVersionUID            = 298053781205276645L;
    private Long              channelId;
    private boolean           enableMainstem              = true;                    // 是否启用mainstem追取数据
    private boolean           enableDetect                = false;                   // 是否启用冲突检测算法
    private boolean           enableRemedy                = false;                   // 是否启用冲突补救算法
    private RemedyAlgorithm   remedyAlgorithm             = RemedyAlgorithm.LOOPBACK; // 冲突补救算法
    private int               remedyCheckTime             = 60 * 1000;               // 开启remedy后，补救程序的check时间
    private int               remedyDelayThresoldForMedia = 60;                      // 低于60秒钟的同步延迟，回环补救不反查
    private DetectAlgorithm   detectAlgorithm             = DetectAlgorithm.COVERAGE; // 冲突检测算法
    private SyncMode          syncMode                    = SyncMode.FIELD;          // 同步模式：字段/整条记录
    private SyncConsistency   syncConsistency             = SyncConsistency.BASE;    // 同步一致性要求

    public boolean isEnableDetect() {
        return enableDetect;
    }

    public void setEnableDetect(boolean enableDetect) {
        this.enableDetect = enableDetect;
    }

    public boolean isEnableRemedy() {
        return enableRemedy;
    }

    public void setEnableRemedy(boolean enableRemedy) {
        this.enableRemedy = enableRemedy;
    }

    public int getRemedyCheckTime() {
        return remedyCheckTime;
    }

    public void setRemedyCheckTime(int remedyCheckTime) {
        this.remedyCheckTime = remedyCheckTime;
    }

    public int getRemedyDelayThresoldForMedia() {
        return remedyDelayThresoldForMedia <= 0 ? 10 : remedyDelayThresoldForMedia;
    }

    public void setRemedyDelayThresoldForMedia(int remedyDelayThresoldForMedia) {
        this.remedyDelayThresoldForMedia = remedyDelayThresoldForMedia;
    }

    public static enum DetectAlgorithm {
        /** 优先覆盖 */
        COVERAGE,

        /** 普通模式-时间优先 */
        ORDINARY;

        public boolean isCoverage() {
            return this.equals(DetectAlgorithm.COVERAGE);
        }

        public boolean isOrdinary() {
            return this.equals(DetectAlgorithm.ORDINARY);
        }

    }

    public static enum RemedyAlgorithm {

        /** 交集覆盖 */
        INTERSECTION,

        /** 普通模式-全部覆盖 */
        LOOPBACK;

        public boolean isIntersection() {
            return this.equals(RemedyAlgorithm.INTERSECTION);
        }

        public boolean isLoopback() {
            return this.equals(RemedyAlgorithm.LOOPBACK);
        }

    }

    public static enum SyncMode {
        /** 行记录 */
        ROW("R"),
        /** 字段记录 */
        FIELD("F");

        private String value;

        SyncMode(String value){
            this.value = value;
        }

        public static SyncMode valuesOf(String value) {
            SyncMode[] modes = values();
            for (SyncMode mode : modes) {
                if (mode.value.equalsIgnoreCase(value)) {
                    return mode;
                }
            }

            throw new ConfigException("unknow SyncMode : " + value);
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public boolean isRow() {
            return this.equals(SyncMode.ROW);
        }

        public boolean isField() {
            return this.equals(SyncMode.FIELD);
        }
    }

    public static enum SyncConsistency {
        /** 基于当前介质最新数据 */
        MEDIA("M"),
        /** 基于当前的store记录的数据 */
        STORE("S"),
        /** 基于当前的变更value，最终一致性 */
        BASE("B");

        private String value;

        SyncConsistency(String value){
            this.value = value;
        }

        public static SyncConsistency valuesOf(String value) {
            SyncConsistency[] modes = values();
            for (SyncConsistency mode : modes) {
                if (mode.value.equalsIgnoreCase(value)) {
                    return mode;
                }
            }

            throw new ConfigException("unknow SyncConsistency : " + value);
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public boolean isMedia() {
            return this.equals(SyncConsistency.MEDIA);
        }

        public boolean isStore() {
            return this.equals(SyncConsistency.STORE);
        }

        public boolean isBase() {
            return this.equals(SyncConsistency.BASE);
        }
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public DetectAlgorithm getDetectAlgorithm() {
        return detectAlgorithm;
    }

    public void setDetectAlgorithm(DetectAlgorithm detectAlgorithm) {
        this.detectAlgorithm = detectAlgorithm;
    }

    public SyncMode getSyncMode() {
        return syncMode;
    }

    public void setSyncMode(SyncMode syncMode) {
        this.syncMode = syncMode;
    }

    public SyncConsistency getSyncConsistency() {
        return syncConsistency;
    }

    public void setSyncConsistency(SyncConsistency syncConsistency) {
        this.syncConsistency = syncConsistency;
    }

    public boolean isEnableMainstem() {
        return enableMainstem;
    }

    public void setEnableMainstem(boolean enableMainstem) {
        this.enableMainstem = enableMainstem;
    }

    public RemedyAlgorithm getRemedyAlgorithm() {
        return remedyAlgorithm == null ? RemedyAlgorithm.LOOPBACK : remedyAlgorithm;
    }

    public void setRemedyAlgorithm(RemedyAlgorithm remedyAlgorithm) {
        this.remedyAlgorithm = remedyAlgorithm;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, OtterToStringStyle.DEFAULT_STYLE);
    }

}
