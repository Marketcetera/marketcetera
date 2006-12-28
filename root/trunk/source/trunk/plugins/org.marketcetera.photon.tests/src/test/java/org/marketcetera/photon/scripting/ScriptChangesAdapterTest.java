package org.marketcetera.photon.scripting;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.apache.bsf.BSFException;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.marketcetera.photon.preferences.ListEditorUtil;
import org.marketcetera.photon.preferences.ScriptRegistryPage;

public class ScriptChangesAdapterTest extends TestCase {

	ScriptChangesAdapter adapter;
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		adapter = new ScriptChangesAdapter();
		adapter.setRegistry(new MockScriptRegistry());
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		adapter = null;
	}

	public void testSetInitialRegistryValueString() throws Exception {
		String[] items =  new String [] {
				"test_script_1",	
				"test_script_2",	
				"test_script_3",	
				"test_script_4",	
				"test_script&_5",	
		};
		String initialRegistryValueString = ListEditorUtil.encodeList(items);
		
		adapter.setInitialRegistryValueString(initialRegistryValueString);
		adapter.afterPropertiesSet();
		ScriptRegistry registry = adapter.getRegistry();
		for (String item : items) {
			assertTrue("Is not registered: "+item, registry.isRegistered(item));
		}
	}

	public void testPropertyChange() throws Exception {
		String[] oldItems =  new String [] {
				"test_script_1",	
				"test_script_2",	
				"test_script_3",	
				"test_script_4",	
				"test_script&_5",	
		};

		String[] newItems =  new String [] {
				"test_script_1",	
				"test_script_2",	
				"test_script_3",	
				"test_script_4",	
				"test_script_6",	
		};

		adapter.setInitialRegistryValueString(ListEditorUtil.encodeList(oldItems));
		adapter.afterPropertiesSet();

		ScriptRegistry registry = adapter.getRegistry();
		assertTrue(registry.isRegistered("test_script&_5"));

		PropertyChangeEvent event = new PropertyChangeEvent(this,
				ScriptRegistryPage.SCRIPT_REGISTRY_PREFERENCE, "",
				ListEditorUtil.encodeList(newItems));
		adapter.propertyChange(event);
		
		for (String item : newItems) {
			assertTrue("Not registered: "+item, registry.isRegistered(item));
		}
		assertTrue(!registry.isRegistered("test_script&_5"));
	}

	public void testResourceChanged() throws Exception {
		String[] oldItems =  new String [] {
				"test_script_1",	
				"test_script_2",	
				"test_script_3",	
				"test_script_4",	
				"test_script&_5",	
		};
		adapter.setInitialRegistryValueString(ListEditorUtil.encodeList(oldItems));
		adapter.afterPropertiesSet();

		MockResourceChangeEvent mockResourceChangeEvent = new MockResourceChangeEvent();
		adapter.resourceChanged(mockResourceChangeEvent);
		assertEquals(1, mockResourceChangeEvent.getNumVisited());
	}

	private final class MockResourceChangeEvent implements IResourceChangeEvent {
		private int numVisited = 0;

		public int getNumVisited() {
			return numVisited;
		}

		public IMarkerDelta[] findMarkerDeltas(String type, boolean includeSubtypes) {
			// TODO Auto-generated method stub
			return null;
		}

		public int getBuildKind() {
			// TODO Auto-generated method stub
			return 0;
		}

		public IResourceDelta getDelta() {
			return new IResourceDelta() {

				public void accept(IResourceDeltaVisitor visitor) throws CoreException {
					numVisited++;
				}

				public void accept(IResourceDeltaVisitor visitor, boolean includePhantoms) throws CoreException {
					// TODO Auto-generated method stub
					
				}

				public void accept(IResourceDeltaVisitor visitor, int memberFlags) throws CoreException {
					// TODO Auto-generated method stub
					
				}

				public IResourceDelta findMember(IPath path) {
					// TODO Auto-generated method stub
					return null;
				}

				public IResourceDelta[] getAffectedChildren() {
					// TODO Auto-generated method stub
					return null;
				}

				public IResourceDelta[] getAffectedChildren(int kindMask) {
					// TODO Auto-generated method stub
					return null;
				}

				public IResourceDelta[] getAffectedChildren(int kindMask, int memberFlags) {
					// TODO Auto-generated method stub
					return null;
				}

				public int getFlags() {
					// TODO Auto-generated method stub
					return 0;
				}

				public IPath getFullPath() {
					// TODO Auto-generated method stub
					return null;
				}

				public int getKind() {
					// TODO Auto-generated method stub
					return 0;
				}

				public IMarkerDelta[] getMarkerDeltas() {
					// TODO Auto-generated method stub
					return null;
				}

				public IPath getMovedFromPath() {
					// TODO Auto-generated method stub
					return null;
				}

				public IPath getMovedToPath() {
					// TODO Auto-generated method stub
					return null;
				}

				public IPath getProjectRelativePath() {
					// TODO Auto-generated method stub
					return null;
				}

				public IResource getResource() {
					// TODO Auto-generated method stub
					return null;
				}

				public Object getAdapter(Class adapter) {
					// TODO Auto-generated method stub
					return null;
				}
				
			};
		}

		public IResource getResource() {
			// TODO Auto-generated method stub
			return null;
		}

		public Object getSource() {
			// TODO Auto-generated method stub
			return null;
		}

		public int getType() {
			// TODO Auto-generated method stub
			return 0;
		}
	}

	class MockScriptRegistry extends ScriptRegistry
	{
		Set<String> registered = new HashSet<String>();
		Set<String> changed = new HashSet<String>();

		@Override
		public void afterPropertiesSet() throws Exception {
			// Do not initialize
		}

		@Override
		public void register(String fileName) throws BSFException {
			registered.add(fileName);
		}

		@Override
		public void scriptChanged(String fileName) throws BSFException {
			changed.add(fileName);
		}

		@Override
		public void unregister(String fileName) throws BSFException {
			registered.remove(fileName);
		}

		/* (non-Javadoc)
		 * @see org.marketcetera.photon.scripting.ScriptRegistry#isRegistered(java.lang.String)
		 */
		@Override
		public boolean isRegistered(String fileName) throws BSFException {
			return registered.contains(fileName);
		}
		
		public boolean isChanged(String fileName){
			return changed.contains(fileName);
		}
		
	}
}
