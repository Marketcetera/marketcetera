package org.marketcetera.photon.ui.databinding;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.list.WritableList;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.TransformedList;
import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;

/**
 * This class is a wrapper of the GlazedLists {@link EventList} API
 * to expose it to SWT databindings.  To do this, ObservableEventList
 * is made a subclass of {@link WritableList}.  This WritableList becomes a cache
 * of the list data returned by the EventList.
 * 
 * Update events from the EventList are marshaled into the correct {@link Realm}
 * and used to update this WritableList.
 * 
 * @author gmiller
 *
 */
@SuppressWarnings("unchecked") //$NON-NLS-1$
public class ObservableEventList extends WritableList {



	class InternalTransformedList extends TransformedList
    {
        /**
         * propagates events on the Swing thread
         */
        private UpdateRunner updateRunner = new UpdateRunner();

        /**
         * whether the dispatch thread has been scheduled
         */
        private boolean scheduled = false;
        public volatile boolean debug = false;

        
		protected InternalTransformedList(EventList source) {
			super(source);
	        this.addListEventListener(updateRunner);

	        source.addListEventListener(this);
		}

        /**
         * {@inheritDoc}
         */
        public final void listChanged(ListEvent listChanges) {
            if (!scheduled) {
                updates.beginEvent(true);
            }
            updates.forwardEvent(listChanges);
            if (!scheduled) {
                scheduled = true;
                schedule(updateRunner);
            }
        }
        
        /**
         * {@inheritDoc}
         */
        protected final boolean isWritable() {
            return true;
        }
        
        /**
         * Schedule the specified runnable to be run on the proxied thread.
         */
        protected void schedule(Runnable runnable) {
            if (targetRealm.isCurrent()) {
                runnable.run();
            } else  {
            	targetRealm.asyncExec(runnable);
            }
        }
	    /**
	     * Updates the internal data over the Swing thread.
	     *
	     * @author <a href="mailto:jesse@odel.on.ca">Jesse Wilson</a>
	     */
	    private class UpdateRunner implements Runnable, ListEventListener {
	
	        private UpdateRunner() {
	            super();
	        }
	
	        /**
	         * When run, this combines all events thus far and forwards them.
	         *
	         * <p>If a reordering event is being forwarded, the reordering may be lost
	         * if it arrives simultaneously with another event. This is somewhat of a
	         * hack for the time being. Hopefully later we can refine this so that a
	         * new event is created with these changes properly.
	         */
	        public void run() {
	            getReadWriteLock().writeLock().lock();
	            try {
	                updates.commitEvent();
	                scheduled = false;
	            } finally {
	                getReadWriteLock().writeLock().unlock();
	            }
	        }
	
	        /**
	         * Update local state as a consequence of the change event.
	         */
	        public void listChanged(ListEvent listChanges) {
	            while (listChanges.next()) {
	                final int sourceIndex = listChanges.getIndex();
	                final int changeType = listChanges.getType();
	                switch (changeType) {
	                case ListEvent.DELETE:
	                	ObservableEventList.this.remove(sourceIndex);
	                    break;
	
	                case ListEvent.INSERT:
	                	ObservableEventList.this.add(sourceIndex, source.get(sourceIndex));
	                    break;
	
	                case ListEvent.UPDATE:
	                	ObservableEventList.this.set(sourceIndex, source.get(sourceIndex));
	                    break;
	
	                }
	            }
	        }
	    }
    }

	private InternalTransformedList internalTransformedList;
	private final Realm targetRealm;

	/**
     */
    public ObservableEventList(EventList source) {
    	this(source, null);
    }
	
	/**
     */
    public ObservableEventList(EventList source, Object elementType) {
    	this(Realm.getDefault(), source, elementType);
    }

    public ObservableEventList(Realm realm, EventList source, Object elementType) {
        targetRealm = realm;
		addAll(source);
        internalTransformedList = new InternalTransformedList(source);
    }

    @Override
    public synchronized void dispose() {
    	internalTransformedList.dispose();
    }

}
