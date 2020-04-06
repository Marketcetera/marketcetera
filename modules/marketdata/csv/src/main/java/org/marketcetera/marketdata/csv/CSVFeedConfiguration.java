package org.marketcetera.marketdata.csv;

import java.io.File;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.Validate;
import org.assertj.core.util.Lists;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Component;

/**
 * Encapsulates the data necessary to initialize an instance of {@link CSVFeed}.
 * 
 * @author toli kuznets
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @since 2.1.0
 * @version $Id$
 */
@Component
@EnableAutoConfiguration
@ClassVersion("$Id$")
public final class CSVFeedConfiguration 
{
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("CSVFeedConfiguration [startDelay=").append(startDelay).append(", replayAtOriginalRate=")
                .append(replayAtOriginalRate).append(", replayRate=").append(replayRate)
                .append(", marketdataDirectory=").append(marketdataDirectory).append(", marketdataFileNames=")
                .append(marketdataFileNames).append("]");
        return builder.toString();
    }
    /**
     * Get the marketdataDirectory value.
     *
     * @return a <code>File</code> value
     */
    public File getMarketdataDirectory()
    {
        return marketdataDirectory;
    }
    /**
     * Get the replayRate value.
     *
     * @return a <code>long</code> value
     */
    public long getReplayRate()
    {
        return replayRate;
    }
    /**
     * Sets the marketdataDirectory value.
     *
     * @param inMarketdataDirectory a <code>File</code> value
     */
    public void setMarketdataDirectory(File inMarketdataDirectory)
    {
        marketdataDirectory = inMarketdataDirectory;
    }
    /**
     * Sets the replayRate value.
     *
     * @param inReplayRate a <code>long</code> value
     */
    public void setReplayRate(long inReplayRate)
    {
        replayRate = inReplayRate;
    }
    /**
     * Get the startDelay value.
     *
     * @return a <code>long</code> value
     */
    public long getStartDelay()
    {
        return startDelay;
    }
    /**
     * Sets the startDelay value.
     *
     * @param inStartDelay a <code>long</code> value
     */
    public void setStartDelay(long inStartDelay)
    {
        startDelay = inStartDelay;
    }
    /**
     * Get the replayAtOriginalRate value.
     *
     * @return a <code>boolean</code> value
     */
    public boolean getReplayAtOriginalRate()
    {
        return replayAtOriginalRate;
    }
    /**
     * Sets the replayAtOriginalRate value.
     *
     * @param inReplayAtOriginalRate a <code>boolean</code> value
     */
    public void setReplayAtOriginalRate(boolean inReplayAtOriginalRate)
    {
        replayAtOriginalRate = inReplayAtOriginalRate;
    }
    /**
     * Get the marketdataFiles value.
     *
     * @return a <code>List&lt;File&gt;</code> value
     */
    public List<File> getMarketdataFiles()
    {
        return marketdataFiles;
    }
    /**
     * Sets the marketdataFiles value.
     *
     * @param inMarketdataFiles a <code>List&lt;File&gt;</code> value
     */
    public void setMarketdataFiles(List<File> inMarketdataFiles)
    {
        marketdataFiles = inMarketdataFiles;
    }
    /**
     * Get the marketdataFileNames value.
     *
     * @return a <code>List&lt;String&gt;</code> value
     */
    public List<String> getMarketdataFileNames()
    {
        return marketdataFileNames;
    }
    /**
     * Sets the marketdataFileNames value.
     *
     * @param inMarketdataFileNames a <code>List&lt;String&gt;</code> value
     */
    public void setMarketdataFileNames(List<String> inMarketdataFileNames)
    {
        marketdataFileNames = inMarketdataFileNames;
    }
    /**
     * Validate and start the object.
     */
    @PostConstruct
    public void start()
    {
        Validate.isTrue(marketdataDirectory.exists(),
                        "Market data directory ' " + marketdataDirectory.getAbsolutePath() + "' does not exist");
        Validate.isTrue(marketdataDirectory.canRead(),
                        "Market data directory ' " + marketdataDirectory.getAbsolutePath() + "' is not readable");
        // make sure replayRate is bounded by zero on the left
        replayRate = Math.max(0,
                              replayRate);
        Validate.isTrue(!marketdataFileNames.isEmpty(),
                        "Must specify at least one market data file");
        for(String marketdataFileName : marketdataFileNames) {
            File marketdataFile = new File(marketdataDirectory,
                                           marketdataFileName);
            Validate.isTrue(marketdataFile.exists(),
                            "Market data file ' " + marketdataFile.getAbsolutePath() + "' does not exist");
            Validate.isTrue(marketdataFile.canRead(),
                            "Market data file ' " + marketdataFile.getAbsolutePath() + "' is not readable");
            marketdataFiles.add(marketdataFile);
        }
        SLF4JLoggerProxy.debug(this,
                               "Prepared {} market data file(s)",
                               marketdataFiles.size());
    }
    /**
     * derived list of {@link File} objects containing market data
     */
    private List<File> marketdataFiles = Lists.newArrayList();
    /**
     * the directory in which to find marketdata
     */
    @Value("${metc.mdata.csv.mdataDir}")
    private File marketdataDirectory;
    /**
     * market data files to replay in order
     */
    @Value("#{'${metc.mdata.csv.mdataFileNames}'.split(',')}") 
    private List<String> marketdataFileNames = Lists.newArrayList();
    /**
     * indicate whether to replace the market data at the original rate as recorded or at an artificial rate as specified by {@link #replayRate}
     */
    @Value("${metc.mdata.csv.mdata.replayAtOriginalRate:false}")
    private boolean replayAtOriginalRate;
    /**
     * number of milliseconds to delay between events
     */
    @Value("${metc.mdata.csv.mdata.replayRate:0}")
    private long replayRate;
    /**
     * number of milliseconds to delay at system start before processing market data files
     */
    @Value("${metc.mdata.csv.mdata.startDelay:60000}")
    private long startDelay;
}
