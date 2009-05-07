/*******************************************************************************
 * Copyright (c) 2000, 2003 db4objects, Inc.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     David Orme (db4objects) - Initial implementation
 ******************************************************************************/
package com.swtworkbench.community.xswt.metalogger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * FileLogger. A logger that logs to a file.
 *
 * @author djo
 */
public class FileLogger extends AbstractLogger {
    private String filePath;
    
    /**
     * Construct a FileLogger.
     * 
     * @param filePath The path to the file
     * @param configPath The logger configuration path
     */
    public FileLogger(String filePath, String configPath) {
        this.filePath=filePath;
        new File(filePath).delete();
        configure(configPath);
    }
    
    private void configure(String configPath) {
        File configFile=new File(configPath);
        if(!configFile.exists()) {
            return;
        }
        BufferedReader in=null;
        try {
            in=new BufferedReader(new FileReader(configPath));
            String curLine=in.readLine();
            setDebug(Boolean.valueOf(curLine).booleanValue());
            if(isDebug()) {
                return;
            }
            while((curLine=in.readLine())!=null) {
                curLine=curLine.trim();
                if(curLine.length()==0) {
                    continue;
                }
                try {
                    Class clazz=Class.forName(curLine);
                    setDebug(clazz, true);
                } catch (ClassNotFoundException e) {
                    System.err.println("Unable to set debugging for "+curLine);
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(in!=null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /* (non-Javadoc)
     * @see com.swtworkbench.community.xswt.metalogger.ILogger#error(java.lang.Throwable, java.lang.String)
     */
    public void error(Throwable t, String message) {
        StringWriter str=new StringWriter();
        PrintWriter print=new PrintWriter(str);
        t.printStackTrace(print);
        print.close();
        log(message+"\n"+str.toString() + "\n");
    }

    /* (non-Javadoc)
     * @see com.swtworkbench.community.xswt.metalogger.ILogger#message(java.lang.String)
     */
    public void message(String message) {
        log(message + "\n");
    }

    /* (non-Javadoc)
     * @see com.swtworkbench.community.xswt.metalogger.ILogger#data(java.lang.String)
     */
    public void data(String data) {
        log(data);
    }

    /* (non-Javadoc)
     * @see com.swtworkbench.community.xswt.metalogger.ILogger#debug(java.lang.Class, java.lang.String)
     */
    public void debug(Class subject, String message) {
        if (isDebug(subject)) {
            log(subject.getName() + ": " + message + "\n");
        }
    }

    private void log(String msg) {
        try {
            PrintWriter out=new PrintWriter(new FileWriter(filePath,true));
            try {
                out.print(msg);
            }
            finally {
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
