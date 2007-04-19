package org.marketcetera.photon.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.marketcetera.photon.ui.BookComposite;

/**
 * Create a BookComposite in a Section. 
 */
public class BookViewPieces {
	private Composite defaultParent;

	private FormToolkit formToolkit;

	private BookComposite bookComposite;

	private Section bookSection;

	public BookViewPieces(Composite defaultParent, FormToolkit formToolkit) {
		this.defaultParent = defaultParent;
		this.formToolkit = formToolkit;

	}

	private FormToolkit getFormToolkit() {
		return formToolkit;
	}

	public void createBookComposite() {
		bookSection = getFormToolkit().createSection(defaultParent,
				Section.TITLE_BAR);
		bookSection.setText("Market data");
		bookSection.setExpanded(true);

		GridLayout gridLayout = new GridLayout();
		GridData layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = true;
		layoutData.verticalAlignment = SWT.FILL;
		layoutData.horizontalAlignment = SWT.FILL;
		layoutData.horizontalSpan = 5;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.numColumns = 2;

		bookSection.setLayout(gridLayout);
		bookSection.setLayoutData(layoutData);

		bookComposite = new BookComposite(bookSection, SWT.NONE,
				getFormToolkit());
		bookSection.setClient(bookComposite);
	}

	public BookComposite getBookComposite() {
		return bookComposite;
	}

}
