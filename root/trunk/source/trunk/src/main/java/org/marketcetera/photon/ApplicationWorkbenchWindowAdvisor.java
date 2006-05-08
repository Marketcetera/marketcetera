package org.marketcetera.photon;

import java.util.Date;

import javax.jms.JMSException;

import org.apache.log4j.SimpleLayout;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.views.IViewDescriptor;
import org.marketcetera.photon.editors.OrderHistoryEditor;
import org.marketcetera.photon.editors.OrderHistoryInput;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

    public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
        super(configurer);
    }

    public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) {
        return new ApplicationActionBarAdvisor(configurer);
    }
    
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
    
	/* (non-Javadoc)
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

		try {
			Application.initJMSConnector();
		} catch (JMSException ex) {
		}

		Application.getMainConsoleLogger().info(
				"Application initialized: " + new Date());
		
	}

	private void initStatusLine() {
//		statusImage = AbstractUIPlugin.imageDescriptorFromPlugin(
//				"org.eclipsercp.hyperbola", IImageKeys.ONLINE).createImage();
		IStatusLineManager statusline = getWindowConfigurer()
				.getActionBarConfigurer().getStatusLineManager();
		statusline.setMessage("Online");
	}

}
