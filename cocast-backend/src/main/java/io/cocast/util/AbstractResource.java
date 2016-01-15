package io.cocast.util;

import io.cocast.util.log.LogUtils;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;

/**
 * Abstract class for all resources
 */
public abstract class AbstractResource {


    /**
     * Logs the result of the API
     */
    protected void logResult(String message, HttpServletRequest httpRequest, String method, Integer count,
                             Integer status, Long initTime) {

        Long endTime = System.currentTimeMillis();
        LogUtils.logAPIExecution(getLogger(), message, getModuleName(), getResourceName(),
                method, count, httpRequest.getRemoteAddr(), status, endTime - initTime);
    }

    /**
     * Return the name of the module
     */
    protected abstract String getModuleName();

    /**
     * Return the name of the resource
     */
    protected abstract String getResourceName();

    /**
     * Return the logger
     */
    protected abstract Logger getLogger();
}
