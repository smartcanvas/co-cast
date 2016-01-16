package io.cocast.util.log;

import javax.servlet.http.HttpServletResponse;

/**
 * API execution log
 */
class APIExecution extends LoggableObject {

    private String module;
    private String resource;
    private String method;
    private Integer numberResults;
    private String ipAddress;
    private Integer status;
    private Long executionTime;
    private boolean success;


    /**
     * Constructor
     */
    public APIExecution(String message, String module, String resource, String method,
                        Integer numberResults, String ipAddress,
                        Integer status, Long execTime) {
        super(message);

        this.module = module;
        this.resource = resource;
        this.method = method;
        this.numberResults = numberResults;
        this.status = status;
        this.executionTime = execTime;
        this.ipAddress = ipAddress;
        this.success = (status == HttpServletResponse.SC_OK) ||
                (status == HttpServletResponse.SC_CREATED) ||
                (status == HttpServletResponse.SC_NO_CONTENT);
    }

    @Override
    public String logType() {
        return "apiExecution";
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Integer getNumberResults() {
        return numberResults;
    }

    public void setNumberResults(Integer numberResults) {
        this.numberResults = numberResults;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(Long executionTime) {
        this.executionTime = executionTime;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
