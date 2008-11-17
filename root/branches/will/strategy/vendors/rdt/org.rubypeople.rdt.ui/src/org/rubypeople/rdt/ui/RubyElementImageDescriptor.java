/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.ui;


import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.Assert;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.RubyPluginImages;

/**
 * A RubyImageDescriptor consists of a base image and several adornments. The adornments
 * are computed according to the flags either passed during creation or set via the method
 * <code>setAdornments</code>. 
 * 
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 *
 * @since 2.0 
 */
public class RubyElementImageDescriptor extends CompositeImageDescriptor {
    
    
    /** Flag to render the static adornment. */
    public final static int STATIC=         0x008;
    
    /** Flag to render the warning adornment. */
    public final static int WARNING=            0x020;
    
    /** Flag to render the error adornment. */
    public final static int ERROR=          0x040;
    
    /** Flag to render the 'override' adornment. */
    public final static int OVERRIDES=      0x080;
    
    /** Flag to render the 'implements' adornment. */
    public final static int IMPLEMENTS=         0x100;
    
    /** Flag to render the 'constructor' adornment. */
    public final static int CONSTRUCTOR=    0x200;
    
    /**
     * Flag to render the 'deprecated' adornment.
     * @since 0.8.0
     */
    public final static int DEPRECATED=     0x400;  

    private ImageDescriptor fBaseImage;
    private int fFlags;
    private Point fSize;

    /**
     * Creates a new RubyElementImageDescriptor.
     * 
     * @param baseImage an image descriptor used as the base image
     * @param flags flags indicating which adornments are to be rendered. See <code>setAdornments</code>
     *  for valid values.
     * @param size the size of the resulting image
     * @see #setAdornments(int)
     */
    public RubyElementImageDescriptor(ImageDescriptor baseImage, int flags, Point size) {
        fBaseImage= baseImage;
        Assert.isNotNull(fBaseImage);
        fFlags= flags;
        Assert.isTrue(fFlags >= 0);
        fSize= size;
        Assert.isNotNull(fSize);
    }
    
    /**
     * Sets the descriptors adornments. Valid values are: <code>ABSTRACT</code>, <code>FINAL</code>,
     * <code>SYNCHRONIZED</code>, </code>STATIC<code>, </code>RUNNABLE<code>, </code>WARNING<code>, 
     * </code>ERROR<code>, </code>OVERRIDDES<code>, <code>IMPLEMENTS</code>, <code>CONSTRUCTOR</code>,
     * <code>DEPRECATED</code>,  or any combination of those.
     * 
     * @param adornments the image descriptors adornments
     */
    public void setAdornments(int adornments) {
        Assert.isTrue(adornments >= 0);
        fFlags= adornments;
    }

    /**
     * Returns the current adornments.
     * 
     * @return the current adornments
     */
    public int getAdronments() {
        return fFlags;
    }

    /**
     * Sets the size of the image created by calling <code>createImage()</code>.
     * 
     * @param size the size of the image returned from calling <code>createImage()</code>
     * @see ImageDescriptor#createImage()
     */
    public void setImageSize(Point size) {
        Assert.isNotNull(size);
        Assert.isTrue(size.x >= 0 && size.y >= 0);
        fSize= size;
    }
    
    /**
     * Returns the size of the image created by calling <code>createImage()</code>.
     * 
     * @return the size of the image created by calling <code>createImage()</code>
     * @see ImageDescriptor#createImage()
     */
    public Point getImageSize() {
        return new Point(fSize.x, fSize.y);
    }
    
    /* (non-Javadoc)
     * Method declared in CompositeImageDescriptor
     */
    protected Point getSize() {
        return fSize;
    }
    
    /* (non-Javadoc)
     * Method declared on Object.
     */
    public boolean equals(Object object) {
        if (object == null || !RubyElementImageDescriptor.class.equals(object.getClass()))
            return false;
            
        RubyElementImageDescriptor other= (RubyElementImageDescriptor)object;
        return (fBaseImage.equals(other.fBaseImage) && fFlags == other.fFlags && fSize.equals(other.fSize));
    }
    
    /* (non-Javadoc)
     * Method declared on Object.
     */
    public int hashCode() {
        return fBaseImage.hashCode() | fFlags | fSize.hashCode();
    }
    
    /* (non-Javadoc)
     * Method declared in CompositeImageDescriptor
     */
    protected void drawCompositeImage(int width, int height) {
        ImageData bg= getImageData(fBaseImage);
            
        
        if ((fFlags & DEPRECATED) != 0) { // over the full image
            Point size= getSize();
            ImageData data= getImageData(RubyPluginImages.DESC_OVR_DEPRECATED);
            drawImage(data, 0, size.y - data.height);
        }
        drawImage(bg, 0, 0);
                
        drawTopRight();
        drawBottomRight();
        drawBottomLeft();
        

    }
    
    private ImageData getImageData(ImageDescriptor descriptor) {
        ImageData data= descriptor.getImageData(); // see bug 51965: getImageData can return null
        if (data == null) {
            data= DEFAULT_IMAGE_DATA;
            RubyPlugin.logErrorMessage("Image data not available: " + descriptor.toString()); //$NON-NLS-1$
        }
        return data;
    }
    
    
    private void drawTopRight() {       
        int x= getSize().x;
        if ((fFlags & CONSTRUCTOR) != 0) {
            ImageData data= getImageData(RubyPluginImages.DESC_OVR_CONSTRUCTOR);
            x-= data.width;
            drawImage(data, x, 0);
        }
        if ((fFlags & STATIC) != 0) {
            ImageData data= getImageData(RubyPluginImages.DESC_OVR_STATIC);
            x-= data.width;
            drawImage(data, x, 0);
        }
    }       
    
    private void drawBottomRight() {
        Point size= getSize();
        int x= size.x;
        int flags= fFlags;
        
        if ((flags & OVERRIDES) != 0) {
            ImageData data= getImageData(RubyPluginImages.DESC_OVR_OVERRIDES);
            x-= data.width;
            drawImage(data, x, size.y - data.height);
        }
        if ((flags & IMPLEMENTS) != 0) {
            ImageData data= getImageData(RubyPluginImages.DESC_OVR_IMPLEMENTS);
            x-= data.width;
            drawImage(data, x, size.y - data.height);
        }
    }       
    
    private void drawBottomLeft() {
        Point size= getSize();
        int x= 0;
        if ((fFlags & ERROR) != 0) {
            ImageData data= getImageData(RubyPluginImages.DESC_OVR_ERROR);
            drawImage(data, x, size.y - data.height);
            x+= data.width;
        }
        if ((fFlags & WARNING) != 0) {
            ImageData data= getImageData(RubyPluginImages.DESC_OVR_WARNING);
            drawImage(data, x, size.y - data.height);
            x+= data.width;
        }

    }       
}
