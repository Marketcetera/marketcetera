package org.marketcetera.photon.ui.databinding;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.graphics.Image;
import org.marketcetera.photon.commons.databinding.TypedConverter;
import org.marketcetera.photon.commons.ui.databinding.RequiredFieldSupport.RequiredStatus;
import org.marketcetera.util.misc.ClassVersion;

/**
 * Converts a status to an appropriate image.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public class StatusToImageConverter extends TypedConverter<IStatus, Image> {

    /**
     * Constructor.
     */
    public StatusToImageConverter() {
        super(IStatus.class, Image.class);
    }

    @Override
    protected Image doConvert(IStatus fromObject) {
        if (fromObject == null)
            return null;

        String fieldDecorationID = null;
        switch (fromObject.getSeverity()) {
        case IStatus.INFO:
            fieldDecorationID = FieldDecorationRegistry.DEC_INFORMATION;
            break;
        case IStatus.WARNING:
            fieldDecorationID = FieldDecorationRegistry.DEC_WARNING;
            break;
        case IStatus.ERROR:
            if (fromObject instanceof RequiredStatus) {
                fieldDecorationID = FieldDecorationRegistry.DEC_REQUIRED;
                break;
            }
        case IStatus.CANCEL:
            fieldDecorationID = FieldDecorationRegistry.DEC_ERROR;
            break;
        default:
            /*
             * No decoration needed.
             */
            return null;
        }
        FieldDecoration fieldDecoration = FieldDecorationRegistry.getDefault()
                .getFieldDecoration(fieldDecorationID);
        return fieldDecoration == null ? null : fieldDecoration.getImage();
    }
}
