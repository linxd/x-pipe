package com.ctrip.xpipe.redis.console.model.consoleportal;

import java.util.List;

public class RouteInfoModel {

    private long id;

    private String orgName;

    private List<String> srcProxies;

    private List<String> optionalProxies;

    private List<String> dstProxies;

    private String srcDcName = "";

    private String dstDcName = "";

    private String tag = "";

    private boolean active;

    private String description;

    public RouteInfoModel() {

    }

    public RouteInfoModel(long id, String orgName, List<String> srcProxies, List<String> optionalProxies,
                          List<String> dstProxies, String srcDcName, String dstDcName, String tag, boolean active, String description) {
        this.id = id;
        this.orgName = orgName;
        this.srcProxies = srcProxies;
        this.optionalProxies = optionalProxies;
        this.dstProxies = dstProxies;
        this.srcDcName = srcDcName;
        this.dstDcName = dstDcName;
        this.tag = tag;
        this.active = active;
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public RouteInfoModel setId(long id) {
        this.id = id;
        return this;
    }

    public String getOrgName() {
        return orgName;
    }

    public RouteInfoModel setOrgName(String orgName) {
        this.orgName = orgName;
        return this;
    }

    public List<String> getSrcProxies() {
        return srcProxies;
    }

    public RouteInfoModel setSrcProxies(List<String> srcProxies) {
        this.srcProxies = srcProxies;
        return this;
    }

    public List<String> getOptionalProxies() {
        return optionalProxies;
    }

    public RouteInfoModel setOptionalProxies(List<String> optionalProxies) {
        this.optionalProxies = optionalProxies;
        return this;
    }

    public List<String> getDstProxies() {
        return dstProxies;
    }

    public RouteInfoModel setDstProxies(List<String> dstProxies) {
        this.dstProxies = dstProxies;
        return this;
    }

    public String getSrcDcName() {
        return srcDcName;
    }

    public RouteInfoModel setSrcDcName(String srcDcName) {
        this.srcDcName = srcDcName;
        return this;
    }

    public String getDstDcName() {
        return dstDcName;
    }

    public RouteInfoModel setDstDcName(String dstDcName) {
        this.dstDcName = dstDcName;
        return this;
    }

    public String getTag() {
        return tag;
    }

    public RouteInfoModel setTag(String tag) {
        this.tag = tag;
        return this;
    }

    public boolean isActive() {
        return active;
    }

    public RouteInfoModel setActive(boolean active) {
        this.active = active;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public RouteInfoModel setDescription(String description) {
        this.description = description;
        return this;
    }

    @Override
    public String toString() {
        return "RouteInfoModel{" +
                "id=" + id +
                ", orgName='" + orgName + '\'' +
                ", srcProxies=" + srcProxies +
                ", optionalProxies=" + optionalProxies +
                ", dstProxies=" + dstProxies +
                ", srcDcName='" + srcDcName + '\'' +
                ", dstDcName='" + dstDcName + '\'' +
                ", tag='" + tag + '\'' +
                ", active=" + active +
                ", description='" + description + '\'' +
                '}';
    }
}
