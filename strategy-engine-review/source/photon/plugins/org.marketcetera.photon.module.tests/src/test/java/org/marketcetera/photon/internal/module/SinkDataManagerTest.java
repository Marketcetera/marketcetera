package org.marketcetera.photon.internal.module;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.marketcetera.core.notifications.INotification;
import org.marketcetera.core.notifications.Notification;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.photon.module.ISinkDataHandler;


/* $License$ */

/**
 * Test {@link SinkDataManager}.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
public class SinkDataManagerTest {

	private SinkDataManager fixture;
	
	@Before
	public void setup() {
		fixture = new SinkDataManager();
	}
	
	/**
	 * Tests a single handler.
	 */
	@Test
	public void simpleTest() {
		ISinkDataHandler mockHandler = mock(ISinkDataHandler.class);
		fixture.register(mockHandler, String.class);
		DataFlowID id = new DataFlowID("1");
		// send a string, should get handled
		String data = "ABC";
		fixture.receivedData(id, data);
		verify(mockHandler).receivedData(id, data);
		// send and integer, should be ignored
		Integer data2 = 1; 
		fixture.receivedData(id, data2);
		verifyNoMoreInteractions(mockHandler);
		// unregister and send a string, should be ignored
		fixture.unregister(mockHandler);
		fixture.receivedData(id, data);
		verifyNoMoreInteractions(mockHandler);
	}
	
	/**
	 * Multiplexing test.
	 */
	@Test
	public void multiTest() {
		ISinkDataHandler mockHandler1 = mock(ISinkDataHandler.class);
		ISinkDataHandler mockHandler2 = mock(ISinkDataHandler.class);
		ISinkDataHandler mockHandler3 = mock(ISinkDataHandler.class);
		ISinkDataHandler mockHandler4 = mock(ISinkDataHandler.class);
		fixture.register(mockHandler1, String.class);
		fixture.register(mockHandler2, Integer.class, Long.class);
		fixture.register(mockHandler3, BigDecimal.class);
		fixture.register(mockHandler4, INotification.class);
		DataFlowID id = new DataFlowID("1");
		String data1 = "ABC";
		Integer data2 = 1;
		BigDecimal data3 = BigDecimal.TEN;
		Long data4 = 1L;
		INotification data5 = mock(Notification.class);
		// send all data
		fixture.receivedData(id, data1);
		fixture.receivedData(id, data2);
		fixture.receivedData(id, data3);
		fixture.receivedData(id, data4);
		fixture.receivedData(id, data5);
		verify(mockHandler1).receivedData(id, data1);
		verify(mockHandler2).receivedData(id, data2);
		verify(mockHandler2).receivedData(id, data4);
		verify(mockHandler3).receivedData(id, data3);
		verify(mockHandler4).receivedData(id, data5);
		// add integer to mock 1 and remove from mock 2
		fixture.register(mockHandler1, Integer.class);
		fixture.unregister(mockHandler2, Integer.class);
		fixture.receivedData(id, data2);
		verify(mockHandler1).receivedData(id, data2);
		verifyNoMoreInteractions(mockHandler1, mockHandler2, mockHandler3, mockHandler4);
	}
	
	/**
	 * Default test.
	 */
	@Test
	public void defaultHandler() {
		ISinkDataHandler mockHandler = mock(ISinkDataHandler.class);
		ISinkDataHandler mockDefault = mock(ISinkDataHandler.class);
		fixture.registerDefault(mockDefault);
		DataFlowID id = new DataFlowID("1");
		Integer data = 1;
		// send data, verify handled by default
		fixture.receivedData(id, data);
		verify(mockDefault).receivedData(id, data);
		verifyNoMoreInteractions(mockHandler, mockDefault);
		// register a integer handler and send again, verify not handled by default
		fixture.register(mockHandler, Integer.class);
		fixture.receivedData(id, data);
		verify(mockHandler).receivedData(id, data);
		verifyNoMoreInteractions(mockHandler, mockDefault);
		// unregister integer handler, verify again handled by default
		fixture.unregister(mockHandler, Integer.class);
		fixture.receivedData(id, data);
		verify(mockDefault, times(2)).receivedData(id, data);
		verifyNoMoreInteractions(mockHandler, mockDefault);
		// unregister wrong default, verify default still handled
		fixture.unregisterDefault(mockHandler);
		fixture.receivedData(id, data);
		verify(mockDefault, times(3)).receivedData(id, data);
		verifyNoMoreInteractions(mockHandler, mockDefault);
		// unregister default, verify not handled
		fixture.unregisterDefault(mockDefault);
		fixture.receivedData(id, data);
		verifyNoMoreInteractions(mockHandler, mockDefault);
	}
	
	/**
	 * Test error handling.
	 */
	@Test
	public void invalid() throws Exception {
		// register
		new ExpectedFailure<IllegalArgumentException>(null) {
			@Override
			protected void run() throws Exception {
				fixture.register(null, String.class);
			}
		};
		new ExpectedFailure<IllegalArgumentException>(null) {
			@Override
			protected void run() throws Exception {
				fixture.register(mock(ISinkDataHandler.class), (Class<?>) null);
			}
		};
		new ExpectedFailure<IllegalArgumentException>(null) {
			@Override
			protected void run() throws Exception {
				fixture.register(mock(ISinkDataHandler.class), (Class<?>[]) null);
			}
		};
		new ExpectedFailure<IllegalArgumentException>(null) {
			@Override
			protected void run() throws Exception {
				fixture.register(mock(ISinkDataHandler.class), null, String.class);
			}
		};
		new ExpectedFailure<IllegalArgumentException>(null) {
			@Override
			protected void run() throws Exception {
				fixture.register(mock(ISinkDataHandler.class), new Class<?>[] {String.class, null});
			}
		};
		// unregister
		new ExpectedFailure<IllegalArgumentException>(null) {
			@Override
			protected void run() throws Exception {
				fixture.unregister(null);
			}
		};
		new ExpectedFailure<IllegalArgumentException>(null) {
			@Override
			protected void run() throws Exception {
				fixture.unregister(null, String.class);
			}
		};
		new ExpectedFailure<IllegalArgumentException>(null) {
			@Override
			protected void run() throws Exception {
				fixture.unregister(mock(ISinkDataHandler.class), (Class<?>) null);
			}
		};
		new ExpectedFailure<IllegalArgumentException>(null) {
			@Override
			protected void run() throws Exception {
				fixture.unregister(mock(ISinkDataHandler.class), (Class<?>[]) null);
			}
		};
		new ExpectedFailure<IllegalArgumentException>(null) {
			@Override
			protected void run() throws Exception {
				fixture.unregister(mock(ISinkDataHandler.class), null, String.class);
			}
		};
		new ExpectedFailure<IllegalArgumentException>(null) {
			@Override
			protected void run() throws Exception {
				fixture.unregister(mock(ISinkDataHandler.class), new Class<?>[] {String.class, null});
			}
		};
		// register default
		new ExpectedFailure<IllegalArgumentException>(null) {
			@Override
			protected void run() throws Exception {
				fixture.registerDefault(null);
			}
		};
		new ExpectedFailure<IllegalStateException>(null) {
			@Override
			protected void run() throws Exception {
				fixture.registerDefault(mock(ISinkDataHandler.class));
				fixture.registerDefault(mock(ISinkDataHandler.class));
			}
		};
		// unregister default
		new ExpectedFailure<IllegalArgumentException>(null) {
			@Override
			protected void run() throws Exception {
				fixture.unregisterDefault(null);
			}
		};
	}
}
