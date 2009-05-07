package org.marketcetera.strategy;

import static org.junit.Assume.assumeTrue;
import static org.marketcetera.strategy.Language.JAVA;

import java.io.File;

import org.junit.Test;

import com.sun.jna.Platform;

/* $License$ */

/**
 * Tests Java language support.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.0.0
 */
public class JavaLanguageTest
        extends LanguageTestBase
{
    public static final File JAVA_STRATEGY = new File(StrategyTestBase.SAMPLE_STRATEGY_DIR,
                                                      "JavaStrategy.java");
    public static final String JAVA_STRATEGY_NAME = "JavaStrategy";
    public static final File BAD_STRATEGY = new File(StrategyTestBase.SAMPLE_STRATEGY_DIR,
                                                     "BadStrategy.java");
    public static final String BAD_STRATEGY_NAME = "BadStrategy";
    public static final File WRONG_STRATEGY = new File(StrategyTestBase.SAMPLE_STRATEGY_DIR,
                                                       "WrongClass.java");
    public static final String WRONG_STRATEGY_NAME = "WrongClass";
    public static final File MULTIPLE_CLASS_STRATEGY = new File(StrategyTestBase.SAMPLE_STRATEGY_DIR,
                                                                "StrategyWithHelpers.java");
    public static final String MULTIPLE_CLASS_STRATEGY_NAME = "StrategyWithHelpers";
    public static final File EMPTY_STRATEGY = new File(StrategyTestBase.SAMPLE_STRATEGY_DIR,
                                                       "EmptyStrategy.java");
    public static final String EMPTY_STRATEGY_NAME = "EmptyStrategy";
    public static final File PARAMETER_STRATEGY = new File(StrategyTestBase.SAMPLE_STRATEGY_DIR,
                                                           "ParameterStrategy.java");
    public static final String PARAMETER_STRATEGY_NAME = "ParameterStrategy";
    public static final File SUGGESTION_STRATEGY = new File(StrategyTestBase.SAMPLE_STRATEGY_DIR,
                                                            "SuggestTrades.java");
    public static final String SUGGESTION_STRATEGY_NAME = "SuggestTrades";
    public static final File ORDER_STRATEGY = new File(StrategyTestBase.SAMPLE_STRATEGY_DIR,
                                                       "Orders.java");
    public static final String ORDER_STRATEGY_NAME = "Orders";
    public static final File MESSAGE_STRATEGY = new File(StrategyTestBase.SAMPLE_STRATEGY_DIR,
                                                         "SendMessage.java");
    public static final String MESSAGE_STRATEGY_NAME = "SendMessage";
    public static final File PART1_STRATEGY = new File(StrategyTestBase.SAMPLE_STRATEGY_DIR,
                                                       "Part1.java");
    public static final String PART1_STRATEGY_NAME = "Part1";
    public static final File PART1_REDEFINED_STRATEGY = new File(StrategyTestBase.SAMPLE_STRATEGY_DIR,
                                                                 "redefined" + File.separator + "Part1.java");
    public static final File PART2_STRATEGY = new File(StrategyTestBase.SAMPLE_STRATEGY_DIR,
                                                       "Part2.java");
    public static final String PART2_STRATEGY_NAME = "Part2";
    public static final File PACKAGE_STRATEGY = new File(StrategyTestBase.SAMPLE_STRATEGY_DIR,
                                                         "PackageStrategy.java");
    public static final String PACKAGE_STRATEGY_NAME = "PackageStrategy";
    public static final File EVENT_STRATEGY = new File(StrategyTestBase.SAMPLE_STRATEGY_DIR,
                                                       "SendEvent.java");
    public static final String EVENT_STRATEGY_NAME = "SendEvent";
    public static final File COMBINED_STRATEGY = new File(StrategyTestBase.SAMPLE_STRATEGY_DIR,
                                                          "CombinedRequest.java");
    public static final String COMBINED_STRATEGY_NAME = "CombinedRequest";
    /**
     * Tests that a strategy declared in a package other than the default package works as expected.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void nonDefaultPackage()
        throws Exception
    {
        assumeTrue(!(Platform.isWindows() && getLanguage().equals(JAVA)));
        StrategyCoordinates strategy = getPackageStrategy();
        verifyNullProperties();
        doSuccessfulStartTestNoVerification(createStrategy(strategy.getName(),
                                                           getLanguage(),
                                                           strategy.getFile(),
                                                           null,
                                                           null,
                                                           null));
        verifyPropertyNonNull("onStart");
    }
    /**
     * Tests that a strategy's start and stop loops can be interrupted.
     *
     * @throws Exception
     */
    @Test
    public void interrupt()
        throws Exception
    {
        assumeTrue(!(Platform.isWindows() && getLanguage().equals(JAVA)));
        doInterruptTest(false,
                        false);
        doInterruptTest(false,
                        true);
        doInterruptTest(true,
                        false);
        doInterruptTest(true,
                        true);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.LanguageTestBase#getEmptyStrategy()
     */
    @Override
    protected StrategyCoordinates getEmptyStrategy()
    {
        return StrategyCoordinates.get(EMPTY_STRATEGY,
                                       EMPTY_STRATEGY_NAME);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.LanguageTestBase#getLanguage()
     */
    @Override
    protected Language getLanguage()
    {
        return Language.JAVA;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.LanguageTestBase#getMessageStrategy()
     */
    @Override
    protected StrategyCoordinates getMessageStrategy()
    {
        return StrategyCoordinates.get(MESSAGE_STRATEGY,
                                       MESSAGE_STRATEGY_NAME);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.LanguageTestBase#getParameterStrategy()
     */
    @Override
    protected StrategyCoordinates getParameterStrategy()
    {
        return StrategyCoordinates.get(PARAMETER_STRATEGY,
                                       PARAMETER_STRATEGY_NAME);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.LanguageTestBase#getStrategyCompiles()
     */
    @Override
    protected StrategyCoordinates getStrategyCompiles()
    {
        return StrategyCoordinates.get(JAVA_STRATEGY,
                                       JAVA_STRATEGY_NAME);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.LanguageTestBase#getStrategyMultipleClasses()
     */
    @Override
    protected StrategyCoordinates getStrategyMultipleClasses()
    {
        return StrategyCoordinates.get(MULTIPLE_CLASS_STRATEGY,
                                       MULTIPLE_CLASS_STRATEGY_NAME);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.LanguageTestBase#getStrategyWillNotCompile()
     */
    @Override
    protected StrategyCoordinates getStrategyWillNotCompile()
    {
        return StrategyCoordinates.get(BAD_STRATEGY,
                                       BAD_STRATEGY_NAME);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.LanguageTestBase#getStrategyWrongClass()
     */
    @Override
    protected StrategyCoordinates getStrategyWrongClass()
    {
        return StrategyCoordinates.get(WRONG_STRATEGY,
                                       WRONG_STRATEGY_NAME);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.LanguageTestBase#getSuggestionStrategy()
     */
    @Override
    protected StrategyCoordinates getSuggestionStrategy()
    {
        return StrategyCoordinates.get(SUGGESTION_STRATEGY,
                                       SUGGESTION_STRATEGY_NAME);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.LanguageTestBase#getPart1Strategy()
     */
    @Override
    protected StrategyCoordinates getPart1Strategy()
    {
        return StrategyCoordinates.get(PART1_STRATEGY,
                                       PART1_STRATEGY_NAME);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.LanguageTestBase#getPart2Strategy()
     */
    @Override
    protected StrategyCoordinates getPart2Strategy()
    {
        return StrategyCoordinates.get(PART2_STRATEGY,
                                       PART2_STRATEGY_NAME);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.LanguageTestBase#getPart1RedefinedStrategy()
     */
    @Override
    protected StrategyCoordinates getPart1RedefinedStrategy()
    {
        return StrategyCoordinates.get(PART1_REDEFINED_STRATEGY,
                                       PART1_STRATEGY_NAME);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.LanguageTestBase#getOrdersStrategy()
     */
    @Override
    protected StrategyCoordinates getOrdersStrategy()
    {
        return StrategyCoordinates.get(ORDER_STRATEGY,
                                       ORDER_STRATEGY_NAME);
    }
    /**
     * Gets a strategy not in the default package.
     *
     * @return a <code>StrategyCoordinates</code> value
     */
    private StrategyCoordinates getPackageStrategy()
    {
        return StrategyCoordinates.get(PACKAGE_STRATEGY,
                                       PACKAGE_STRATEGY_NAME);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.LanguageTestBase#getEventStrategy()
     */
    @Override
    protected StrategyCoordinates getEventStrategy()
    {
        return StrategyCoordinates.get(EVENT_STRATEGY,
                                       EVENT_STRATEGY_NAME);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.LanguageTestBase#getCombinedStrategy()
     */
    @Override
    protected StrategyCoordinates getCombinedStrategy()
    {
        return StrategyCoordinates.get(COMBINED_STRATEGY,
                                       COMBINED_STRATEGY_NAME);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.LanguageTestBase#getNotificationCount()
     */
    @Override
    protected int getExpectedCompilationWarningsFor(StrategyCoordinates inStrategy)
    {
        if(inStrategy.getName().equals(JAVA_STRATEGY_NAME)) {
            return 2;
        }
        return 0;
    }
}
