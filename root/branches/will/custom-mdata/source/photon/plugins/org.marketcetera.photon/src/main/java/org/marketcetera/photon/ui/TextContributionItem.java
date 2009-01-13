package org.marketcetera.photon.ui;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.accessibility.Accessible;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IPartListener;

/**
 * A ControlContribution that uses a {@link org.eclipse.swt.widgets.Text} as
 * its control
 * 
 */
public class TextContributionItem extends ContributionItem {

	private Text textField;

	private ToolItem toolitem;

	private IPartListener partListener;

	private String initialText;

	private List<KeyListener> keyListeners;
	
	private boolean initialEnabledState = true;

	public TextContributionItem(String initText) {
		initialText = initText;
		keyListeners = new LinkedList<KeyListener>();
	}

	private void refresh(boolean repopulateCombo) {
		if (textField == null || textField.isDisposed())
			return;
	}

	protected int computeWidth(Control control) {
		return control.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x;
	}

	protected Control createControl(Composite parent) {
		textField = new Text(parent, SWT.BORDER);
		textField.setText(initialText);
		textField.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				// do nothing
			}

			public void focusLost(FocusEvent e) {
				refresh(false);
			}
		});

		toolitem.setWidth(200); // TODO i18n
		refresh(true);
		for (KeyListener listener : keyListeners) {
			textField.addKeyListener(listener);
		}
		keyListeners = null;
		
		textField.setEnabled(initialEnabledState);
		return textField;
	}

	public void dispose() {
		if (partListener == null)
			return;
		textField = null;
		partListener = null;
	}

	public final void fill(Composite parent) {
		createControl(parent);
	}

	public final void fill(Menu parent, int index) {
		Assert.isTrue(false, "Can't add a control to a menu");//$NON-NLS-1$
	}

	public void fill(ToolBar parent, int index) {
		toolitem = new ToolItem(parent, SWT.SEPARATOR, index);
		Control control = createControl(parent);
		toolitem.setControl(control);
	}

	public void addKeyListener(KeyListener listener) {
		if (textField != null){
			textField.addKeyListener(listener);
		} else {
			keyListeners.add(listener);
		}
	}
	public void append(String string) {
		textField.append(string);
	}

	public void clearSelection() {
		textField.clearSelection();
	}

	public Point computeSize(int wHint, int hHint, boolean changed) {
		return textField.computeSize(wHint, hHint, changed);
	}

	public Point computeSize(int wHint, int hHint) {
		return textField.computeSize(wHint, hHint);
	}

	public Rectangle computeTrim(int x, int y, int width, int height) {
		return textField.computeTrim(x, y, width, height);
	}

	public void copy() {
		textField.copy();
	}

	public void cut() {
		textField.cut();
	}

	public boolean equals(Object arg0) {
		return textField.equals(arg0);
	}

	public boolean forceFocus() {
		return textField.forceFocus();
	}

	public Accessible getAccessible() {
		return textField.getAccessible();
	}

	public Color getBackground() {
		return textField.getBackground();
	}

	public Image getBackgroundImage() {
		return textField.getBackgroundImage();
	}

	public int getBorderWidth() {
		return textField.getBorderWidth();
	}

	public Rectangle getBounds() {
		return textField.getBounds();
	}

	public int getCaretLineNumber() {
		return textField.getCaretLineNumber();
	}

	public Point getCaretLocation() {
		return textField.getCaretLocation();
	}

	public int getCaretPosition() {
		return textField.getCaretPosition();
	}

	public int getCharCount() {
		return textField.getCharCount();
	}

	public Rectangle getClientArea() {
		return textField.getClientArea();
	}

	public Object getData() {
		return textField.getData();
	}

	public Object getData(String key) {
		return textField.getData(key);
	}

	public Display getDisplay() {
		return textField.getDisplay();
	}

	public boolean getDoubleClickEnabled() {
		return textField.getDoubleClickEnabled();
	}

	public char getEchoChar() {
		return textField.getEchoChar();
	}

	public boolean getEditable() {
		return textField.getEditable();
	}

	public boolean getEnabled() {
		return textField.getEnabled();
	}

	public Font getFont() {
		return textField.getFont();
	}

	public Color getForeground() {
		return textField.getForeground();
	}

	public ScrollBar getHorizontalBar() {
		return textField.getHorizontalBar();
	}

	public Object getLayoutData() {
		return textField.getLayoutData();
	}

	public int getLineCount() {
		return textField.getLineCount();
	}

	public String getLineDelimiter() {
		return textField.getLineDelimiter();
	}

	public int getLineHeight() {
		return textField.getLineHeight();
	}

	public Point getLocation() {
		return textField.getLocation();
	}

	public Menu getMenu() {
		return textField.getMenu();
	}

	public int getOrientation() {
		return textField.getOrientation();
	}

	public Point getSelection() {
		return textField.getSelection();
	}

	public int getSelectionCount() {
		return textField.getSelectionCount();
	}

	public String getSelectionText() {
		return textField.getSelectionText();
	}

	public Point getSize() {
		return textField.getSize();
	}

	public int getStyle() {
		return textField.getStyle();
	}

	public int getTabs() {
		return textField.getTabs();
	}

	public String getText() {
		return textField.getText();
	}

	public String getText(int start, int end) {
		return textField.getText(start, end);
	}

	public int getTextLimit() {
		return textField.getTextLimit();
	}

	public String getToolTipText() {
		return textField.getToolTipText();
	}

	public int getTopIndex() {
		return textField.getTopIndex();
	}

	public int getTopPixel() {
		return textField.getTopPixel();
	}

	public ScrollBar getVerticalBar() {
		return textField.getVerticalBar();
	}

	public boolean getVisible() {
		return textField.getVisible();
	}

	public void insert(String string) {
		textField.insert(string);
	}

	public boolean isDisposed() {
		if(textField == null) {
			return true;
		}
		return textField.isDisposed();
	}

	public boolean isEnabled() {
		return textField.isEnabled();
	}

	public boolean isFocusControl() {
		return textField.isFocusControl();
	}

	public boolean isListening(int eventType) {
		return textField.isListening(eventType);
	}

	public boolean isReparentable() {
		return textField.isReparentable();
	}

	public void notifyListeners(int eventType, Event event) {
		textField.notifyListeners(eventType, event);
	}

	public void pack() {
		textField.pack();
	}

	public void pack(boolean changed) {
		textField.pack(changed);
	}

	public void paste() {
		textField.paste();
	}

	public void selectAll() {
		textField.selectAll();
	}

	public void setBackground(Color color) {
		textField.setBackground(color);
	}

	public void setBackgroundImage(Image image) {
		textField.setBackgroundImage(image);
	}

	public void setBounds(int x, int y, int width, int height) {
		textField.setBounds(x, y, width, height);
	}

	public void setBounds(Rectangle rect) {
		textField.setBounds(rect);
	}

	public void setCapture(boolean capture) {
		textField.setCapture(capture);
	}

	public void setCursor(Cursor cursor) {
		textField.setCursor(cursor);
	}

	public void setData(Object data) {
		textField.setData(data);
	}

	public void setData(String key, Object value) {
		textField.setData(key, value);
	}

	public void setDoubleClickEnabled(boolean doubleClick) {
		textField.setDoubleClickEnabled(doubleClick);
	}

	public void setEchoChar(char echo) {
		textField.setEchoChar(echo);
	}

	public void setEditable(boolean editable) {
		textField.setEditable(editable);
	}

	public void setEnabled(boolean enabled) {
		if(textField != null) {
			textField.setEnabled(enabled);
		} else {
			initialEnabledState = enabled;
		}
	}

	public boolean setFocus() {
		return textField.setFocus();
	}

	public void setFont(Font font) {
		textField.setFont(font);
	}

	public void setForeground(Color color) {
		textField.setForeground(color);
	}

	public void setLayoutData(Object layoutData) {
		textField.setLayoutData(layoutData);
	}

	public void setMenu(Menu menu) {
		textField.setMenu(menu);
	}

	public void setOrientation(int orientation) {
		textField.setOrientation(orientation);
	}

	public void setRedraw(boolean redraw) {
		textField.setRedraw(redraw);
	}

	public void setSelection(int start, int end) {
		textField.setSelection(start, end);
	}

	public void setSelection(int start) {
		textField.setSelection(start);
	}

	public void setSelection(Point selection) {
		textField.setSelection(selection);
	}

	public void setSize(int width, int height) {
		textField.setSize(width, height);
	}

	public void setSize(Point size) {
		textField.setSize(size);
	}

	public void setTabs(int tabs) {
		textField.setTabs(tabs);
	}

	public void setText(String string) {
		textField.setText(string);
	}

	public void setTextLimit(int limit) {
		textField.setTextLimit(limit);
	}

	public void setToolTipText(String string) {
		textField.setToolTipText(string);
	}

	public void setTopIndex(int index) {
		textField.setTopIndex(index);
	}

	public void setVisible(boolean visible) {
		textField.setVisible(visible);
	}

	public void showSelection() {
		textField.showSelection();
	}

	public String toString() {
		return textField.toString();
	}

	public boolean traverse(int traversal) {
		return textField.traverse(traversal);
	}


}