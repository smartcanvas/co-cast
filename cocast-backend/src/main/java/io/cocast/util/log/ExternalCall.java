package io.cocast.util.log;

/**
 * Logs an external call
 */
class ExternalCall extends LoggableObject {

    private String externalApp;
    private String operation;
    private String source;
    private Integer numberResults;
    private Integer status;
    private Long executionTime;


    public ExternalCall(String message, String externalApp, String operation, String source,
                        Integer numberResults, Integer status, Long executionTime) {
        super(message);
        this.externalApp = externalApp;
        this.operation = operation;
        this.source = source;
        this.numberResults = numberResults;
        this.status = status;
        this.executionTime = executionTime;
    }

    public String getExternalApp() {
        return externalApp;
    }

    public void setExternalApp(String externalApp) {
        this.externalApp = externalApp;
    }

    public Integer getNumberResults() {
        return numberResults;
    }

    public void setNumberResults(Integer numberResults) {
        this.numberResults = numberResults;
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

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public String logType() {
        return "externalCall";
    }
}
