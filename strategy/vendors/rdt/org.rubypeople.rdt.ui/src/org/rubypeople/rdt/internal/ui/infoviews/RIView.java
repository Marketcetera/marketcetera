package org.rubypeople.rdt.internal.ui.infoviews;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.UIJob;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.RubyPluginImages;
import org.rubypeople.rdt.internal.ui.rdocexport.RDocUtility;
import org.rubypeople.rdt.internal.ui.rdocexport.RdocListener;
import org.rubypeople.rdt.internal.ui.util.CollectionContentProvider;
import org.rubypeople.rdt.launching.IVMInstall;
import org.rubypeople.rdt.launching.IVMInstallChangedListener;
import org.rubypeople.rdt.launching.PropertyChangeEvent;
import org.rubypeople.rdt.launching.RubyRuntime;

public class RIView extends ViewPart implements RdocListener, IVMInstallChangedListener {

	private PageBook pageBook;
    private SashForm form;    
	private Text searchStr;
    private TableViewer searchListViewer;
    private Browser searchResult;
    private static List<String> fgPossibleMatches = new ArrayList<String>();
    private IStructuredContentProvider contentProvider = new CollectionContentProvider();
	private RubyInvokerJob latestJob;
	private MyViewerFilter filter;
	private Timer timer;
	private Table searchTable;

	/**
	 * The constructor.
	 */
	public RIView() {
		RubyRuntime.addVMInstallChangedListener(this);
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		contributeToActionBars();
		
		pageBook = new PageBook(parent, SWT.NONE);                       
        
        Label inProgressLabel = new Label( pageBook, SWT.LEFT | SWT.TOP | SWT.WRAP );
        inProgressLabel.setText(InfoViewMessages.RubyInformation_please_wait);
        
        form = new SashForm(pageBook, SWT.HORIZONTAL);        
                       
        Composite panel = new Composite(form, SWT.NONE);
        panel.setLayout(new GridLayout(1, false));              
       
        // Search String
        timer = new Timer();
        searchStr = new Text(panel, SWT.BORDER);
        GridData data = new GridData();        
        data.horizontalAlignment = SWT.FILL;
        searchStr.setLayoutData(data);
        searchStr.addModifyListener(new ModifyListener() {        
            public void modifyText(ModifyEvent e) {
            	// run this filter on a timer, reset timer if user hits keystroke before last scheduled filtering starts
            	if (timer != null) timer.cancel();
            	timer = new Timer();
            	TimerTask task = new TimerTask() {
				
					@Override
					public void run() {
						Display.getDefault().asyncExec(new Runnable() {
							public void run() {
								filterSearchList();  
							}
						});				
					}
				
				};
            	timer.schedule(task, 500);
            }        
        });
        searchStr.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);                                
                if (e.keyCode == 16777218 || e.keyCode == 13) { // sorry didn't find the SWT constant for down arrow
                    searchListViewer.getTable().setFocus();
                } else if (e.keyCode == SWT.ESC) {
                    searchStr.setText("");
                }
            }
        });
        
        searchTable = new Table(panel, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.VIRTUAL);
        searchListViewer = new TableViewer(searchTable);
        searchListViewer.setContentProvider(contentProvider);
        data = new GridData(GridData.FILL_VERTICAL | GridData.FILL_HORIZONTAL);
        searchListViewer.getTable().setLayoutData(data);
        searchListViewer.getTable().addSelectionListener(new SelectionListener() {        
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        
            public void widgetSelected(SelectionEvent e) {     
                showSelectedItem();
            }
        });        
        searchStr.addFocusListener(new FocusAdapter() {        
            public void focusGained(FocusEvent e) {
                searchStr.selectAll();
            }        
        });
        
        // Add the filter which narrows our results by what user types
        filter = new MyViewerFilter();
        
        // search result
        try {
        	searchResult = new Browser(form, SWT.BORDER);
        	searchResult.setText("<html><body style=\"background-color: #000\"></body></html>");
		} catch (Exception e) {
			MessageDialog.openError(Display.getDefault().getActiveShell(), "Unable to create embedded browser", "It appears that you do not have an embeddable browser. Please see http://www.eclipse.org/swt/faq.php#browserlinux for more information if you are on Linux.");
		}
        
        form.setWeights(new int[]{1, 3});        
        
        pageBook.showPage(inProgressLabel);
        updatePage();
		RDocUtility.addRdocListener(this);
	}
	    
	private void contributeToActionBars() {
		IAction refreshAction = new Action() {
			public void run() {
				Job job = new Job("Refreshing RI View") {
				
					@Override
					public IStatus run(IProgressMonitor monitor) {
						RiUtility.rebuildIndex();
						updatePage();
						return Status.OK_STATUS;
					}
				
				};
				job.schedule();
			}
		};
		refreshAction.setText(InfoViewMessages.RubyInformation_refresh);
		refreshAction.setToolTipText(InfoViewMessages.RubyInformation_refresh_tooltip);
		refreshAction.setImageDescriptor(RubyPluginImages.TOOLBAR_REFRESH);
			
		IToolBarManager manager = getViewSite().getActionBars().getToolBarManager();
		manager.add(refreshAction);		
	}

	private void updatePage() {
    	initSearchList();
    	Display.getDefault().asyncExec(new Runnable () {
		      public void run () {
		    	  pageBook.showPage(form);        
		      }
		 });
    }
    
    private void showSelectedItem() {
        String searchText = (String)((IStructuredSelection)searchListViewer.getSelection()).getFirstElement();        
        if (latestJob != null && latestJob.getState() != Job.NONE) {
        	latestJob.cancel();
        }
        latestJob = new RubyInvokerJob(new RIDescriptionUpdater(searchText));
        latestJob.setPriority(Job.INTERACTIVE);
        latestJob.schedule();
    }        
    
    public void dispose() {
        RDocUtility.removeRdocListener(this);
        RubyRuntime.removeVMInstallChangedListener(this);
        filter = null;
        super.dispose();
    }
             
    private synchronized void initSearchList() {
    	RubyInvoker invoker = new RIPopulator();
		Job job = new RubyInvokerJob(invoker);
		job.setPriority(Job.LONG);
		job.schedule();
	}	
	
	protected List<String> read(Reader reader) {
		Set<String> results = new HashSet<String>();
		BufferedReader reader2 = null;
		try {
			reader2 = new BufferedReader(reader);
			String line = null;                  
			while ((line = reader2.readLine()) != null) {
				results.add(line.trim());
			}			
		} catch (IOException e) {
			RubyPlugin.log(e);
		} finally {
			try {
				if (reader2 != null) 
					reader2.close();
			} catch (IOException e) {
				// ignore
			}
		}
		List<String> list = new ArrayList<String>(results);
		Collections.sort(list);
		return list;
	}

	private static class RubyInvokerJob extends Job {
		private RubyInvoker invoker;

		public RubyInvokerJob(RubyInvoker invoker) {
			super(InfoViewMessages.RubyInformation_update_job_title);
			this.invoker = invoker;
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			invoker.invoke();
			return Status.OK_STATUS;
		}
	}
	

    private void filterSearchList() {    	
    	UIJob job = new UIJob("Filtering RI List") {
		
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				List<String> filtered =  filter(searchStr.getText());
		    	searchTable.setItemCount(filtered.size());
				searchTable.clearAll();
				searchListViewer.setInput(filtered);
				if (searchTable.getItemCount() > 0) searchTable.setSelection(0);             
		        if (searchTable.getItemCount() == 1) showSelectedItem();
				return Status.OK_STATUS;
			}
		
		};
		job.schedule();        
    }

	protected List<String> filter(String text) {
		filter.setText(text);
		List<String> filtered = new ArrayList<String>();
		for (String possible : fgPossibleMatches) {
			if (filter.select(null, null, possible)) filtered.add(possible);
		}
		return filtered;
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		form.setFocus();
	}

    abstract class RubyInvoker {
        protected abstract List<String> getArgList();
        protected abstract void handleOutput(String content);
        protected void beforeInvoke(){}        
        public abstract void invoke();
    }
    
    private class RIDescriptionUpdater extends RubyInvoker {
    	private String searchValue;
		private final String HEADER = "<html><head></head><body style=\"color: #fff; background-color: #000\">";
		private final String TAIL = "</body></html>";
		private StringBuilder buffer;
    	
    	RIDescriptionUpdater(String value) {
    		this.searchValue = value;
    	}
    	
    	@Override
    	public void invoke() {
    		String content = RiUtility.getRIHTMLContents(getArgList());			

			// If we can't find it ourselves then display an error to the
			// user
			if (content == null) {
				content = "";
			}
			handleOutput(content);    
    	}
    	
        protected List<String> getArgList() {
            List<String> args = new ArrayList<String>();
            args.add(searchValue);
            return args;
        }

        protected void beforeInvoke() {
            searchResult.setText(InfoViewMessages.RubyInformation_please_wait);
        }
        

        protected void handleOutput(final String content) {
        	if (content == null)
				return;
			buffer = new StringBuilder();
			buffer.append(content);
			int index = buffer.indexOf("<body>");
			buffer.replace(index, index + 6, "<body style=\"color: #fff; background-color: #000\">");
			final String text = buffer.toString();
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					searchResult.setText(text);
				}
			});
        }

	
    }

    /**
	 * When the rdoc has changed, automatically update/regenerate the view
	 */
	public void rdocChanged() {
		updatePage();		
	}
	
	private class RIPopulator extends RubyInvoker {
				
		@Override
		public void invoke() {
			String content = RiUtility.getRIContents(getArgList());			

			// If we can't find it ourselves then display an error to the
			// user
			if (content == null) {
				content = "";
			}
			handleOutput(content);    
		}
		
		@Override
		protected List<String> getArgList() {
			 List<String> args = new ArrayList<String>();
             args.add("--no-pager");
             args.add("-l");
             return args;
		}

		@Override
		protected void handleOutput(String content) {
			if (content == null) return;
            BufferedReader reader = new BufferedReader(new StringReader(content));
            String line = null;
            fgPossibleMatches = read(new StringReader(content));
            // if no matches were found display an error message
            Display.getDefault().asyncExec(new Runnable () {
    		      public void run () {
    		    	searchListViewer.setInput(fgPossibleMatches);
    		    	filterSearchList();
    		    	pageBook.showPage(form);
    		      }
    		   });
		}		
	}
	
	public void defaultVMInstallChanged(IVMInstall previous, IVMInstall current) {
		updatePage();		
	}

	public void vmAdded(IVMInstall newVm) {
		// ignore		
	}

	public void vmChanged(PropertyChangeEvent event) {
		// ignore		
	}

	public void vmRemoved(IVMInstall removedVm) {
		// ignore		
	}
	
	private static class MyViewerFilter extends ViewerFilter {
		
		private List<String> userTokens;

		public void setText(String value) {
			if (value == null || value.trim().length() == 0) {
				this.userTokens = null;
			} else {
				this.userTokens = getTokens(value);			
			}
		}

		@Override
		public boolean select(Viewer viewer, Object parentElement, Object element) {		       
		    if (userTokens == null) return true;
			String riEntry = (String) element;
			List<String> riListTokens = getTokens(riEntry);
			if (userTokens.size() == 1) { // special case, match if any token starts with what user typed
				String userInput = userTokens.get(0);
				for (int i = 0; i < riListTokens.size(); i++) {
					if (riListTokens.get(i).startsWith(userInput)) {
						return true;
					}
				}
				return false;
			} else { // More than one token			
				if (userTokens.size() > riListTokens.size()) return false; // if user entered longer qualified name, don't even try matching
			
				// match up to # of tokens user typed
				for (int i = 0; i < userTokens.size(); i++) {
					if (!riListTokens.get(i).startsWith(userTokens.get(i))) {
						return false;
					}
				}
				return true;
			}
		}
		
		private List<String> getTokens(String raw) {
			List<String> tokens = new ArrayList<String>();
			StringTokenizer tokenizer = new StringTokenizer(raw, "::#");
			while (tokenizer.hasMoreTokens()) {
				tokens.add(tokenizer.nextToken().toLowerCase());
			}
			return tokens;
		}
	}
}