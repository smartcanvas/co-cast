package io.cocast.util;

import io.cocast.auth.SecurityContext;
import io.cocast.util.log.LogUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.validation.ValidationException;
import java.util.concurrent.TimeUnit;

/**
 * Standard runnable for all Co-cast asynchronous taks
 */
public abstract class AbstractRunnable implements Runnable {

    private static Logger logger = LogManager.getLogger(AbstractRunnable.class.getName());

    public static final Integer DEFAULT_RETRY = 3;
    public static final Integer DEFAULT_RETRY_INTERVAL = 10; //seconds

    private String networkMnemonic;
    private SecurityContext securityContext;
    private int retry;

    /**
     * Constructor
     */
    public AbstractRunnable(String networkMnemonic, SecurityContext context, Integer retry) {
        this.networkMnemonic = networkMnemonic;
        this.securityContext = context;
        this.retry = retry;
    }

    /**
     * Constructor
     */
    public AbstractRunnable(String networkMnemonic, SecurityContext context) {
        this(networkMnemonic, context, DEFAULT_RETRY);
    }

    /**
     * Constructor
     */
    public AbstractRunnable(String networkMnemonic) {
        this(networkMnemonic, SecurityContext.get(), DEFAULT_RETRY);
    }

    @Override
    public void run() {

        SecurityContext.set(securityContext);

        long initTime = System.currentTimeMillis();
        logger.info("Executing asynchronous job " + getJobName());

        int tentative = 0;
        while (tentative <= retry) {
            try {
                this.execute();
                long endTime = System.currentTimeMillis();
                LogUtils.logAsyncExecution(logger, this, "OK", endTime - initTime, true, tentative);
                break;
            } catch (ValidationException exc) {
                //nothing can be done
                long endTime = System.currentTimeMillis();
                LogUtils.logAsyncExecution(logger, this, exc.getMessage(), endTime - initTime, false, tentative);
                break;
            } catch (CoCastCallException exc) {
                //nothing can be done
                long endTime = System.currentTimeMillis();
                LogUtils.logAsyncExecution(logger, this, exc.getMessage(), endTime - initTime, false, tentative);
                break;
            } catch (Exception exc) {
                tentative++;
                if (tentative <= retry) {
                    try {
                        TimeUnit.SECONDS.sleep(DEFAULT_RETRY_INTERVAL * (tentative - 1));
                        logger.warn("Execution of " + getJobName() + " has failed due to " + exc.getMessage()
                                + ". Retrying #" + tentative);
                    } catch (InterruptedException e) {
                        logger.error("Error in the async mechanism", e);
                    }
                } else {
                    long endTime = System.currentTimeMillis();
                    LogUtils.logAsyncExecution(logger, this, exc.getMessage(), endTime - initTime, false, tentative - 1);
                    LogUtils.fatal(logger, exc.getMessage(), exc);
                }
            }
        }
    }

    protected String getNetworkMnemonic() {
        return networkMnemonic;
    }

    protected SecurityContext getSecurityContext() {
        return securityContext;
    }

    /**
     * Executes the job
     */
    public abstract void execute() throws ValidationException, Exception;

    /**
     * Gets the name of the job
     */
    public abstract String getJobName();
}
