package org.marketcetera.photon.views;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import junit.framework.TestCase;

import org.apache.log4j.BasicConfigurator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.marketcetera.messagehistory.FIXMessageHistory;
import org.marketcetera.messagehistory.MessageHolder;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.messagehistory.FIXMatcher;
import org.marketcetera.photon.ui.IndexedTableViewer;
import org.marketcetera.photon.views.AbstractFIXMessagesView.FilterMatcherEditor;
import org.marketcetera.quickfix.FIXVersion;

import quickfix.Message;
import ca.odell.glazedlists.impl.matchers.TrueMatcher;

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
        throws Exception
    {
        int counter = 0;
        try {
            Boolean result = inCondition.call();
            while(!result &&
                    counter < 600) {
                delay(100);
                result = inCondition.call();
                counter += 1;
            }
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Unexpected exception");
        } finally {
            if(counter >= 600) {
                fail("Timeout waiting for async condition");
            }
        }
    }   

	protected static void delay(long millis) {
		Display display = Display.getCurrent();
		
		if (display != null){
			long endTimeMillis = 
				System.currentTimeMillis() + millis;
			while (System.currentTimeMillis() < endTimeMillis)
			{
				if (!display.readAndDispatch())
				{
					display.sleep();
				}
				display.update();
			}
		} else {
			try 
			{
				Thread.sleep(millis);
			} catch (InterruptedException ex){
			}
		}
	}

	public static void waitForJobs() {
		while (Platform.getJobManager().currentJob() != null){
			delay(1000);
		}
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
                              FIXMessageHistory inHistory)
    {
        inHistory.addOutgoingMessage(inMessage);
    }
    protected void doFilterTest()
        throws Exception
    {
        FIXMessageHistory hist = new FIXMessageHistory(FIXVersion.FIX42.getMessageFactory());
        AbstractFIXMessagesView view = (AbstractFIXMessagesView)getTestView();
        view.setInput(hist);
        List<Message> messages = getFilterTestMessages();
        for(Message message : messages) {
            addMessage(message,
                       hist);
        }
        assertEquals(messages.size(),
                     view.getMessageList(hist).size());
        IndexedTableViewer tableViewer = view.getMessagesViewer();
        Table table = tableViewer.getTable();
        assertEquals(messages.size(),
                     table.getItemCount());
        FilterMatcherEditor filter = view.getFilterMatcherEditor();
        // view is set up with all messages, begin filter tests
        assertEquals(messages.size(),
                     view.getMessageList(hist).size());
        // get the test conditions
        List<FilterTestCondition> conditions = getFilterTestConditions();
        // execute the test conditions
        int testConditionCounter = 0;
        for(FilterTestCondition condition : conditions) {
            // construct the expected results
            List<MessageHolder> expectedResults = new ArrayList<MessageHolder>();
            for(int index : condition.mMatchingMessages) {
                expectedResults.add(view.getMessageList(hist).get(index));
            }
            // implement the filter
            filter.setMatcher(condition.mMatcher);
            // make sure messages in the view are the ones expected in the test condition
            assertEquals("Test condition " + testConditionCounter + " failed",
                         expectedResults.size(),
                         view.getMessageList(hist).size());
            for(MessageHolder expectedMessage : expectedResults) {
                boolean found = false;
                for(MessageHolder actualMessage : view.getMessageList(hist)) {
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
            filter.setMatcher(new TrueMatcher<MessageHolder>());
            assertEquals("Test condition " + testConditionCounter + " failed",
                         messages.size(),
                         view.getMessageList(hist).size());
            testConditionCounter += 1;
        }
    }
}
