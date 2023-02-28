package org.marketcetera.ui.service;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.stage.Stage;

/* $License$ */

/**
 * Manages the desktop viewable area parameters.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class DesktopParameters
{
    /**
     * Create a new DesktopParameters instance.
     *
     * @param inMainStage
     */
    public DesktopParameters(Stage inMainStage)
    {
        mainStage = inMainStage;
        mainStage.widthProperty().addListener(new ChangeListener< Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> inObservable,
                                Number inOldValue,
                                Number inNewValue)
            {
                recalculate();
            }}
        );
        mainStage.heightProperty().addListener(new ChangeListener< Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> inObservable,
                                Number inOldValue,
                                Number inNewValue)
            {
                recalculate();
            }}
        );
    }
    /**
     * Get the top value.
     *
     * @return a <code>double</code> value
     */
    public double getTop()
    {
        return top;
    }
    /**
     * Sets the top value.
     *
     * @param doubleop a <code>double</code> value
     */
    public void setTop(double doubleop)
    {
        top = doubleop;
    }
    /**
     * Get the left value.
     *
     * @return a <code>double</code> value
     */
    public double getLeft()
    {
        return left;
    }
    /**
     * Sets the left value.
     *
     * @param inLeft a <code>double</code> value
     */
    public void setLeft(double inLeft)
    {
        left = inLeft;
    }
    /**
     * Get the bottom value.
     *
     * @return a <code>double</code> value
     */
    public double getBottom()
    {
        return bottom;
    }
    /**
     * Sets the bottom value.
     *
     * @param inBottom a <code>double</code> value
     */
    public void setBottom(double inBottom)
    {
        bottom = inBottom;
    }
    /**
     * Get the right value.
     *
     * @return a <code>double</code> value
     */
    public double getRight()
    {
        return right;
    }
    /**
     * Sets the right value.
     *
     * @param inRight a <code>double</code> value
     */
    public void setRight(double inRight)
    {
        right = inRight;
    }
    /**
     * Recalculate the dynamic parameters.
     */
    public void recalculate()
    {
        top = mainStage.getY();
        left = mainStage.getX();
        bottom = mainStage.getY() + mainStage.getHeight();
        right = mainStage.getX() + mainStage.getWidth();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("DesktopParameters [top=").append(top).append(", left=").append(left).append(", bottom=")
                .append(bottom).append(", right=").append(right).append("]");
        return builder.toString();
    }
    /**
     * main stage object
     */
    private final Stage mainStage;
    /**
     * desktop viewable area top edge coordinate
     */
    private double top = 0;
    /**
     * desktop viewable area left edge coordinate
     */
    private double left = 0;
    /**
     * desktop viewable area bottom edge coordinate
     */
    private double bottom;
    /**
     * desktop viewable area right edge coordinate
     */
    private double right;
}
