package org.marketcetera.photon.views;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

import org.apache.log4j.BasicConfigurator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.marketcetera.core.instruments.MockUnderlyingSymbolSupport;
import org.marketcetera.messagehistory.ReportHolder;
import org.marketcetera.messagehistory.TradeReportsHistory;
import org.marketcetera.photon.OrderManagerTest;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.messagehistory.FIXMatcher;
import org.marketcetera.photon.test.SWTTestUtil;
import org.marketcetera.photon.ui.IndexedTableViewer;
import org.marketcetera.photon.views.AbstractFIXMessagesView.FilterMatcherEditor;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.trade.MessageCreationException;

import quickfix.Message;
import ca.odell.glazedlists.matchers.Matcher;
import ca.odell.glazedlists.matchers.Matchers;

public abstract class ViewTestBase extends TestCase {

	private IViewPart testView;

	public ViewTestBase(String name) {
		super(name);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		BasicConfigurator.configure();
		waitForJobs();
		if (PhotonPlugin.getDefault().getStockOrderTicketModel() == null){
			PhotonPlugin.getDefault().initOrderTickets();
		}
		testView = PlatformUI.
			getWorkbench().
			getActiveWorkbenchWindow().
			getActivePage().
			showView(getViewID());
		waitForJobs();
	}

	protected abstract String getViewID();

	@Override
	protected void tearDown() throws Exception {
		waitForJobs();
		
		PlatformUI.
			getWorkbench().
			getActiveWorkbenchWindow().
			getActivePage().
			hideView(testView);
		
		super.tearDown();
	}

	/**
     * Waits until the passed block evaluates to <code>true</code> while
     * allowing the system display queue to execute events.
     * 
     * <p>This call will evaluate the block until it it returns <code>true</code>
     * for a maximum of 60 seconds.  After 60 seconds, it will throw an exception.
     * 
     * @param inCondition a <code>Callable&lt;Boolean&gt;</code> value which must evaluate to true or false
     * @throws Exception if an error occurs
     */
	public static void doDelay(Callable<Boolean> inCondition)
        throws Exception {
		SWTTestUtil.conditionalDelay(60, TimeUnit.SECONDS, inCondition);
    }  

	/**
	 * Sleep for a number of milliseconds.
	 */
	protected static void delay(long millis) {
		Display display = Display.getCurrent();
		
		if (display != null){
			SWTTestUtil.delay(millis, TimeUnit.MILLISECONDS);
		} else {
			try	{
				Thread.sleep(millis);
			} catch (InterruptedException ex){
			}
		}
	}

	private void waitForJobs() {
	    SWTTestUtil.waitForJobs(1000, TimeUnit.MILLISECONDS);
	}

	public IViewPart getTestView() {
		return testView;
	}
    protected List<Message> getFilterTestMessages()
    {
        return new ArrayList<Message>();
    }
    protected List<FilterTestCondition> getFilterTestConditions()
    {
        return new ArrayList<FilterTestCondition>();
    }
    protected static class FilterTestCondition
    {
        private final FIXMatcher<String> mMatcher;
        private final int[] mMatchingMessages;
        protected FilterTestCondition(FIXMatcher<String> inMatcher,
                                      int[] inMatchingMessages)
        {
            mMatcher = inMatcher;
            mMatchingMessages = inMatchingMessages;
        }
    }
    protected void addMessage(Message inMessage,
                              TradeReportsHistory inHistory) throws MessageCreationException
    {
        inHistory.addIncomingMessage(OrderManagerTest.createReport(inMessage));
    }
    protected void doFilterTest()
        throws Exception
    {
    	TradeReportsHistory hist = new TradeReportsHistory(FIXVersion.FIX_SYSTEM.getMessageFactory(), new MockUnderlyingSymbolSupport());
        AbstractFIXMessagesView view = (AbstractFIXMessagesView)getTestView();
        view.setInput(hist);
        List<Message> messages = getFilterTestMessages();
        for(Message message : messages) {
            addMessage(message,
                       hist);
        }
        assertEquals(messages.size(),
                     view.getInput().size());
        IndexedTableViewer tableViewer = view.getMessagesViewer();
        Table table = tableViewer.getTable();
        assertEquals(messages.size(),
                     table.getItemCount());
        FilterMatcherEditor filter = view.getFilterMatcherEditor();
        // view is set up with all messages, begin filter tests
        assertEquals(messages.size(),
                     view.getInput().size());
        // get the test conditions
        List<FilterTestCondition> conditions = getFilterTestConditions();
        // execute the test conditions
        int testConditionCounter = 0;
        for(FilterTestCondition condition : conditions) {
            // construct the expected results
            List<ReportHolder> expectedResults = new ArrayList<ReportHolder>();
            for(int index : condition.mMatchingMessages) {
                expectedResults.add(view.getInput().get(index));
            }
            // implement the filter
            filter.setMatcher(condition.mMatcher);
            // make sure messages in the view are the ones expected in the test condition
            assertEquals("Test condition " + testConditionCounter + " failed",
                         expectedResults.size(),
                         view.getInput().size());
            for(ReportHolder expectedMessage : expectedResults) {
                boolean found = false;
                for(ReportHolder actualMessage : view.getInput()) {
                    if(actualMessage.getMessage().equals(expectedMessage.getMessage())) {
                        found = true;
                        break;
                    }
                }
                if(!found) {
                    fail("Test condition " + testConditionCounter + " failed");
                }
            }
            // reset the filter and make sure they all show up again
            Matcher<ReportHolder> trueMatcher = Matchers.trueMatcher();
            filter.setMatcher(trueMatcher);
            assertEquals("Test condition " + testConditionCounter + " failed",
                         messages.size(),
                         view.getInput().size());
            testConditionCounter += 1;
        }
    }
}
