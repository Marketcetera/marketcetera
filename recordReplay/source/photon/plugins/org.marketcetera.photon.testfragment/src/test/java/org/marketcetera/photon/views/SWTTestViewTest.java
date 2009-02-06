package org.marketcetera.photon.views;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.marketcetera.photon.ui.databinding.ObservableEventList;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;


public class SWTTestViewTest extends ViewTestBase {

	private DataBindingContext context;

	public SWTTestViewTest(String name) {
		super(name);
	}

	@Override
	protected String getViewID() {
		return SWTTestView.ID;
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		context = new DataBindingContext();
	}
	
	@Override
	protected void tearDown() throws Exception {
		context.dispose();
	}
	
	public void testComboValues() throws Exception {
		WritableList list = new WritableList();
		SWTTestView view = (SWTTestView) getTestView();
		Combo combo = view.getCombo();
		
		context.bindList(
				SWTObservables.observeItems(combo),
				list,
				null, null);
		assertEquals(0, combo.getItemCount());
		list.add("ASDF");
		assertEquals(1, combo.getItemCount());
	}
	
	public void testEventListComboValues() throws Exception {
		SWTTestView view = (SWTTestView) getTestView();
		Combo combo = view.getCombo();
		combo.removeAll();
		assertEquals(0, combo.getItemCount());
		
		EventList<String> itemEventList = new BasicEventList<String>();
		ObservableEventList observableList = new ObservableEventList(itemEventList, String.class);
		context.bindList(
				SWTObservables.observeItems(combo),
				observableList,
				null, null);
		
		assertEquals(0, combo.getItemCount());
		itemEventList.add("QWER");
		assertEquals(1, combo.getItemCount());
		assertEquals("QWER", combo.getItem(0));
	}
	
	public void testThreadedEventListComboValues() throws Exception {
		SWTTestView view = (SWTTestView) getTestView();
		Combo combo = view.getCombo();
		combo.removeAll();
		assertEquals(0, combo.getItemCount());
		
		final EventList<String> itemEventList = new BasicEventList<String>();
		ObservableEventList observableList = new ObservableEventList(itemEventList, String.class);
		context.bindList(
				SWTObservables.observeItems(combo),
				observableList,
				null, null);

		
		final Boolean [] finished = new Boolean[1];
		Thread aThread = new Thread() {
			@Override
			public void run() {
				for (int i = 0; i < 10; i++){
					for (int j = 0; j < 3; j++){
						itemEventList.add(""+j);
						try {
							Thread.sleep(200);
						} catch (InterruptedException e) {
						}
					}
					for (int j = 2; j >= 0; j--){
						itemEventList.remove(j);
					}
				}
				itemEventList.add("HELLO");
				finished[0] = true;
			}
		};
		aThread.run();
		aThread.join();
		assertTrue(finished[0]);
		assertEquals(1, combo.getItemCount());
		assertEquals("HELLO", combo.getItem(0));
	}
	
	public void testFieldAssist() throws Exception {
		SWTTestView view = (SWTTestView) getTestView();

		Control text = view.getText();
		FieldDecorationRegistry decorationRegistry = FieldDecorationRegistry.getDefault();
		FieldDecoration deco = decorationRegistry
				.getFieldDecoration(FieldDecorationRegistry.DEC_ERROR);
		Image image = deco.getImage();
		
		
		ControlDecoration cd = new ControlDecoration(text, SWT.LEFT);
		// Create a field decoration and add it to the field
		
		cd.setImage(image);
		cd.setDescriptionText("FOO!");
	}
}
