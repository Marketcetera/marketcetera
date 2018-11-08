package com.marketcetera.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;

/* $License$ */

/**
 * Manages directories needed for testing.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: TestDirectoryManager.java 85060 2015-12-16 03:19:37Z colin $
 * @since $Release$
 */
public class TestDirectoryManager
{
    /**
     * Get the testDirectories value.
     *
     * @return a <code>List<String></code> value
     */
    public List<String> getTestDirectories()
    {
        return testDirectories;
    }
    /**
     * Sets the testDirectories value.
     *
     * @param inTestDirectories a <code>List<String></code> value
     */
    public void setTestDirectories(List<String> inTestDirectories)
    {
        testDirectories = inTestDirectories;
    }
    /**
     * Get the deleteDirectories value.
     *
     * @return a <code>List<String></code> value
     */
    public List<String> getDeleteDirectories()
    {
        return deleteDirectories;
    }
    /**
     * Sets the deleteDirectories value.
     *
     * @param inDeleteDirectories a <code>List<String></code> value
     */
    public void setDeleteDirectories(List<String> inDeleteDirectories)
    {
        deleteDirectories = inDeleteDirectories;
    }
    /**
     * Initializes and starts the object.
     *
     * @throws Exception if an error occurs starting the object
     */
    @PostConstruct
    public void start()
            throws Exception
    {
        if(testDirectories != null) {
            for(String testDirectoryName : testDirectories) {
                File testDirectory = new File(testDirectoryName);
                FileUtils.forceMkdir(testDirectory);
                FileUtils.cleanDirectory(testDirectory);
            }
        }
        if(deleteDirectories != null) {
            for(String testDirectoryName : deleteDirectories) {
                File testDirectory = new File(testDirectoryName);
                FileUtils.deleteDirectory(testDirectory);
            }
        }
    }
    /**
     * test directories to create
     */
    private List<String> testDirectories = new ArrayList<>();
    /**
     * test directories to delete
     */
    private List<String> deleteDirectories = new ArrayList<>();
}
