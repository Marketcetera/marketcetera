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
package org.rubypeople.rdt.internal.ui.viewsupport;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.Assert;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.rubypeople.rdt.core.Flags;
import org.rubypeople.rdt.core.IMember;
import org.rubypeople.rdt.core.IMethod;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.ISourceFolderRoot;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.RubyPluginImages;
import org.rubypeople.rdt.internal.ui.RubyUIMessages;
import org.rubypeople.rdt.ui.RubyElementImageDescriptor;
import org.rubypeople.rdt.ui.viewsupport.ImageDescriptorRegistry;

/**
 * Default strategy of the Ruby plugin for the construction of Ruby element
 * icons.
 */
public class RubyElementImageProvider {

    /**
     * Flags for the RubyImageLabelProvider: Generate images with overlays.
     */
    public final static int OVERLAY_ICONS = 0x1;

    /**
     * Generate small sized images.
     */
    public final static int SMALL_ICONS = 0x2;

    /**
     * Use the 'light' style for rendering types.
     */
    public final static int LIGHT_TYPE_ICONS = 0x4;

    public static final Point SMALL_SIZE = new Point(16, 16);
    public static final Point BIG_SIZE = new Point(22, 16);

    private static ImageDescriptor DESC_OBJ_PROJECT_CLOSED;
    private static ImageDescriptor DESC_OBJ_PROJECT;
    {
        ISharedImages images = RubyPlugin.getDefault().getWorkbench().getSharedImages();
        DESC_OBJ_PROJECT_CLOSED = images
                .getImageDescriptor(IDE.SharedImages.IMG_OBJ_PROJECT_CLOSED);
        DESC_OBJ_PROJECT = images.getImageDescriptor(IDE.SharedImages.IMG_OBJ_PROJECT);
    }

    private ImageDescriptorRegistry fRegistry;

    public RubyElementImageProvider() {
        fRegistry = null; // lazy initialization
    }

    /**
     * Returns the icon for a given element. The icon depends on the element
     * type and element properties. If configured, overlay icons are constructed
     * for <code>ISourceReference</code>s.
     * 
     * @param flags
     *            Flags as defined by the RubyImageLabelProvider
     */
    public Image getImageLabel(Object element, int flags) {
        return getImageLabel(computeDescriptor(element, flags));
    }

    private Image getImageLabel(ImageDescriptor descriptor) {
        if (descriptor == null) return null;
        return getRegistry().get(descriptor);
    }

    private ImageDescriptorRegistry getRegistry() {
        if (fRegistry == null) {
            fRegistry = RubyPlugin.getImageDescriptorRegistry();
        }
        return fRegistry;
    }

    private ImageDescriptor computeDescriptor(Object element, int flags) {
        if (element instanceof IRubyElement) {
            return getRubyImageDescriptor((IRubyElement) element, flags);
        } else if (element instanceof IAdaptable) { 
        	return getWorkbenchImageDescriptor((IAdaptable) element, flags); 
        } else if (element instanceof IFile) {
            IFile file = (IFile) element;
            if (RubyCore.isRubyLikeFileName(file.getName())) { 
            	return getCUResourceImageDescriptor(
                    file, flags); // image for a ruby script not on the build path
            }
            return getWorkbenchImageDescriptor(file, flags);
        } 
        return null;
    }

    private static boolean showOverlayIcons(int flags) {
        return (flags & OVERLAY_ICONS) != 0;
    }

    private static boolean useSmallSize(int flags) {
        return (flags & SMALL_ICONS) != 0;
    }

    private static boolean useLightIcons(int flags) {
        return (flags & LIGHT_TYPE_ICONS) != 0;
    }

    /**
     * Returns an image descriptor for a compilation unit not on the class path.
     * The descriptor includes overlays, if specified.
     */
    public ImageDescriptor getCUResourceImageDescriptor(IFile file, int flags) {
        Point size = useSmallSize(flags) ? SMALL_SIZE : BIG_SIZE;
        return new RubyElementImageDescriptor(RubyPluginImages.DESC_OBJS_RUBY_RESOURCE, 0, size);
    }

    /**
     * Returns an image descriptor for a ruby element. The descriptor includes
     * overlays, if specified.
     */
    public ImageDescriptor getRubyImageDescriptor(IRubyElement element, int flags) {
        int adornmentFlags = computeRubyAdornmentFlags(element, flags);
        Point size = useSmallSize(flags) ? SMALL_SIZE : BIG_SIZE;
        return new RubyElementImageDescriptor(getBaseImageDescriptor(element, flags),
                adornmentFlags, size);
    }

    /**
     * Returns an image descriptor for a IAdaptable. The descriptor includes
     * overlays, if specified (only error ticks apply). Returns
     * <code>null</code> if no image could be found.
     */
    public ImageDescriptor getWorkbenchImageDescriptor(IAdaptable adaptable, int flags) {
        IWorkbenchAdapter wbAdapter = (IWorkbenchAdapter) adaptable
                .getAdapter(IWorkbenchAdapter.class);
        if (wbAdapter == null) { return null; }
        ImageDescriptor descriptor = wbAdapter.getImageDescriptor(adaptable);
        if (descriptor == null) { return null; }

        Point size = useSmallSize(flags) ? SMALL_SIZE : BIG_SIZE;
        return new RubyElementImageDescriptor(descriptor, 0, size);
    }

    // ---- Computation of base image key
    // -------------------------------------------------

    /**
     * Returns an image descriptor for a ruby element. This is the base image,
     * no overlays.
     */
    public ImageDescriptor getBaseImageDescriptor(IRubyElement element, int renderFlags) {
        try {
            switch (element.getElementType()) {
            case IRubyElement.METHOD: {
                IMethod method = (IMethod) element;
                IType declType = method.getDeclaringType();
                int flags = method.getVisibility();
                return getMethodImageDescriptor(flags);
            }
            case IRubyElement.GLOBAL:
                return RubyPluginImages.DESC_OBJS_GLOBAL;

            case IRubyElement.CLASS_VAR:
                return RubyPluginImages.DESC_OBJS_CLASS_VAR;

            case IRubyElement.CONSTANT:
                return RubyPluginImages.DESC_OBJS_CONSTANT;

            case IRubyElement.LOCAL_VARIABLE:
            case IRubyElement.DYNAMIC_VAR: // FIXME Make dynamic var have it's own image?
                return RubyPluginImages.DESC_OBJS_LOCAL_VAR;

            case IRubyElement.INSTANCE_VAR:
                return RubyPluginImages.DESC_OBJS_INSTANCE_VAR;

            case IRubyElement.IMPORT_DECLARATION:
                return RubyPluginImages.DESC_OBJS_IMPDECL;

            case IRubyElement.IMPORT_CONTAINER:
                return RubyPluginImages.DESC_OBJS_IMPCONT;

            case IRubyElement.TYPE: {
                IType type = (IType) element;
                
                IType declType = type.getDeclaringType();
                boolean isInner = declType != null;
                return getTypeImageDescriptor(type.isModule(), isInner, useLightIcons(renderFlags));
            }

            case IRubyElement.SCRIPT:
                return RubyPluginImages.DESC_OBJS_SCRIPT;
                
            case IRubyElement.SOURCE_FOLDER:            	
                return RubyPluginImages.DESC_OBJS_SOURCE_FOLDER;
                
            case IRubyElement.SOURCE_FOLDER_ROOT:            	
				ISourceFolderRoot root= (ISourceFolderRoot) element;
				if (root.isExternal()) {
					return RubyPluginImages.DESC_OBJS_LIBRARY;
				} else {
					return RubyPluginImages.DESC_OBJS_SOURCE_FOLDER_ROOT;
				}             
                

            case IRubyElement.RUBY_PROJECT:
                IRubyProject jp = (IRubyProject) element;
                if (jp.getProject().isOpen()) {
                    IProject project = jp.getProject();
                    IWorkbenchAdapter adapter = (IWorkbenchAdapter) project
                            .getAdapter(IWorkbenchAdapter.class);
                    if (adapter != null) {
                        ImageDescriptor result = adapter.getImageDescriptor(project);
                        if (result != null) return result;
                    }
                    return DESC_OBJ_PROJECT;
                }
                return DESC_OBJ_PROJECT_CLOSED;

            case IRubyElement.RUBY_MODEL:
                return RubyPluginImages.DESC_OBJS_RUBY_MODEL;
            }

            Assert.isTrue(false, RubyUIMessages.RubyImageLabelprovider_assert_wrongImage);
            return RubyPluginImages.DESC_OBJS_GHOST;
        } catch (RubyModelException e) {
            return RubyPluginImages.DESC_OBJS_UNKNOWN;
        }
    }

    public void dispose() {
    }

    // ---- Methods to compute the adornments flags
    // ---------------------------------

    private int computeRubyAdornmentFlags(IRubyElement element, int renderFlags) {
        int flags = 0;
        if (showOverlayIcons(renderFlags) && element instanceof IMember) {
            IMember member = (IMember) element;

            if (element.getElementType() == IRubyElement.METHOD
                    && ((IMethod) element).isConstructor())
                flags |= RubyElementImageDescriptor.CONSTRUCTOR;
            if (element.getElementType() == IRubyElement.METHOD
                    && ((IMethod) element).isSingleton())
                flags |= RubyElementImageDescriptor.STATIC;

        }
        return flags;
    }

    public static ImageDescriptor getMethodImageDescriptor(int flags) {		
        if (Flags.isPublic(flags)) return RubyPluginImages.DESC_MISC_PUBLIC;
        if (Flags.isProtected(flags)) return RubyPluginImages.DESC_MISC_PROTECTED;
        return RubyPluginImages.DESC_MISC_PRIVATE;
    }

    public static ImageDescriptor getTypeImageDescriptor(boolean isModule, boolean isInner, boolean useLightIcons) {
        if (isModule) { 
            if (useLightIcons) { return RubyPluginImages.DESC_OBJS_MODULEALT; }
            if (isInner) { return getInnerModuleImageDescriptor(); }
            return RubyPluginImages.DESC_OBJS_MODULE; }
        if (useLightIcons) { return RubyPluginImages.DESC_OBJS_CLASSALT; }
        if (isInner) { return getInnerClassImageDescriptor(); }
        return getClassImageDescriptor();
    }

    public static Image getDecoratedImage(ImageDescriptor baseImage, int adornments, Point size) {
        return RubyPlugin.getImageDescriptorRegistry().get(
                new RubyElementImageDescriptor(baseImage, adornments, size));
    }

    private static ImageDescriptor getClassImageDescriptor() {
        return RubyPluginImages.DESC_OBJS_CLASS;
    }

    private static ImageDescriptor getInnerClassImageDescriptor() {
        return RubyPluginImages.DESC_OBJS_INNER_CLASS;
    }
    
    private static ImageDescriptor getInnerModuleImageDescriptor() {
        return RubyPluginImages.DESC_OBJS_MODULE;
    }

    public static ImageDescriptor getConstantImageDescriptor() {
        return RubyPluginImages.DESC_OBJS_CONSTANT;
    }
    
    public static ImageDescriptor getClassVariableImageDescriptor() {
        return RubyPluginImages.DESC_OBJS_CLASS_VAR;
    }
    
    public static ImageDescriptor getInstanceVariableImageDescriptor() {
        return RubyPluginImages.DESC_OBJS_INSTANCE_VAR;
    }
    
    public static ImageDescriptor getGlobalVariableImageDescriptor() {
        return RubyPluginImages.DESC_OBJS_GLOBAL;
    }
}
