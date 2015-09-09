package org.marketcetera.core;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.concurrent.ThreadSafe;
import javax.management.MBeanServerConnection;

import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.context.Lifecycle;

import com.sun.management.OperatingSystemMXBean;

/* $License$ */

/**
 * Monitors system statistics.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: SystemMonitor.java 83882 2014-08-01 22:31:54Z colin $
 * @since 1.3.1
 */
@ThreadSafe
public class SystemMonitor
        implements Lifecycle, Runnable
{
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#isRunning()
     */
    @Override
    public boolean isRunning()
    {
        return running.get();
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#start()
     */
    @Override
    public synchronized void start()
    {
        if(isRunning()) {
            stop();
        }
        thread = new Thread(this,
                            "System Monitor");
        thread.start();
        running.set(true);
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#stop()
     */
    @Override
    public synchronized void stop()
    {
        if(!isRunning()) {
            return;
        }
        try {
            keepAlive.set(false);
            if(thread != null) {
                thread.interrupt();
                thread.join();
            }
        } catch (InterruptedException ignored) {
        } finally {
            thread = null;
            running.set(false);
        }
    }
    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run()
    {
        summary();
        try {
            while(keepAlive.get()) {
                Thread.sleep(monitorInterval);
                monitorProcessCpu();
                monitorFreeRam();
                if(isOverThreshold()) {
                    overThreshold();
                } else {
                    underThreshold();
                }
            }
        } catch (InterruptedException ignored) {
        } catch (Exception e) {
            stop();
        }
    }
    /**
     * Get the process CPU threshold value.
     *
     * @return a <code>double</code> value
     */
    public double getProcessCpuThreshold()
    {
        return thresholdStats.processCpu.doubleValue();
    }
    /**
     * Sets the process CPU threshold value.
     *
     * @param inProcessCpuThreshold a <code>double</code> value
     */
    public void setProcessCpuThreshold(double inProcessCpuThreshold)
    {
        thresholdStats.setProcessCpu(inProcessCpuThreshold);
    }
    /**
     * Get the monitorInterval value.
     *
     * @return a <code>long</code> value
     */
    public long getMonitorInterval()
    {
        return monitorInterval;
    }
    /**
     * Sets the monitorInterval value.
     *
     * @param inMonitorInterval a <code>long</code> value
     */
    public void setMonitorInterval(long inMonitorInterval)
    {
        monitorInterval = inMonitorInterval;
    }
    /**
     * Get the freeRamThreshold value.
     *
     * @return a <code>long</code> value
     */
    public long getFreeRamThreshold()
    {
        return thresholdStats.freeRam;
    }
    /**
     * Sets the freeRamThreshold value.
     *
     * @param inFreeRamThreshold a <code>long</code> value
     */
    public void setFreeRamThreshold(long inFreeRamThreshold)
    {
        thresholdStats.setFreeRam(inFreeRamThreshold);
    }
    /**
     * Get the underThresholdAction value.
     *
     * @return a <code>Runnable</code> value
     */
    public Runnable getUnderThresholdAction()
    {
        return underThresholdAction;
    }
    /**
     * Sets the underThresholdAction value.
     *
     * @param inUnderThresholdAction a <code>Runnable</code> value
     */
    public void setUnderThresholdAction(Runnable inUnderThresholdAction)
    {
        underThresholdAction = inUnderThresholdAction;
    }
    /**
     * Get the overThresholdAction value.
     *
     * @return a <code>Runnable</code> value
     */
    public Runnable getOverThresholdAction()
    {
        return overThresholdAction;
    }
    /**
     * Sets the overThresholdAction value.
     *
     * @param inOverThresholdAction a <code>Runnable</code> value
     */
    public void setOverThresholdAction(Runnable inOverThresholdAction)
    {
        overThresholdAction = inOverThresholdAction;
    }
    /**
     * Create a new SystemMonitor instance.
     */
    public SystemMonitor()
    {
        MBeanServerConnection mbsc = ManagementFactory.getPlatformMBeanServer();
        try {
            osMBean = ManagementFactory.newPlatformMXBeanProxy(mbsc,
                                                               ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME,
                                                               OperatingSystemMXBean.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        underThresholdAction = new Runnable() {
            @Override
            public void run()
            {
                SLF4JLoggerProxy.trace(SystemMonitor.this,
                                       "System monitor under threshold: {}",
                                       currentStats);
            }
        };
        overThresholdAction = new Runnable() {
            @Override
            public void run()
            {
                SLF4JLoggerProxy.warn(SystemMonitor.this,
                                      "System monitor over threshold: {}",
                                      currentStats);
            }
        };
    }
    /**
     * Announces summary information about the host.
     */
    private void summary()
    {
        SLF4JLoggerProxy.info(this,
                              "{} {} {} {} processors {} max heap RAM",
                              osMBean.getName(),
                              osMBean.getVersion(),
                              osMBean.getArch(),
                              osMBean.getAvailableProcessors(),
                              Runtime.getRuntime().totalMemory());
    }
    /**
     * Performs necessary actions if the threshold has not been exceeded.
     */
    private void underThreshold()
    {
        try {
            if(underThresholdAction != null) {
                underThresholdAction.run();
            }
        } catch (Exception e) {
            SLF4JLoggerProxy.warn(this,
                                  e);
        }
    }
    /**
     * Performs necessary actions if the threshold has been exceeded.
     */
    private void overThreshold()
    {
        try {
            if(overThresholdAction != null) {
                overThresholdAction.run();
            }
        } catch (Exception e) {
            SLF4JLoggerProxy.warn(this,
                                  e);
        }
    }
    /**
     * Determines if the current stats exceeds the established threshold.
     *
     * @return a <code>boolean</code> value
     */
    private boolean isOverThreshold()
    {
        return currentStats.compareTo(thresholdStats) != -1;
    }
    /**
     * Monitors free RAM.
     */
    private void monitorFreeRam()
    {
        long freeRam = osMBean.getFreePhysicalMemorySize() / MEGABYTE;
        currentStats.setFreeRam(freeRam);
    }
    /**
     * Monitors process CPU usage over time.
     */
    private void monitorProcessCpu()
    {
        long nanoNow = System.nanoTime();
        long cpuNow = osMBean.getProcessCpuTime();
        try {
            if(nanoBefore == -1) {
                return;
            }
            double percent;
            // calculate process CPU (avg)
            if(nanoNow > nanoBefore) {
                percent = ((cpuNow-cpuBefore)*100.0)/(nanoNow-nanoBefore);
            } else {
                percent = 0;
            }
            currentStats.setProcessCpu(percent);
        } finally {
            cpuBefore = cpuNow;
            nanoBefore = nanoNow;
        }
    }
    /**
     * Records system statistics.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id: SystemMonitor.java 83882 2014-08-01 22:31:54Z colin $
     * @since 1.3.1
     */
    private static class Stats
            implements Comparable<Stats>
    {
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            StringBuilder builder = new StringBuilder();
            builder.append("ProcessCPU: ").append(processCpu.toPlainString()).append('%')
                    .append(" FreeRAM: ").append(freeRam).append("mb");
            return builder.toString();
        }
        /* (non-Javadoc)
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
        @Override
        public int compareTo(Stats inThreshold)
        {
            // compareTo returns 1 if this object exceeds the threshold, otherwise returns -1
            if(processCpu.compareTo(inThreshold.processCpu) == 1) {
                return 1;
            }
            // threshold for RAM means less free than the threshold
            if(freeRam < inThreshold.freeRam) {
                return 1;
            }
            return -1;
        }
        /**
         * Create a new Stats instance.
         *
         * @param inProcessCpu a <code>double</code> value
         * @param inFreeRam a <code>long</code> value
         */
        private Stats(double inProcessCpu,
                      long inFreeRam)
        {
            setProcessCpu(inProcessCpu);
            setFreeRam(inFreeRam);
        }
        /**
         * Create a new Stats instance.
         */
        private Stats() {}
        /**
         * Sets the processCpu value.
         *
         * @param inProcessCpu a <code>BigDecimal</code> value
         */
        private void setProcessCpu(double inProcessCpu)
        {
            processCpu = new BigDecimal(inProcessCpu,
                                        conversionContext);
        }
        /**
         * Sets the freeRam value.
         *
         * @param inFreeRam a <code>long</code> value
         */
        private void setFreeRam(long inFreeRam)
        {
            freeRam = inFreeRam;
        }
        /**
         * process CPU value
         */
        private volatile BigDecimal processCpu = BigDecimal.ZERO;
        /**
         * free system RAM value
         */
        private volatile long freeRam;
        /**
         * converts doubles to BigDecimals in a consistent fashion
         */
        private static final MathContext conversionContext = new MathContext(2,RoundingMode.HALF_UP);
    }
    /**
     * used to track cpu by recording the nano timestamp from the last monitor session
     */
    private volatile long nanoBefore = -1;
    /**
     * used to track cpu by recording the cpu from the last monitor session
     */
    private volatile long cpuBefore = -1;
    /**
     * used to supply values from the OS for monitoring
     */
    private final OperatingSystemMXBean osMBean;
    /**
     * interval at which monitoring occurs
     */
    private volatile long monitorInterval = 30000;
    /**
     * thread on which the monitor runs
     */
    private volatile Thread thread;
    /**
     * action to perform if the statistics are below threshold
     */
    private volatile Runnable underThresholdAction;
    /**
     * action to perform if the statistics are above threshold
     */
    private volatile Runnable overThresholdAction;
    /**
     * indicates if the monitor is running or not
     */
    private final AtomicBoolean running = new AtomicBoolean(false);
    /**
     * indicates if the monitor should keep running or stop
     */
    private final AtomicBoolean keepAlive = new AtomicBoolean(true);
    /**
     * holds current statistics
     */
    private final Stats currentStats = new Stats();
    /**
     * threshold above which monitor results are published
     */
    private final Stats thresholdStats = new Stats(50,
                                                   250);
    /**
     * bytes in a megabyte
     */
    private static final long MEGABYTE = 1048576;
}
