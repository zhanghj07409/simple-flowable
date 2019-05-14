package com.sim.common.vo;

/**
 * 功能说明: <br>
 * 系统版本: 1.0 <br>
 * 开发人员: zhanghj
 * 开发时间: 2017/4/11<br>
 * <br>
 */
public class FlowableTasksVo {

    /**
     * 页码
     */
    private Integer page;
    /**
     * 行数
     */
    private Integer rows;

    /**
     * 流程定义id
     */
    private String processDefinitionKey;
    /**
     * 企业id对应的key
     */
    private String keyName;
    /**
     * 企业id
     */
    private String keyValue;
    /**
     * 登录用户id
     */
    private String userId;

    /**
     * 工作流中操作标志的key
     */
    private String operationKey;

    /**
     * 操作标志的值
     */
    private String operationFlags;

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }

    public String getOperationKey() {
        return operationKey;
    }

    public void setOperationKey(String operationKey) {
        this.operationKey = operationKey;
    }

    public String getOperationFlags() {
        return operationFlags;
    }

    public void setOperationFlags(String operationFlags) {
        this.operationFlags = operationFlags;
    }

    public String getProcessDefinitionKey() {
        return processDefinitionKey;
    }

    public void setProcessDefinitionKey(String processDefinitionKey) {
        this.processDefinitionKey = processDefinitionKey;
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public String getKeyValue() {
        return keyValue;
    }

    public void setKeyValue(String keyValue) {
        this.keyValue = keyValue;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
