package org.rubypeople.rdt.ui;

import org.eclipse.core.resources.IStorage;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.rubypeople.rdt.internal.ui.viewsupport.RubyElementImageProvider;
import org.rubypeople.rdt.internal.ui.viewsupport.StorageLabelProvider;

public class RubyElementLabelProvider extends LabelProvider {

    /**
     * Flag (bit mask) indicating that the label should include overlay icons
     * for element type and modifiers.
     */
    public final static int SHOW_OVERLAY_ICONS = 0x002;

    /**
     * Flag (bit mask) indicating that the label should include the name of the
     * package fragment root (appended).
     */
    public final static int SHOW_ROOT = 0x004;

    /**
     * Flag (bit mask) indicating that the label should show the icons with no
     * space reserved for overlays.
     */
    public final static int SHOW_SMALL_ICONS = 0x008;

    /**
     * Flag (bit mask) indicating that the package fragment roots from class
     * path variables should be rendered with the variable in the name
     */
    public final static int SHOW_VARIABLE = 0x010;

    /**
     * Flag (bit mask) indicating that compilation units, class files, types,
     * declarations and members should be rendered qualified. Examples:
     * <code>java.lang.String</code>, <code>java.util.Vector.size()</code>
     * 
     * @since 2.0
     */
    public final static int SHOW_QUALIFIED = 0x020;

    /**
     * Flag (bit mask) indicating that compilation units, class files, types,
     * declarations and members should be rendered qualified.The qualification
     * is appended. Examples: <code>String - java.lang</code>,
     * <code>size() - java.util.Vector</code>
     * 
     * @since 2.0
     */
    public final static int SHOW_POST_QUALIFIED = 0x040;

    /**
     * Constant (value <code>0</code>) indicating that the label should show
     * the basic images only.
     */
    public final static int SHOW_BASICS = 0x000;

    /**
     * Constant indicating the default label rendering. Currently the default is
     * equivalent to <code>SHOW_OVERLAY_ICONS</code>.
     */
    public final static int SHOW_DEFAULT = new Integer(SHOW_OVERLAY_ICONS)
            .intValue();

    private RubyElementImageProvider fImageLabelProvider;

    private StorageLabelProvider fStorageLabelProvider;
    private int fFlags;
    private int fImageFlags;
    private long fTextFlags;

    /**
     * Creates a new label provider with <code>SHOW_DEFAULT</code> flag.
     * 
     * @see #SHOW_DEFAULT
     * @since 0.8.0
     */
    public RubyElementLabelProvider() {
        this(SHOW_DEFAULT);
    }

    /**
     * Creates a new label provider.
     * 
     * @param flags
     *            the initial options; a bitwise OR of <code>SHOW_* </code>
     *            constants
     */
    public RubyElementLabelProvider(int flags) {
        fImageLabelProvider = new RubyElementImageProvider();
        fStorageLabelProvider = new StorageLabelProvider();
        fFlags = flags;
        updateImageProviderFlags();
        updateTextProviderFlags();
    }

    private boolean getFlag(int flag) {
        return (fFlags & flag) != 0;
    }

    /**
     * Turns on the rendering options specified in the given flags.
     * 
     * @param flags
     *            the options; a bitwise OR of <code>SHOW_* </code> constants
     */
    public void turnOn(int flags) {
        fFlags |= flags;
        updateImageProviderFlags();
        updateTextProviderFlags();
    }

    /**
     * Turns off the rendering options specified in the given flags.
     * 
     * @param flags
     *            the initial options; a bitwise OR of <code>SHOW_* </code>
     *            constants
     */
    public void turnOff(int flags) {
        fFlags &= (~flags);
        updateImageProviderFlags();
        updateTextProviderFlags();
    }

    private void updateImageProviderFlags() {
        fImageFlags = 0;
        if (getFlag(SHOW_OVERLAY_ICONS)) {
            fImageFlags |= RubyElementImageProvider.OVERLAY_ICONS;
        }
        if (getFlag(SHOW_SMALL_ICONS)) {
            fImageFlags |= RubyElementImageProvider.SMALL_ICONS;
        }
    }

    private void updateTextProviderFlags() {
        fTextFlags = RubyElementLabels.M_PARAMETER_NAMES;
        if (getFlag(SHOW_ROOT)) {
            fTextFlags |= RubyElementLabels.APPEND_ROOT_PATH;
        }
        if (getFlag(SHOW_VARIABLE)) {
            fTextFlags |= RubyElementLabels.ROOT_VARIABLE;
        }
        if (getFlag(SHOW_QUALIFIED)) {
            fTextFlags |= (RubyElementLabels.F_FULLY_QUALIFIED
                    | RubyElementLabels.M_FULLY_QUALIFIED | RubyElementLabels.I_FULLY_QUALIFIED
                    | RubyElementLabels.T_FILENAME_QUALIFIED | RubyElementLabels.D_QUALIFIED
                    | RubyElementLabels.CF_QUALIFIED | RubyElementLabels.CU_QUALIFIED);
        }
        if (getFlag(SHOW_POST_QUALIFIED)) {
            fTextFlags |= (RubyElementLabels.F_POST_QUALIFIED | RubyElementLabels.M_POST_QUALIFIED
                    | RubyElementLabels.I_POST_QUALIFIED | RubyElementLabels.T_POST_QUALIFIED
                    | RubyElementLabels.D_POST_QUALIFIED | RubyElementLabels.CF_POST_QUALIFIED | RubyElementLabels.CU_POST_QUALIFIED);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see ILabelProvider#getImage
     */
    public Image getImage(Object element) {
        Image result = fImageLabelProvider.getImageLabel(element, fImageFlags);
        if (result != null) { return result; }

        if (element instanceof IStorage) return fStorageLabelProvider.getImage(element);

        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ILabelProvider#getText
     */
    public String getText(Object element) {
        String text = RubyElementLabels.getTextLabel(element, fTextFlags);
        if (text.length() > 0) { return text; }

        if (element instanceof IStorage) return fStorageLabelProvider.getText(element);

        return text;
    }

    /*
     * (non-Javadoc)
     * 
     * @see IBaseLabelProvider#dispose
     */
    public void dispose() {
        fStorageLabelProvider.dispose();
        fImageLabelProvider.dispose();
    }

}
