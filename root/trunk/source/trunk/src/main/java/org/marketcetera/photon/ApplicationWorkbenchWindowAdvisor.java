package org.marketcetera.photon;

import java.util.Date;

import org.apache.log4j.SimpleLayout;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.photon.editors.OrderHistoryEditor;
import org.marketcetera.photon.editors.OrderHistoryInput;
import org.marketcetera.photon.views.FiltersView;
import org.marketcetera.photon.views.StockOrderTicket;

/**
 * Required by the RCP platform this class is responsible for setting up the
 * workbench upon startup.
 * @author gmiller
 *
 */
@ClassVersion("$Id$")
public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

    /**
     * Simply calls superclass constructor.
     * @param configurer the configurer to pass to the superclass
     */
    public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
        super(configurer);
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.application.WorkbenchWindowAdvisor#createActionBarAdvisor(org.eclipse.ui.application.IActionBarConfigurer)
     */
    public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) {
        return new ApplicationActionBarAdvisor(configurer);
    }
    
    /**
     * Sets a number of options on the IWorkbenchWindowConfigurer prior
     * to opening the window.
     * 
     * @see org.eclipse.ui.application.WorkbenchWindowAdvisor#preWindowOpen()
     */
    public void preWindowOpen() {
        IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
        configurer.setInitialSize(new Point(800, 600));
        configurer.setShowCoolBar(true);
        configurer.setShowStatusLine(true);
        configurer.setShowMenuBar(true);
//        IProduct product = Platform.getProduct();
//        String productName = product == null ? "" : product.getName()
//        configurer.setTitle(productName);
        configurer.setShowPerspectiveBar(true);
        configurer.setShowProgressIndicator(true);
    }
    
	/*
	 * Called after the window has opened, and all UI elements have been initialized,
	 * this method takes care of wiring UI components to
	 * the underlying model and controller elements.  For example it connects the
	 * Console view to a logger appender to feed it data.
	 * 
	 * 
	 * @see org.eclipse.ui.application.WorkbenchWindowAdvisor#postWindowOpen()
	 */
	@Override
	public void postWindowOpen() {
		initStatusLine();

		IConsole[] consoles = ConsolePlugin.getDefault().getConsoleManager()
				.getConsoles();
		for (IConsole console : consoles) {
			if (console instanceof MainConsole) {
				MainConsole mainConsole = (MainConsole) console;
				PhotonConsoleAppender appender = new PhotonConsoleAppender(
						getWindowConfigurer().getWindow().getShell()
								.getDisplay(), mainConsole);
				appender.setLayout(new SimpleLayout());
				Application.getMainConsoleLogger().addAppender(appender);
			}
		} 
				
		IWorkbenchPage page = this.getWindowConfigurer().getWindow().getActivePage();
		OrderHistoryInput input = new OrderHistoryInput(Application.getFIXMessageHistory());
		try {
			page.openEditor(input, OrderHistoryEditor.ID, true);
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		IViewPart stockOrderTicket = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(StockOrderTicket.ID);		
		IStatusLineManager statusLineManager = getWindowConfigurer().getActionBarConfigurer().getStatusLineManager();
		IContributionItem item = statusLineManager.find(CommandStatusLineContribution.ID);
		if (stockOrderTicket instanceof StockOrderTicket && item instanceof CommandStatusLineContribution) {
			StockOrderTicket sot = (StockOrderTicket) stockOrderTicket;
			CommandStatusLineContribution cslc = (CommandStatusLineContribution) item;
			cslc.addCommandListener(sot.getCommandListener());
			cslc.setIDFactory(Application.getIDFactory());
		}

		IViewPart filterView = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(FiltersView.ID);
		Application.getFIXMessageHistory().setMatcherEditor(((FiltersView)filterView).getMatcherEditor());
		Application.getOrderManager().addOrderActionListener(((FiltersView)filterView));
		
		Application.initJMSConnector();

		Application.getMainConsoleLogger().info(
				"Application initialized: " + new Date());
		
	}

	/** 
	 * Initializes the status line.
	 * 
	 */
	private void initStatusLine() {
//		statusImage = AbstractUIPlugin.imageDescriptorFromPlugin(
//				"org.eclipsercp.hyperbola", IImageKeys.ONLINE).createImage();
		IStatusLineManager statusline = getWindowConfigurer()
				.getActionBarConfigurer().getStatusLineManager();
		statusline.setMessage("Online");
	}

}
