package org.marketcetera.photon.module.preferences;


import org.eclipse.jface.layout.GridLayoutFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/* $License$ */

/**
 * Test {@link ModulePropertiesPreferencePage}.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
public class ModulePropertiesPreferencePageTest extends SWTTestBase {

	@Before
	public void setUp() throws Exception {
		super.setUp();
	}

	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}
	
	@Test
	public void open() {
		ModulePropertiesPreferencePage fixture = new ModulePropertiesPreferencePage();
		GridLayoutFactory.fillDefaults().applyTo(shell);
		fixture.createContents(shell);
	}

}
