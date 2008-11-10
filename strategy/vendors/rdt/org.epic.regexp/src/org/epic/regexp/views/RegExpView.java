package org.epic.regexp.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.jface.action.*;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.*;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.FocusEvent;
import org.epic.regexp.RegExpPlugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import gnu.regexp.RE;
import gnu.regexp.REMatch;
import gnu.regexp.REException;

public class RegExpView extends ViewPart {

	class focusListener implements FocusListener {
		public void focusGained(FocusEvent e) {
			activeInput = e.getSource();
		}

		public void focusLost(FocusEvent e) {
		}
	};

	class DebugInfo {
		private List subexpressions = new ArrayList();

		private List bracketsInRegexp = new ArrayList();

		private List allMatches = new ArrayList();

		private String input;

		private String regexp;

		private String matchString;

		private boolean matchesInitialized = false;

		private int matchingBracketsCount = 0;

		int eflags = 0;

		public DebugInfo() {
		}

		public void setInput(String input) {
			this.input = input;
		}

		public void setRegexp(String regexp) {
			this.regexp = regexp;
		}

		public void setMatchString(String match) {
			this.matchString = match;
		}

		public String getInput() {
			return input;
		}

		public String getRegExp() {
			return regexp;
		}

		public String getMatchString() {
			return matchString;
		}

		public void addBracketPosition(int start, int end) {
			bracketsInRegexp.add(new SubexpressionPos(start, end));
		}

		public void addSubexpressionPosition(int start, int end) {

			subexpressions.add(new SubexpressionPos(start, end));
		}

		public SubexpressionPos getSubexpressionPosition(int index) {
			return (SubexpressionPos) subexpressions.get(index);
		}

		public int getSubexpressionCount() {
			return subexpressions.size();
		}

		public REMatch[] getMatches(int index) {

			int checkEflags = 0;

			if (ignoreCaseCheckBox.getSelection()) {
				checkEflags |= RE.REG_ICASE;
			}

			if (multilineCheckBox.getSelection()) {
				checkEflags |= RE.REG_MULTILINE;
			}

			if (!matchesInitialized || eflags != checkEflags) {
				eflags = checkEflags;
				initMatches();
			}

			return (REMatch[]) allMatches.toArray(new REMatch[0]);
		}

		public int geMatchingBracketsCount() {
			return matchingBracketsCount;
		}

		public void initMatches() {
			// Get longest match
			String reg = null;
			boolean found = false;
			RE re = null;

			this.allMatches.clear();

			try {
				for (int i = bracketsInRegexp.size() - 1; i >= 0; i--) {
					SubexpressionPos pos = (SubexpressionPos) bracketsInRegexp
							.get(i);
					reg = regexp.substring(0, pos.getEnd() + 1);

					re = new RE(reg, eflags);
					if (re.getAllMatches(matchString).length > 0) {
						matchingBracketsCount = i + 1;
						found = true;

						break;
					}
				}

				if (re != null && found) {
					REMatch[] matches = re.getAllMatches(matchString);
					for (int j = 0; j < matches.length; j++) {
						this.allMatches.add(matches[j]);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			this.matchesInitialized = true;

		}

	};

	class SubexpressionPos {
		int start;

		int end;

		public SubexpressionPos(int start, int end) {
			this.start = start;
			this.end = end;
		}

		private int getStart() {
			return start;
		}

		private int getEnd() {
			return end;
		}
	};

	private Composite panel;

	private List colorTable = new ArrayList();

	private Action validateAction, cutAction, copyAction, pasteAction;

	private Action stopDebugAction, forwardDebugAction, backDebugAction;

	private StyledText regExpText;

	private StyledText matchText;

	private Label resultImageLabel;

	private Button ignoreCaseCheckBox, multilineCheckBox;

	private Object activeInput = null;

	private DebugInfo debugInfo = null;

	private int debugPosition = 0;

	/**
	 * The constructor.
	 */
	public RegExpView() {
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {

		makeActions();
		contributeToActionBars();

		panel = new Composite(parent, SWT.NULL);
		//Create a data that takes up the extra space in the dialog .

		buildColorTable();

		GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		panel.setLayout(layout);

		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.grabExcessHorizontalSpace = true;
		panel.setLayoutData(data);

		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 1;
		Label regExpLabel = new Label(panel, SWT.NONE);
		regExpLabel.setText("Regular Expression:");
		regExpLabel.setLayoutData(data);

		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 1;
		resultImageLabel = new Label(panel, SWT.NONE);
		setResultLabelImage(RegExpImages.RESULT_GRAY);
		resultImageLabel.setLayoutData(data);

		// Insert CheckBoxes
		data = new GridData();
		data.horizontalSpan = 1;
		ignoreCaseCheckBox = new Button(panel, SWT.CHECK);
		ignoreCaseCheckBox.setText("ignore case");
		ignoreCaseCheckBox.setLayoutData(data);

		data = new GridData();
		data.horizontalSpan = 1;
		multilineCheckBox = new Button(panel, SWT.CHECK);
		multilineCheckBox.setText("multiline");
		multilineCheckBox.setLayoutData(data);

		//RegExp
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 4;
		regExpText = new StyledText(panel, SWT.BORDER);
		regExpText.setLayoutData(data);
		regExpText.addFocusListener(new focusListener());

		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 4;
		Label matchTextLabel = new Label(panel, SWT.NONE);
		matchTextLabel.setText("Text to match against:");
		matchTextLabel.setLayoutData(data);

		//Match text
		data = new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL);
		data.horizontalSpan = 4;
		matchText = new StyledText(panel, SWT.BORDER | SWT.H_SCROLL
				| SWT.V_SCROLL);
		matchText.setLayoutData(data);
		matchText.addFocusListener(new focusListener());

		//set the actions for the global action handler
		IActionBars bars = getViewSite().getActionBars();
		bars.setGlobalActionHandler(ActionFactory.CUT.getId(), cutAction);
		bars.setGlobalActionHandler(ActionFactory.COPY.getId(), copyAction);
		bars.setGlobalActionHandler(ActionFactory.PASTE.getId(), pasteAction);

		hookContextMenu();

	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				fillContextMenu(manager);
			}
		});

		Menu menuRegExp = menuMgr.createContextMenu(regExpText);
		regExpText.setMenu(menuRegExp);

		Menu menuMatch = menuMgr.createContextMenu(matchText);
		matchText.setMenu(menuMatch);
		//getSite().registerContextMenu(menuMgr, );
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(cutAction);
		manager.add(copyAction);
		manager.add(pasteAction);
		createShortcutsMenu(manager);

		// Other plug-ins can contribute there actions here
		manager.add(new Separator("Additions"));
	}

	private void buildColorTable() {
		Display display = panel.getDisplay();
		colorTable.add(display.getSystemColor(SWT.COLOR_BLUE));
		colorTable.add(display.getSystemColor(SWT.COLOR_BLACK));
		colorTable.add(display.getSystemColor(SWT.COLOR_RED));
		colorTable.add(display.getSystemColor(SWT.COLOR_DARK_GRAY));
		colorTable.add(display.getSystemColor(SWT.COLOR_DARK_GREEN));
		colorTable.add(new Color(display, 255, 127, 0));
		colorTable.add(display.getSystemColor(SWT.COLOR_DARK_MAGENTA));
		colorTable.add(new Color(display, 201, 141, 141));
		colorTable.add(new Color(display, 214, 179, 74));
		colorTable.add(new Color(display, 204, 74, 214));
	}

	private void createShortcutsMenu(IMenuManager mgr) {
		IMenuManager submenu = new MenuManager("Shortcuts");
		mgr.add(submenu);

		Action shortcut;

		try {
			File shortcutsFile = new File(RegExpPlugin.getPlugInDir()
					+ File.separator + "shortcuts");
			
			
			FileInputStream fin = new FileInputStream(shortcutsFile);
			BufferedReader br = new BufferedReader(new InputStreamReader(fin));

			String line;

			while ((line = br.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(line, "\t");

				if (st.countTokens() == 2) {

					final String shortc = st.nextToken();
					String descr = st.nextToken();

					shortcut = new Action() {
						public void run() {
							insertShortcut(shortc);
						}
					};

					shortcut.setText(shortc + " - " + descr);
					submenu.add(shortcut);
					//}

					mgr.update(true);
				} else if (st.countTokens() == 1) {
					String token = st.nextToken();
					if (token.equals("<DEL>")) {
						submenu.add(new Separator());
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(stopDebugAction);
		manager.add(backDebugAction);
		manager.add(forwardDebugAction);
		manager.add(new Separator());
		manager.add(validateAction);
	}

	private void makeActions() {

		// Reset debug action
		stopDebugAction = new Action() {
			public void run() {
				resetDebug();
			}
		};
		stopDebugAction.setText("Reset");
		stopDebugAction.setToolTipText("Reset");
		stopDebugAction.setImageDescriptor(RegExpImages.ICON_DEBUG_STOP);

		//	Back debug action
		backDebugAction = new Action() {
			public void run() {
				backDebug();
			}
		};
		backDebugAction.setText("Backward");
		backDebugAction.setToolTipText("Backward");
		backDebugAction.setImageDescriptor(RegExpImages.ICON_DEBUG_BACK);

		//	Forward debug action
		forwardDebugAction = new Action() {
			public void run() {
				forwardDebug();
			}
		};
		forwardDebugAction.setText("Forward");
		forwardDebugAction.setToolTipText("Forward");
		forwardDebugAction.setImageDescriptor(RegExpImages.ICON_DEBUG_FORWARD);

		// Validation action
		validateAction = new Action() {
			public void run() {
				validateRegExp();
			}
		};
		validateAction.setText("Validate RegExp");
		validateAction.setToolTipText("Validate RegExp");
		validateAction.setImageDescriptor(RegExpImages.ICON_RUN);

		// Cut action
		cutAction = new Action("Cut", RegExpImages.EDIT_CUT) {
			public void run() {
				((StyledText) activeInput).cut();
			}
		};

		// Copy action
		copyAction = new Action("Copy", RegExpImages.EDIT_COPY) {
			public void run() {
				((StyledText) activeInput).copy();
			}
		};

		// Paste action
		pasteAction = new Action("Paste", RegExpImages.EDIT_PASTE) {
			public void run() {
				((StyledText) activeInput).paste();
			}
		};

	}

	private void insertShortcut(String text) {
		int selCount = regExpText.getSelectionCount();
		int pos = regExpText.getCaretOffset();
		regExpText.insert(text);

		// Adjust carret offset if no selection was available,
		// so carret is placed after the inserted text
		if (selCount == 0) {
			regExpText.setCaretOffset(pos + text.length());
		}
	}

	public void validateRegExp() {
		boolean result = false;
		int eflags = 0;

		// Reset style
		regExpText.setStyleRange(null);

		// Reset debug poition counter
		debugPosition = 0;

		if (ignoreCaseCheckBox.getSelection()) {
			eflags |= RE.REG_ICASE;
		}

		if (multilineCheckBox.getSelection()) {
			eflags |= RE.REG_MULTILINE;
		}

		try {
			RE re = new RE(regExpText.getText(), eflags);

			REMatch[] matches = re.getAllMatches(matchText.getText());

			result = matches.length > 0 ? true : false;

			// Reset style
			matchText.setStyleRange(null);

			for (int i = 0; i < matches.length; i++) {
				int color = 0;
				for (int j = 1; j <= re.getNumSubs(); j++) {

					StyleRange styleRange = new StyleRange();

					styleRange.start = matches[i].getStartIndex(j);
					styleRange.length = matches[i].getEndIndex(j)
							- matches[i].getStartIndex(j);

					Display display = panel.getDisplay();
					styleRange.foreground = display
							.getSystemColor(SWT.COLOR_WHITE);
					//styleRange.fontStyle = SWT.BOLD;

					styleRange.background = (Color) colorTable.get(color);

					matchText.setStyleRange(styleRange);
					// Update text position
					matchText.setTopIndex(styleRange.start);
					matchText.setCaretOffset(styleRange.start);
					int offsetFromLine = styleRange.start
							- matchText.getOffsetAtLine(matchText
									.getLineAtOffset(styleRange.start));
					matchText.setHorizontalIndex(offsetFromLine);
					matchText.redraw();

					if (++color > colorTable.size()) {
						color = 0;
					}
				}

			}

		} catch (REException e) {
			e.printStackTrace();
		}

		if (result) {
			setResultLabelImage(RegExpImages.RESULT_GREEN);
		} else {
			setResultLabelImage(RegExpImages.RESULT_RED);
		}
	}

	public void setResultLabelImage(ImageDescriptor descr) {
		Image labelImage = new Image(resultImageLabel.getDisplay(), descr
				.getImageData());
		labelImage.setBackground(panel.getBackground());
		resultImageLabel.setImage(labelImage);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		//regExpText.setFocus();
	}

	private void buildDebugRegExp(String input, String match) {
		String convInput;
		String result = "";
		RE re = null;
		boolean inBracket = false;
		boolean escape = false;
		String character;
		int start = 0, end = 0;
		int bracketStart = 0;

		debugInfo = new DebugInfo();
		debugInfo.setInput(input);
		debugInfo.setMatchString(match);

		try {
			// Replace escaped characters
			re = new RE("\\[^.]");
			convInput = re.substitute(input, "..");

			if (convInput.indexOf('(') == -1) {
				for (int i = 0; i < input.length(); i++) {
					character = input.substring(i, i + 1);

					if (!inBracket) {
						re = new RE("[\\[\\{]");
						if (re.isMatch(character)) {
							bracketStart = result.length();
							result += "(" + character;
							inBracket = true;
							start = i;
						} else {
							if (!escape) {
								bracketStart = result.length();
								result += "(";
								start = i;
							} else {
								escape = false;
							}

							result += character;
							if (character.equals("\\")) {
								escape = true;
								continue;
							}

							if ((i + 1) < input.length()) {
								String nextChar = input.substring(i + 1, i + 2);

								re = new RE("[\\*\\+]");
								if (re.isMatch(nextChar)) {
									result += nextChar;
									i++;
								}
							}

							if ((i + 1) < input.length()) {
								String nextChar = input.substring(i + 1, i + 2);

								re = new RE("[\\?]");
								if (re.isMatch(nextChar)) {
									result += nextChar;
									i++;
								}
							}

							result += ")";
							debugInfo.addSubexpressionPosition(start, i + 1);
							debugInfo.addBracketPosition(bracketStart, result
									.length() - 1);

						}

					} else {

						re = new RE("[\\]\\}]");
						if (re.isMatch(character)) {
							if ((i + 1) < input.length()) {

								String nextChar = input.substring(i + 1, i + 2);

								if (nextChar.equals("{")) {
									result += character + nextChar;
									i++;
									continue;
								}

								re = new RE("[\\+\\*]");
								if (re.isMatch(nextChar)) {
									result += character + nextChar;
									i++;

									if ((i + 1) < input.length()) {
										nextChar = input
												.substring(i + 1, i + 2);

										re = new RE("[\\?]");
										if (re.isMatch(nextChar)) {
											result += nextChar;
											i++;
										}
									}
									result += ")";
									debugInfo.addSubexpressionPosition(start,
											i + 1);
									debugInfo.addBracketPosition(bracketStart,
											result.length() - 1);

									inBracket = false;

								} else {
									result += character + ")";
									debugInfo.addSubexpressionPosition(start,
											i + 1);
									debugInfo.addBracketPosition(bracketStart,
											result.length() - 1);
									inBracket = false;
								}
							}
							// If it's the last character
							else {
								result += character + ")";
								debugInfo
										.addSubexpressionPosition(start, i + 1);
								debugInfo.addBracketPosition(bracketStart,
										result.length() - 1);
								inBracket = false;
							}

						} else {
							result += character;
						}
					}

				}

			} else {
				// Handle pre-formatted regexp
				//..................................
				result = input;

				re = new RE("\\((.*?)\\)");

				REMatch[] matches = re.getAllMatches(convInput);

				for (int i = 0; i < matches.length; i++) {
					for (int j = 1; j <= re.getNumSubs(); j++) {
						start = matches[i].getStartIndex(j);
						end = matches[i].getEndIndex(j);
						debugInfo.addSubexpressionPosition(start, end);
						debugInfo.addBracketPosition(start, end);
					}
				}
			}

		} catch (Exception e) {
			result = null;
			e.printStackTrace();
		}

		debugInfo.setRegexp(result);
	}

	private void resetDebug() {
		debugPosition = 0;
		regExpText.setStyleRange(null);
		matchText.setStyleRange(null);
		setResultLabelImage(RegExpImages.RESULT_GRAY);
	}

	private void backDebug() {
		showDebugResult(debugPosition - 1);
	}

	private void forwardDebug() {
		showDebugResult(debugPosition + 1);
	}

	private void showDebugResult(int position) {

		if (debugInfo == null) {
			buildDebugRegExp(regExpText.getText(), matchText.getText());
			position = 1;
		} else if (!debugInfo.getInput().equals(regExpText.getText())
				|| !debugInfo.getMatchString().equals(matchText.getText())) {
			buildDebugRegExp(regExpText.getText(), matchText.getText());
			position = 1;
		}

		// Nothing to do
		if (position > debugInfo.getSubexpressionCount() || position < 1) {
			return;
		}

		REMatch[] matches = debugInfo.getMatches(position - 1);

		debugPosition = position;

		StyleRange styleRangeRegExp = new StyleRange();

		Display display = panel.getDisplay();
		styleRangeRegExp.background = display.getSystemColor(SWT.COLOR_BLUE);
		styleRangeRegExp.foreground = display.getSystemColor(SWT.COLOR_WHITE);

		// Colour the regexp
		SubexpressionPos pos = debugInfo.getSubexpressionPosition(position - 1);
		//Reset style
		regExpText.setStyleRange(null);

		styleRangeRegExp.start = pos.getStart();
		styleRangeRegExp.length = pos.getEnd() - pos.getStart();

		regExpText.setStyleRange(styleRangeRegExp);

		//	Update text position
		regExpText.setTopIndex(styleRangeRegExp.start);
		regExpText.setCaretOffset(styleRangeRegExp.start);
		int offsetFromLine = styleRangeRegExp.start
				- regExpText.getOffsetAtLine(regExpText
						.getLineAtOffset(styleRangeRegExp.start));
		regExpText.setHorizontalIndex(offsetFromLine);
		regExpText.redraw();

		// Colour the matching text
		matchText.setStyleRange(null);

		if (position <= debugInfo.geMatchingBracketsCount()) {
			setResultLabelImage(RegExpImages.RESULT_GREEN);
			for (int i = 0; i < matches.length; i++) {

				StyleRange styleRangeMatch = new StyleRange();
				styleRangeMatch.background = display
						.getSystemColor(SWT.COLOR_BLUE);
				styleRangeMatch.foreground = display
						.getSystemColor(SWT.COLOR_WHITE);

				styleRangeMatch.start = matches[i].getStartIndex(position);
				styleRangeMatch.length = matches[i].getEndIndex(position)
						- matches[i].getStartIndex(position);
				matchText.setStyleRange(styleRangeMatch);
				//	Update text position
				matchText.setTopIndex(styleRangeMatch.start);
				matchText.setCaretOffset(styleRangeMatch.start);
				offsetFromLine = styleRangeMatch.start
						- matchText.getOffsetAtLine(matchText
								.getLineAtOffset(styleRangeMatch.start));
				matchText.setHorizontalIndex(offsetFromLine);
				matchText.redraw();
			}

		} else {
			setResultLabelImage(RegExpImages.RESULT_RED);
		}

	}

	/**
	 * @param regExpText
	 *            The regExpText to set.
	 */
	public void setRegExpText(String regexp) {
		regExpText.setText(regexp);
	}

	/**
	 * @param matchText
	 *            The matchText to set.
	 */
	public void setMatchText(String text) {
		matchText.setText(text);
	}

	/**
	 * @param state
	 *            The state of the ingore checkbox, <code>true</code> or
	 *            <code>false</code>
	 */
	public void setIgnoreCaseCheckbox(boolean state) {
		ignoreCaseCheckBox.setSelection(state);
	}

	/**
	 * @param state
	 *            The state of the multiline checkbox, <code>true</code> or
	 *            <code>false</code>
	 */
	public void setMultilineCheckbox(boolean state) {
		multilineCheckBox.setSelection(state);
	}
}