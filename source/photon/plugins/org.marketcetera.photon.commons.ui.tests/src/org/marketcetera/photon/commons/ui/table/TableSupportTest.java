package org.marketcetera.photon.commons.ui.table;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.junit.Test;
import org.marketcetera.photon.commons.ui.table.TableConfiguration;
import org.marketcetera.photon.commons.ui.table.TableSupport;


/* $License$ */

/**
 * Test {@link TableSupport}.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
public class TableSupportTest {

	@Test
	public void createsTable() {
		TableSupport fixture = TableSupport.create(TableConfiguration.defaults());
		Display display = Display.getCurrent();
		if (display == null) display = new Display();
		Shell shell = new Shell(display);
		fixture.createTable(shell);
		Control[] shellChildren = shell.getChildren();
		assertEquals(1, shellChildren.length);
		Composite composite = (Composite) shellChildren[0];
		assertTrue(composite.getLayout() instanceof TableColumnLayout);
		Control[] compositeChildren = composite.getChildren();
		assertEquals(1, compositeChildren.length);
		assertTrue(compositeChildren[0] instanceof Table);
	}
}
