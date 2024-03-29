package org.marketcetera.core;

import static org.junit.Assert.fail;

/**
 * A wrapper around a piece of code that we expect to fail and throw an exception.
 * We wrap the call, capture the exception and verify the excpetion class is as expected.
 * If the "contains" string is provided we verity that it matches the excepion output as well.
 *
 * <PRE>
 *  (new ExpectedTestFailure(OrderParsingException.class) {
 *    protected void execute() throws OrderParsingException {
 *       &lt;... Code throwing exception goes here ... &gt;
 *  }}).run();
 * </PRE>
 *
 * @author Toli Kuznets
 * @version $Id$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public abstract class ExpectedTestFailure
{
    private String mContains;
    private Class<?> mThrowable;

    public ExpectedTestFailure(Class<?> inThrowable) {
        this(inThrowable, null);
    }

    public ExpectedTestFailure(Class<?> inThrowable, String inContains)
    {
        mThrowable = inThrowable;
        mContains = inContains;
    }

    /** Subclasses must override this method with an implementation that
     * throws their expected error
     * @throws Throwable if an error occurs
     */
    protected abstract void execute() throws Throwable;

    /** 
     * Executes the code that was implemented in @link {execute()} method.
     *
     * @return a <code>Throwable</code> value
     */
    public Throwable run()
    {
        try {
            execute();
        } catch(Throwable error) {
            validateError(error);
            return error;
        }
        fail("Expected an error but no exception was thrown"); //$NON-NLS-1$
        return null;
    }

    /** Validate the passed-in throwable against the class that we expected to find
     * The message of the passed in trowable is validated againt the expected message
     * if there is one
     *
     * If we are expecting a message but the incoming exception.getMessage() doesn't contain it, 
     * also check exception.toString() as well - for the case of exceptions that aren't
     * constructed correctly
     *
     * @param inError throwable to validate
     */
    protected void validateError(Throwable inError)
    {
        if ((mThrowable!=null) &&
            (!mThrowable.isAssignableFrom(inError.getClass()))) {
            fail("Thrown throwable was of the wrong class: "+ inError.getClass()+": "+inError);
        }
        if ((mContains!=null) &&
            (((inError.getMessage()==null) ||
             (inError.getMessage().indexOf(mContains)==-1))) &&
            (inError.toString().indexOf(mContains) == -1)) {
            fail("Thrown throwable contained incorrect message: "+ inError.getMessage()+": "+inError);
        }
    }
}
