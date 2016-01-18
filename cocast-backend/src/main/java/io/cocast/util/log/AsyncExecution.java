package io.cocast.util.log;

import io.cocast.util.AbstractRunnable;

/**
 * Created by dviveiros on 18/01/16.
 */
public class AsyncExecution extends LoggableObject {

    private String jobName;
    private Long executionTime;
    private boolean success;
    private Integer retries;

    /**
     * Constructor
     */
    public AsyncExecution(AbstractRunnable runnable, String message, Long executionTime, boolean success, Integer retries) {
        super(message);
        this.jobName = runnable.getJobName();
        this.success = success;
        this.retries = retries;
        this.executionTime = executionTime;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
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

    public Integer getRetries() {
        return retries;
    }

    public void setRetries(Integer retries) {
        this.retries = retries;
    }

    @Override
    public String logType() {
        return "asyncExecution";
    }
}
