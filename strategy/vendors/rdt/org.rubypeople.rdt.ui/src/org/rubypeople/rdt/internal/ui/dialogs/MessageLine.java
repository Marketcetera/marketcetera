package org.rubypeople.rdt.internal.ui.dialogs;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.rubypeople.rdt.internal.ui.RubyPluginImages;

/**
 * A message line displaying a status.
 */
public class MessageLine extends CLabel
{

	private Color fNormalMsgAreaBackground;
	private Color fErrorMsgAreaBackground;

	/**
	 * Creates a new message line as a child of the given parent.
	 * 
	 * @param parent
	 */
	public MessageLine(Composite parent)
	{
		this(parent, SWT.LEFT);
	}

	/**
	 * Sets the background when an error message is present
	 * 
	 * @param color
	 */
	public void setErrorBackground(Color color)
	{
		fErrorMsgAreaBackground = color;
	}

	/**
	 * Creates a new message line as a child of the parent and with the given SWT stylebits.
	 * 
	 * @param parent
	 * @param style
	 */
	public MessageLine(Composite parent, int style)
	{
		super(parent, style);
		fNormalMsgAreaBackground = getBackground();
		fErrorMsgAreaBackground = null;
	}

	private Image findImage(IStatus status)
	{
		if (status.isOK())
		{
			return null;
		}
		else if (status.matches(IStatus.ERROR))
		{
			return RubyPluginImages.get(RubyPluginImages.IMG_OBJS_ERROR);
		}
		else if (status.matches(IStatus.WARNING))
		{
			return RubyPluginImages.get(RubyPluginImages.IMG_OBJS_WARNING);
		}
		else if (status.matches(IStatus.INFO))
		{
			return RubyPluginImages.get(RubyPluginImages.IMG_OBJS_INFO);
		}
		return null;
	}

	/**
	 * Sets the message and image to the given status. <code>null</code> is a valid argument and will set the empty
	 * text and no image
	 * 
	 * @param status
	 */
	public void setErrorStatus(IStatus status)
	{
		if (status != null)
		{
			String message = status.getMessage();
			if (message != null && message.length() > 0)
			{
				setText(message);
				setImage(findImage(status));
				if (fErrorMsgAreaBackground == null)
				{
					setBackground(fNormalMsgAreaBackground);
				}
				else
				{
					setBackground(fErrorMsgAreaBackground);
				}
				return;
			}
		}
		setText("");
		setImage(null);
		setBackground(fNormalMsgAreaBackground);
	}

}