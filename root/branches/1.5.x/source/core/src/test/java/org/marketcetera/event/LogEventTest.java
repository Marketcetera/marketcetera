package org.marketcetera.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.marketcetera.event.TestMessages.MESSAGE_0P;
import static org.marketcetera.event.TestMessages.MESSAGE_1P;
import static org.marketcetera.event.TestMessages.MESSAGE_2P;
import static org.marketcetera.event.TestMessages.MESSAGE_3P;
import static org.marketcetera.event.TestMessages.MESSAGE_4P;
import static org.marketcetera.event.TestMessages.MESSAGE_5P;
import static org.marketcetera.event.TestMessages.MESSAGE_6P;
import static org.marketcetera.event.TestMessages.MESSAGE_NP;
import static org.marketcetera.marketdata.TestMessages.EXPECTED_EXCEPTION;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.junit.Test;
import org.marketcetera.event.LogEvent.Level;
import org.marketcetera.util.log.I18NMessage;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.log.I18NMessage1P;
import org.marketcetera.util.log.I18NMessage2P;
import org.marketcetera.util.log.I18NMessage3P;
import org.marketcetera.util.log.I18NMessage4P;
import org.marketcetera.util.log.I18NMessage5P;
import org.marketcetera.util.log.I18NMessage6P;
import org.marketcetera.util.log.I18NMessageNP;

/* $License$ */

/**
 * Tests {@link LogEvent}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.5.0
 */
public class LogEventTest
{
    private final I18NMessage[] messages = new I18NMessage[] { MESSAGE_0P, MESSAGE_1P, MESSAGE_2P, MESSAGE_3P, MESSAGE_4P, MESSAGE_5P, MESSAGE_6P, MESSAGE_NP };
    private final Level[] levels = Level.values();
    private final Throwable[] exceptions = new Throwable[] { null, new NullPointerException(EXPECTED_EXCEPTION.getText()) };
    private final Serializable[][] parameters = new Serializable[][] { {},{"1"},{"1","2"},{"1","2","3"},{"1","2","3","4"},{"1","2","3","4","5"},
            {"1","2","3","4","5","6"},{"1","2","3","4","5","6","7"} };
    /**
     * Tests all the permutations of the creation shortcuts.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void eventShortcuts()
        throws Exception
    {
        for(int levelIndex=0;levelIndex<levels.length;levelIndex++) {
            for(int exceptionIndex=0;exceptionIndex<exceptions.length;exceptionIndex++) {
                if(exceptions[exceptionIndex] == null) {
                    verifyEvent(LogEvent.event((I18NMessage0P)messages[0],
                                               levels[levelIndex]),
                                levels[levelIndex],
                                exceptions[exceptionIndex],
                                messages[0],
                                parameters[0]);
                    if(levels[levelIndex].equals(Level.DEBUG)) {
                        verifyEvent(LogEvent.debug((I18NMessage0P)messages[0]),
                                    levels[levelIndex],
                                    exceptions[exceptionIndex],
                                    messages[0],
                                    parameters[0]);
                    } else if(levels[levelIndex].equals(Level.INFO)) {
                        verifyEvent(LogEvent.info((I18NMessage0P)messages[0]),
                                    levels[levelIndex],
                                    exceptions[exceptionIndex],
                                    messages[0],
                                    parameters[0]);
                    } else if(levels[levelIndex].equals(Level.WARN)) {
                        verifyEvent(LogEvent.warn((I18NMessage0P)messages[0]),
                                    levels[levelIndex],
                                    exceptions[exceptionIndex],
                                    messages[0],
                                    parameters[0]);
                    } else if(levels[levelIndex].equals(Level.ERROR)) {
                        verifyEvent(LogEvent.error((I18NMessage0P)messages[0]),
                                    levels[levelIndex],
                                    exceptions[exceptionIndex],
                                    messages[0],
                                    parameters[0]);
                    }
                } else {
                    verifyEvent(LogEvent.event((I18NMessage0P)messages[0],
                                               exceptions[exceptionIndex],
                                               levels[levelIndex]),
                                levels[levelIndex],
                                exceptions[exceptionIndex],
                                messages[0],
                                parameters[0]);
                    if(levels[levelIndex].equals(Level.DEBUG)) {
                        verifyEvent(LogEvent.debug((I18NMessage0P)messages[0],
                                                   exceptions[exceptionIndex]),
                                    levels[levelIndex],
                                    exceptions[exceptionIndex],
                                    messages[0],
                                    parameters[0]);
                    } else if(levels[levelIndex].equals(Level.INFO)) {
                        verifyEvent(LogEvent.info((I18NMessage0P)messages[0],
                                                  exceptions[exceptionIndex]),
                                    levels[levelIndex],
                                    exceptions[exceptionIndex],
                                    messages[0],
                                    parameters[0]);
                    } else if(levels[levelIndex].equals(Level.WARN)) {
                        verifyEvent(LogEvent.warn((I18NMessage0P)messages[0],
                                                  exceptions[exceptionIndex]),
                                    levels[levelIndex],
                                    exceptions[exceptionIndex],
                                    messages[0],
                                    parameters[0]);
                    } else if(levels[levelIndex].equals(Level.ERROR)) {
                        verifyEvent(LogEvent.error((I18NMessage0P)messages[0],
                                                   exceptions[exceptionIndex]),
                                    levels[levelIndex],
                                    exceptions[exceptionIndex],
                                    messages[0],
                                    parameters[0]);
                    }
                }
                if(exceptions[exceptionIndex] == null) {
                    verifyEvent(LogEvent.event((I18NMessage1P)messages[1],
                                               levels[levelIndex],
                                               parameters[1][0]),
                                levels[levelIndex],
                                exceptions[exceptionIndex],
                                messages[1],
                                parameters[1]);
                    if(levels[levelIndex].equals(Level.DEBUG)) {
                        verifyEvent(LogEvent.debug((I18NMessage1P)messages[1],
                                                   parameters[1][0]),
                                    levels[levelIndex],
                                    exceptions[exceptionIndex],
                                    messages[1],
                                    parameters[1]);
                    } else if(levels[levelIndex].equals(Level.INFO)) {
                        verifyEvent(LogEvent.info((I18NMessage1P)messages[1],
                                                  parameters[1][0]),
                                    levels[levelIndex],
                                    exceptions[exceptionIndex],
                                    messages[1],
                                    parameters[1]);
                    } else if(levels[levelIndex].equals(Level.WARN)) {
                        verifyEvent(LogEvent.warn((I18NMessage1P)messages[1],
                                                  parameters[1][0]),
                                    levels[levelIndex],
                                    exceptions[exceptionIndex],
                                    messages[1],
                                    parameters[1]);
                    } else if(levels[levelIndex].equals(Level.ERROR)) {
                        verifyEvent(LogEvent.error((I18NMessage1P)messages[1],
                                                   parameters[1][0]),
                                    levels[levelIndex],
                                    exceptions[exceptionIndex],
                                    messages[1],
                                    parameters[1]);
                    }
                } else {
                    verifyEvent(LogEvent.event((I18NMessage1P)messages[1],
                                               exceptions[exceptionIndex],
                                               levels[levelIndex],
                                               parameters[1][0]),
                                levels[levelIndex],
                                exceptions[exceptionIndex],
                                messages[1],
                                parameters[1]);
                    if(levels[levelIndex].equals(Level.DEBUG)) {
                        verifyEvent(LogEvent.debug((I18NMessage1P)messages[1],
                                                   exceptions[exceptionIndex],
                                                   parameters[1][0]),
                                    levels[levelIndex],
                                    exceptions[exceptionIndex],
                                    messages[1],
                                    parameters[1]);
                    } else if(levels[levelIndex].equals(Level.INFO)) {
                        verifyEvent(LogEvent.info((I18NMessage1P)messages[1],
                                                  exceptions[exceptionIndex],
                                                  parameters[1][0]),
                                    levels[levelIndex],
                                    exceptions[exceptionIndex],
                                    messages[1],
                                    parameters[1]);
                    } else if(levels[levelIndex].equals(Level.WARN)) {
                        verifyEvent(LogEvent.warn((I18NMessage1P)messages[1],
                                                  exceptions[exceptionIndex],
                                                  parameters[1][0]),
                                    levels[levelIndex],
                                    exceptions[exceptionIndex],
                                    messages[1],
                                    parameters[1]);
                    } else if(levels[levelIndex].equals(Level.ERROR)) {
                        verifyEvent(LogEvent.error((I18NMessage1P)messages[1],
                                                   exceptions[exceptionIndex],
                                                   parameters[1][0]),
                                    levels[levelIndex],
                                    exceptions[exceptionIndex],
                                    messages[1],
                                    parameters[1]);
                    }
                }
                if(exceptions[exceptionIndex] == null) {
                    verifyEvent(LogEvent.event((I18NMessage2P)messages[2],
                                               levels[levelIndex],
                                               parameters[2][0],
                                               parameters[2][1]),
                                levels[levelIndex],
                                exceptions[exceptionIndex],
                                messages[2],
                                parameters[2]);
                    if(levels[levelIndex].equals(Level.DEBUG)) {
                        verifyEvent(LogEvent.debug((I18NMessage2P)messages[2],
                                                   parameters[2][0],
                                                   parameters[2][1]),
                                    levels[levelIndex],
                                    exceptions[exceptionIndex],
                                    messages[2],
                                    parameters[2]);
                    } else if(levels[levelIndex].equals(Level.INFO)) {
                        verifyEvent(LogEvent.info((I18NMessage2P)messages[2],
                                                  parameters[2][0],
                                                  parameters[2][1]),
                                    levels[levelIndex],
                                    exceptions[exceptionIndex],
                                    messages[2],
                                    parameters[2]);
                    } else if(levels[levelIndex].equals(Level.WARN)) {
                        verifyEvent(LogEvent.warn((I18NMessage2P)messages[2],
                                                  parameters[2][0],
                                                  parameters[2][1]),
                                    levels[levelIndex],
                                    exceptions[exceptionIndex],
                                    messages[2],
                                    parameters[2]);
                    } else if(levels[levelIndex].equals(Level.ERROR)) {
                        verifyEvent(LogEvent.error((I18NMessage2P)messages[2],
                                                   parameters[2][0],
                                                   parameters[2][1]),
                                    levels[levelIndex],
                                    exceptions[exceptionIndex],
                                    messages[2],
                                    parameters[2]);
                    }
                } else {
                    verifyEvent(LogEvent.event((I18NMessage2P)messages[2],
                                               exceptions[exceptionIndex],
                                               levels[levelIndex],
                                               parameters[2][0],
                                               parameters[2][1]),
                                levels[levelIndex],
                                exceptions[exceptionIndex],
                                messages[2],
                                parameters[2]);
                    if(levels[levelIndex].equals(Level.DEBUG)) {
                        verifyEvent(LogEvent.debug((I18NMessage2P)messages[2],
                                                   exceptions[exceptionIndex],
                                                   parameters[2][0],
                                                   parameters[2][1]),
                                    levels[levelIndex],
                                    exceptions[exceptionIndex],
                                    messages[2],
                                    parameters[2]);
                    } else if(levels[levelIndex].equals(Level.INFO)) {
                        verifyEvent(LogEvent.info((I18NMessage2P)messages[2],
                                                  exceptions[exceptionIndex],
                                                  parameters[2][0],
                                                  parameters[2][1]),
                                    levels[levelIndex],
                                    exceptions[exceptionIndex],
                                    messages[2],
                                    parameters[2]);
                    } else if(levels[levelIndex].equals(Level.WARN)) {
                        verifyEvent(LogEvent.warn((I18NMessage2P)messages[2],
                                                  exceptions[exceptionIndex],
                                                  parameters[2][0],
                                                  parameters[2][1]),
                                    levels[levelIndex],
                                    exceptions[exceptionIndex],
                                    messages[2],
                                    parameters[2]);
                    } else if(levels[levelIndex].equals(Level.ERROR)) {
                        verifyEvent(LogEvent.error((I18NMessage2P)messages[2],
                                                   exceptions[exceptionIndex],
                                                   parameters[2][0],
                                                   parameters[2][1]),
                                    levels[levelIndex],
                                    exceptions[exceptionIndex],
                                    messages[2],
                                    parameters[2]);
                    }
                }
                if(exceptions[exceptionIndex] == null) {
                    verifyEvent(LogEvent.event((I18NMessage3P)messages[3],
                                               levels[levelIndex],
                                               parameters[3][0],
                                               parameters[3][1],
                                               parameters[3][2]),
                                levels[levelIndex],
                                exceptions[exceptionIndex],
                                messages[3],
                                parameters[3]);
                    if(levels[levelIndex].equals(Level.DEBUG)) {
                        verifyEvent(LogEvent.debug((I18NMessage3P)messages[3],
                                                   parameters[3][0],
                                                   parameters[3][1],
                                                   parameters[3][2]),
                                    levels[levelIndex],
                                    exceptions[exceptionIndex],
                                    messages[3],
                                    parameters[3]);
                    } else if(levels[levelIndex].equals(Level.INFO)) {
                        verifyEvent(LogEvent.info((I18NMessage3P)messages[3],
                                                  parameters[3][0],
                                                  parameters[3][1],
                                                  parameters[3][2]),
                                    levels[levelIndex],
                                    exceptions[exceptionIndex],
                                    messages[3],
                                    parameters[3]);
                    } else if(levels[levelIndex].equals(Level.WARN)) {
                        verifyEvent(LogEvent.warn((I18NMessage3P)messages[3],
                                                  parameters[3][0],
                                                  parameters[3][1],
                                                  parameters[3][2]),
                                    levels[levelIndex],
                                    exceptions[exceptionIndex],
                                    messages[3],
                                    parameters[3]);
                    } else if(levels[levelIndex].equals(Level.ERROR)) {
                        verifyEvent(LogEvent.error((I18NMessage3P)messages[3],
                                                   parameters[3][0],
                                                   parameters[3][1],
                                                   parameters[3][2]),
                                    levels[levelIndex],
                                    exceptions[exceptionIndex],
                                    messages[3],
                                    parameters[3]);
                    }
                } else {
                    verifyEvent(LogEvent.event((I18NMessage3P)messages[3],
                                               exceptions[exceptionIndex],
                                               levels[levelIndex],
                                               parameters[3][0],
                                               parameters[3][1],
                                               parameters[3][2]),
                                levels[levelIndex],
                                exceptions[exceptionIndex],
                                messages[3],
                                parameters[3]);
                    if(levels[levelIndex].equals(Level.DEBUG)) {
                        verifyEvent(LogEvent.debug((I18NMessage3P)messages[3],
                                                   exceptions[exceptionIndex],
                                                   parameters[3][0],
                                                   parameters[3][1],
                                                   parameters[3][2]),
                                    levels[levelIndex],
                                    exceptions[exceptionIndex],
                                    messages[3],
                                    parameters[3]);
                    } else if(levels[levelIndex].equals(Level.INFO)) {
                        verifyEvent(LogEvent.info((I18NMessage3P)messages[3],
                                                  exceptions[exceptionIndex],
                                                  parameters[3][0],
                                                  parameters[3][1],
                                                  parameters[3][2]),
                                    levels[levelIndex],
                                    exceptions[exceptionIndex],
                                    messages[3],
                                    parameters[3]);
                    } else if(levels[levelIndex].equals(Level.WARN)) {
                        verifyEvent(LogEvent.warn((I18NMessage3P)messages[3],
                                                  exceptions[exceptionIndex],
                                                  parameters[3][0],
                                                  parameters[3][1],
                                                  parameters[3][2]),
                                    levels[levelIndex],
                                    exceptions[exceptionIndex],
                                    messages[3],
                                    parameters[3]);
                    } else if(levels[levelIndex].equals(Level.ERROR)) {
                        verifyEvent(LogEvent.error((I18NMessage3P)messages[3],
                                                   exceptions[exceptionIndex],
                                                   parameters[3][0],
                                                   parameters[3][1],
                                                   parameters[3][2]),
                                    levels[levelIndex],
                                    exceptions[exceptionIndex],
                                    messages[3],
                                    parameters[3]);
                    }
                }
                if(exceptions[exceptionIndex] == null) {
                    verifyEvent(LogEvent.event((I18NMessage4P)messages[4],
                                               levels[levelIndex],
                                               parameters[4][0],
                                               parameters[4][1],
                                               parameters[4][2],
                                               parameters[4][3]),
                                levels[levelIndex],
                                exceptions[exceptionIndex],
                                messages[4],
                                parameters[4]);
                    if(levels[levelIndex].equals(Level.DEBUG)) {
                        verifyEvent(LogEvent.debug((I18NMessage4P)messages[4],
                                                   parameters[4][0],
                                                   parameters[4][1],
                                                   parameters[4][2],
                                                   parameters[4][3]),
                                    levels[levelIndex],
                                    exceptions[exceptionIndex],
                                    messages[4],
                                    parameters[4]);
                    } else if(levels[levelIndex].equals(Level.INFO)) {
                        verifyEvent(LogEvent.info((I18NMessage4P)messages[4],
                                                  parameters[4][0],
                                                  parameters[4][1],
                                                  parameters[4][2],
                                                  parameters[4][3]),
                                    levels[levelIndex],
                                    exceptions[exceptionIndex],
                                    messages[4],
                                    parameters[4]);
                    } else if(levels[levelIndex].equals(Level.WARN)) {
                        verifyEvent(LogEvent.warn((I18NMessage4P)messages[4],
                                                  parameters[4][0],
                                                  parameters[4][1],
                                                  parameters[4][2],
                                                  parameters[4][3]),
                                    levels[levelIndex],
                                    exceptions[exceptionIndex],
                                    messages[4],
                                    parameters[4]);
                    } else if(levels[levelIndex].equals(Level.ERROR)) {
                        verifyEvent(LogEvent.error((I18NMessage4P)messages[4],
                                                   parameters[4][0],
                                                   parameters[4][1],
                                                   parameters[4][2],
                                                   parameters[4][3]),
                                    levels[levelIndex],
                                    exceptions[exceptionIndex],
                                    messages[4],
                                    parameters[4]);
                    }
                } else {
                    verifyEvent(LogEvent.event((I18NMessage4P)messages[4],
                                               exceptions[exceptionIndex],
                                               levels[levelIndex],
                                               parameters[4][0],
                                               parameters[4][1],
                                               parameters[4][2],
                                               parameters[4][3]),
                                levels[levelIndex],
                                exceptions[exceptionIndex],
                                messages[4],
                                parameters[4]);
                    if(levels[levelIndex].equals(Level.DEBUG)) {
                        verifyEvent(LogEvent.debug((I18NMessage4P)messages[4],
                                                   exceptions[exceptionIndex],
                                                   parameters[4][0],
                                                   parameters[4][1],
                                                   parameters[4][2],
                                                   parameters[4][3]),
                                    levels[levelIndex],
                                    exceptions[exceptionIndex],
                                    messages[4],
                                    parameters[4]);
                    } else if(levels[levelIndex].equals(Level.INFO)) {
                        verifyEvent(LogEvent.info((I18NMessage4P)messages[4],
                                                  exceptions[exceptionIndex],
                                                  parameters[4][0],
                                                  parameters[4][1],
                                                  parameters[4][2],
                                                  parameters[4][3]),
                                    levels[levelIndex],
                                    exceptions[exceptionIndex],
                                    messages[4],
                                    parameters[4]);
                    } else if(levels[levelIndex].equals(Level.WARN)) {
                        verifyEvent(LogEvent.warn((I18NMessage4P)messages[4],
                                                  exceptions[exceptionIndex],
                                                  parameters[4][0],
                                                  parameters[4][1],
                                                  parameters[4][2],
                                                  parameters[4][3]),
                                    levels[levelIndex],
                                    exceptions[exceptionIndex],
                                    messages[4],
                                    parameters[4]);
                    } else if(levels[levelIndex].equals(Level.ERROR)) {
                        verifyEvent(LogEvent.error((I18NMessage4P)messages[4],
                                                   exceptions[exceptionIndex],
                                                   parameters[4][0],
                                                   parameters[4][1],
                                                   parameters[4][2],
                                                   parameters[4][3]),
                                    levels[levelIndex],
                                    exceptions[exceptionIndex],
                                    messages[4],
                                    parameters[4]);
                    }
                }
                if(exceptions[exceptionIndex] == null) {
                    verifyEvent(LogEvent.event((I18NMessage5P)messages[5],
                                               levels[levelIndex],
                                               parameters[5][0],
                                               parameters[5][1],
                                               parameters[5][2],
                                               parameters[5][3],
                                               parameters[5][4]),
                                levels[levelIndex],
                                exceptions[exceptionIndex],
                                messages[5],
                                parameters[5]);
                    if(levels[levelIndex].equals(Level.DEBUG)) {
                        verifyEvent(LogEvent.debug((I18NMessage5P)messages[5],
                                                   parameters[5][0],
                                                   parameters[5][1],
                                                   parameters[5][2],
                                                   parameters[5][3],
                                                   parameters[5][4]),
                                    levels[levelIndex],
                                    exceptions[exceptionIndex],
                                    messages[5],
                                    parameters[5]);
                    } else if(levels[levelIndex].equals(Level.INFO)) {
                        verifyEvent(LogEvent.info((I18NMessage5P)messages[5],
                                                  parameters[5][0],
                                                  parameters[5][1],
                                                  parameters[5][2],
                                                  parameters[5][3],
                                                  parameters[5][4]),
                                    levels[levelIndex],
                                    exceptions[exceptionIndex],
                                    messages[5],
                                    parameters[5]);
                    } else if(levels[levelIndex].equals(Level.WARN)) {
                        verifyEvent(LogEvent.warn((I18NMessage5P)messages[5],
                                                  parameters[5][0],
                                                  parameters[5][1],
                                                  parameters[5][2],
                                                  parameters[5][3],
                                                  parameters[5][4]),
                                    levels[levelIndex],
                                    exceptions[exceptionIndex],
                                    messages[5],
                                    parameters[5]);
                    } else if(levels[levelIndex].equals(Level.ERROR)) {
                        verifyEvent(LogEvent.error((I18NMessage5P)messages[5],
                                                   parameters[5][0],
                                                   parameters[5][1],
                                                   parameters[5][2],
                                                   parameters[5][3],
                                                   parameters[5][4]),
                                    levels[levelIndex],
                                    exceptions[exceptionIndex],
                                    messages[5],
                                    parameters[5]);
                    }
                } else {
                    verifyEvent(LogEvent.event((I18NMessage5P)messages[5],
                                               exceptions[exceptionIndex],
                                               levels[levelIndex],
                                               parameters[5][0],
                                               parameters[5][1],
                                               parameters[5][2],
                                               parameters[5][3],
                                               parameters[5][4]),
                                levels[levelIndex],
                                exceptions[exceptionIndex],
                                messages[5],
                                parameters[5]);
                    if(levels[levelIndex].equals(Level.DEBUG)) {
                        verifyEvent(LogEvent.debug((I18NMessage5P)messages[5],
                                                   exceptions[exceptionIndex],
                                                   parameters[5][0],
                                                   parameters[5][1],
                                                   parameters[5][2],
                                                   parameters[5][3],
                                                   parameters[5][4]),
                                    levels[levelIndex],
                                    exceptions[exceptionIndex],
                                    messages[5],
                                    parameters[5]);
                    } else if(levels[levelIndex].equals(Level.INFO)) {
                        verifyEvent(LogEvent.info((I18NMessage5P)messages[5],
                                                  exceptions[exceptionIndex],
                                                  parameters[5][0],
                                                  parameters[5][1],
                                                  parameters[5][2],
                                                  parameters[5][3],
                                                  parameters[5][4]),
                                    levels[levelIndex],
                                    exceptions[exceptionIndex],
                                    messages[5],
                                    parameters[5]);
                    } else if(levels[levelIndex].equals(Level.WARN)) {
                        verifyEvent(LogEvent.warn((I18NMessage5P)messages[5],
                                                  exceptions[exceptionIndex],
                                                  parameters[5][0],
                                                  parameters[5][1],
                                                  parameters[5][2],
                                                  parameters[5][3],
                                                  parameters[5][4]),
                                    levels[levelIndex],
                                    exceptions[exceptionIndex],
                                    messages[5],
                                    parameters[5]);
                    } else if(levels[levelIndex].equals(Level.ERROR)) {
                        verifyEvent(LogEvent.error((I18NMessage5P)messages[5],
                                                   exceptions[exceptionIndex],
                                                   parameters[5][0],
                                                   parameters[5][1],
                                                   parameters[5][2],
                                                   parameters[5][3],
                                                   parameters[5][4]),
                                    levels[levelIndex],
                                    exceptions[exceptionIndex],
                                    messages[5],
                                    parameters[5]);
                    }
                }
                if(exceptions[exceptionIndex] == null) {
                    verifyEvent(LogEvent.event((I18NMessage6P)messages[6],
                                               levels[levelIndex],
                                               parameters[6][0],
                                               parameters[6][1],
                                               parameters[6][2],
                                               parameters[6][3],
                                               parameters[6][4],
                                               parameters[6][5]),
                                levels[levelIndex],
                                exceptions[exceptionIndex],
                                messages[6],
                                parameters[6]);
                    if(levels[levelIndex].equals(Level.DEBUG)) {
                        verifyEvent(LogEvent.debug((I18NMessage6P)messages[6],
                                                   parameters[6][0],
                                                   parameters[6][1],
                                                   parameters[6][2],
                                                   parameters[6][3],
                                                   parameters[6][4],
                                                   parameters[6][5]),
                                    levels[levelIndex],
                                    exceptions[exceptionIndex],
                                    messages[6],
                                    parameters[6]);
                    } else if(levels[levelIndex].equals(Level.INFO)) {
                        verifyEvent(LogEvent.info((I18NMessage6P)messages[6],
                                                  parameters[6][0],
                                                  parameters[6][1],
                                                  parameters[6][2],
                                                  parameters[6][3],
                                                  parameters[6][4],
                                                  parameters[6][5]),
                                    levels[levelIndex],
                                    exceptions[exceptionIndex],
                                    messages[6],
                                    parameters[6]);
                    } else if(levels[levelIndex].equals(Level.WARN)) {
                        verifyEvent(LogEvent.warn((I18NMessage6P)messages[6],
                                                  parameters[6][0],
                                                  parameters[6][1],
                                                  parameters[6][2],
                                                  parameters[6][3],
                                                  parameters[6][4],
                                                  parameters[6][5]),
                                    levels[levelIndex],
                                    exceptions[exceptionIndex],
                                    messages[6],
                                    parameters[6]);
                    } else if(levels[levelIndex].equals(Level.ERROR)) {
                        verifyEvent(LogEvent.error((I18NMessage6P)messages[6],
                                                   parameters[6][0],
                                                   parameters[6][1],
                                                   parameters[6][2],
                                                   parameters[6][3],
                                                   parameters[6][4],
                                                   parameters[6][5]),
                                    levels[levelIndex],
                                    exceptions[exceptionIndex],
                                    messages[6],
                                    parameters[6]);
                    }
                } else {
                    verifyEvent(LogEvent.event((I18NMessage6P)messages[6],
                                               exceptions[exceptionIndex],
                                               levels[levelIndex],
                                               parameters[6][0],
                                               parameters[6][1],
                                               parameters[6][2],
                                               parameters[6][3],
                                               parameters[6][4],
                                               parameters[6][5]),
                                levels[levelIndex],
                                exceptions[exceptionIndex],
                                messages[6],
                                parameters[6]);
                    if(levels[levelIndex].equals(Level.DEBUG)) {
                        verifyEvent(LogEvent.debug((I18NMessage6P)messages[6],
                                                   exceptions[exceptionIndex],
                                                   parameters[6][0],
                                                   parameters[6][1],
                                                   parameters[6][2],
                                                   parameters[6][3],
                                                   parameters[6][4],
                                                   parameters[6][5]),
                                    levels[levelIndex],
                                    exceptions[exceptionIndex],
                                    messages[6],
                                    parameters[6]);
                    } else if(levels[levelIndex].equals(Level.INFO)) {
                        verifyEvent(LogEvent.info((I18NMessage6P)messages[6],
                                                  exceptions[exceptionIndex],
                                                  parameters[6][0],
                                                  parameters[6][1],
                                                  parameters[6][2],
                                                  parameters[6][3],
                                                  parameters[6][4],
                                                  parameters[6][5]),
                                    levels[levelIndex],
                                    exceptions[exceptionIndex],
                                    messages[6],
                                    parameters[6]);
                    } else if(levels[levelIndex].equals(Level.WARN)) {
                        verifyEvent(LogEvent.warn((I18NMessage6P)messages[6],
                                                  exceptions[exceptionIndex],
                                                  parameters[6][0],
                                                  parameters[6][1],
                                                  parameters[6][2],
                                                  parameters[6][3],
                                                  parameters[6][4],
                                                  parameters[6][5]),
                                    levels[levelIndex],
                                    exceptions[exceptionIndex],
                                    messages[6],
                                    parameters[6]);
                    } else if(levels[levelIndex].equals(Level.ERROR)) {
                        verifyEvent(LogEvent.error((I18NMessage6P)messages[6],
                                                   exceptions[exceptionIndex],
                                                   parameters[6][0],
                                                   parameters[6][1],
                                                   parameters[6][2],
                                                   parameters[6][3],
                                                   parameters[6][4],
                                                   parameters[6][5]),
                                    levels[levelIndex],
                                    exceptions[exceptionIndex],
                                    messages[6],
                                    parameters[6]);
                    }
                }
                if(exceptions[exceptionIndex] == null) {
                    verifyEvent(LogEvent.event((I18NMessageNP)messages[7],
                                               levels[levelIndex],
                                               parameters[7][0],
                                               parameters[7][1],
                                               parameters[7][2],
                                               parameters[7][3],
                                               parameters[7][4],
                                               parameters[7][5],
                                               parameters[7][6]),
                                levels[levelIndex],
                                exceptions[exceptionIndex],
                                messages[7],
                                parameters[7]);
                    if(levels[levelIndex].equals(Level.DEBUG)) {
                        verifyEvent(LogEvent.debug((I18NMessageNP)messages[7],
                                                   parameters[7][0],
                                                   parameters[7][1],
                                                   parameters[7][2],
                                                   parameters[7][3],
                                                   parameters[7][4],
                                                   parameters[7][5],
                                                   parameters[7][6]),
                                    levels[levelIndex],
                                    exceptions[exceptionIndex],
                                    messages[7],
                                    parameters[7]);
                    } else if(levels[levelIndex].equals(Level.INFO)) {
                        verifyEvent(LogEvent.info((I18NMessageNP)messages[7],
                                                  parameters[7][0],
                                                  parameters[7][1],
                                                  parameters[7][2],
                                                  parameters[7][3],
                                                  parameters[7][4],
                                                  parameters[7][5],
                                                  parameters[7][6]),
                                    levels[levelIndex],
                                    exceptions[exceptionIndex],
                                    messages[7],
                                    parameters[7]);
                    } else if(levels[levelIndex].equals(Level.WARN)) {
                        verifyEvent(LogEvent.warn((I18NMessageNP)messages[7],
                                                  parameters[7][0],
                                                  parameters[7][1],
                                                  parameters[7][2],
                                                  parameters[7][3],
                                                  parameters[7][4],
                                                  parameters[7][5],
                                                  parameters[7][6]),
                                    levels[levelIndex],
                                    exceptions[exceptionIndex],
                                    messages[7],
                                    parameters[7]);
                    } else if(levels[levelIndex].equals(Level.ERROR)) {
                        verifyEvent(LogEvent.error((I18NMessageNP)messages[7],
                                                   parameters[7][0],
                                                   parameters[7][1],
                                                   parameters[7][2],
                                                   parameters[7][3],
                                                   parameters[7][4],
                                                   parameters[7][5],
                                                   parameters[7][6]),
                                    levels[levelIndex],
                                    exceptions[exceptionIndex],
                                    messages[7],
                                    parameters[7]);
                    }
                } else {
                    verifyEvent(LogEvent.event((I18NMessageNP)messages[7],
                                               exceptions[exceptionIndex],
                                               levels[levelIndex],
                                               parameters[7][0],
                                               parameters[7][1],
                                               parameters[7][2],
                                               parameters[7][3],
                                               parameters[7][4],
                                               parameters[7][5],
                                               parameters[7][6]),
                                levels[levelIndex],
                                exceptions[exceptionIndex],
                                messages[7],
                                parameters[7]);
                }
            }
        }
    }
    /**
     * Verifies that the event created contains the expected information.
     *
     * @param inActualEvent a <code>LogEvent</code> value containing the event to verify
     * @param inExpectedLevel a <code>Priority</code> value containing the expected priority
     * @param inException a <code>Throwable</code> value containing the expected exception or <code>null</code> for none
     * @param inExpectedMessage an <code>I18NMessage</code> value containing the expected message
     * @param inExpectedParameters a <code>Serializable[]</code> value containing the expected parameters
     */
    public static void verifyEvent(LogEvent inActualEvent,
                                   Level inExpectedLevel,
                                   Throwable inException,
                                   I18NMessage inExpectedMessage,
                                   Serializable...inExpectedParameters)
        throws Exception
    {
        assertEquals(inExpectedLevel,
                     inActualEvent.getLevel());
        assertEquals(inException,
                     inActualEvent.getException());
        String messageText = inExpectedMessage.getMessageProvider().getText(inExpectedMessage,
                                                                            (Object[])inExpectedParameters);
        assertEquals(messageText,
                     inActualEvent.getMessage());
        // serialize event
        ByteArrayOutputStream bos = new ByteArrayOutputStream() ;
        ObjectOutput out = new ObjectOutputStream(bos);
        out.writeObject(inActualEvent);
        out.close();
        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        ObjectInput in = new ObjectInputStream(bis);
        LogEvent serializedEvent = (LogEvent)in.readObject();
        assertEquals(inExpectedLevel,
                     serializedEvent.getLevel());
        if(inException == null) {
            assertNull(serializedEvent.getException());
        } else {
            assertEquals(inException.getMessage(),
                         serializedEvent.getException().getMessage());
        }
        assertEquals(messageText,
                     serializedEvent.getMessage());
    }
}
