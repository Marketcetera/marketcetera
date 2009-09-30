package org.marketcetera.photon.commons.ui;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Map;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import org.eclipse.jface.resource.ColorDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.widgets.Display;
import org.marketcetera.photon.commons.Validate;
import org.marketcetera.util.misc.ClassVersion;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/* $License$ */

/**
 * Manages colors in a thread safe way. There are only two states for this
 * object:
 * <ol>
 * <li>not initialized - getColor returns null for all ColorDescriptors and
 * IColorDescriptorProviders.</li>
 * <li>initialized - getColor returns a valid Color for all ColorDescriptors
 * that were used to create the manager.</li>
 * </ol>
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ThreadSafe
@ClassVersion("$Id$")
public class ColorManager {

    /**
     * An object that provides a {@link ColorDescriptor}.
     */
    @ClassVersion("$Id$")
    public interface IColorDescriptorProvider {

        /**
         * Returns a ColorDescriptor that can be used to create a {@link Color}.
         * 
         * @return the color descriptor
         */
        ColorDescriptor getDescriptor();
    }

    /**
     * Creates a ColorManager from an enum that implements
     * {@link IColorDescriptorProvider}.
     * 
     * @param <E>
     *            the enum type
     * @param clazz
     *            the enum class
     * @return a ColorManager for the ColorDescriptors provided by the enum
     *         instances
     * @throws IllegalStateException
     *             if any enum instance provides a null ColorDescriptor
     */
    public static <E extends Enum<E> & IColorDescriptorProvider> ColorManager createForEnum(Class<E> clazz) {
        Collection<IColorDescriptorProvider> providers = Lists.newArrayList();
        for (IColorDescriptorProvider provider : EnumSet.allOf(clazz)) {
            providers.add(provider);
        }
        return createForProviders(providers);
    }

    /**
     * Creates a ColorManager from an collection of IColorDescriptorProviders.
     * 
     * @param providers
     *            the IColorDescriptorProviders that provide the
     *            ColorDescriptors
     * @return a ColorManager for the ColorDescriptors provided by the providers
     * @throws IllegalArgumentException
     *             if any provider is null or provides a null ColorDescriptor
     */
    public static ColorManager createForProviders(
            Collection<IColorDescriptorProvider> providers) {
        Validate.noNullElements(providers, "providers"); //$NON-NLS-1$
        Collection<ColorDescriptor> descriptors = Lists.newArrayList();
        for (IColorDescriptorProvider provider : providers) {
            final ColorDescriptor descriptor = provider.getDescriptor();
            if (descriptor == null) {
                throw new IllegalArgumentException(
                        Messages.COLOR_MANAGER_PROVIDER_NULL_DESCRIPTOR
                                .getText(provider));
            }
            descriptors.add(descriptor);
        }
        return createFor(descriptors);
    }

    /**
     * Creates a ColorManager from an collection of ColorDescriptors.
     * 
     * @param descriptors
     *            the ColorDescriptors to manage
     * @return a ColorManager for the provided ColorDescriptors
     * @throws IllegalArgumentException
     *             if descriptors is null or contains any null elements
     */
    public static ColorManager createFor(Collection<ColorDescriptor> descriptors) {
        Validate.noNullElements(descriptors, "descriptors"); //$NON-NLS-1$
        return new ColorManager(descriptors);
    }

    /**
     * Returns the Color for the provider's descriptor. Clients must not dispose
     * the returned Color.
     * 
     * @param provider
     *            the color descriptor provider
     * @return the color, or null if the manager is not initialized
     * @throws IllegalArgumentException
     *             if the provider is null, if the provider's descriptor is
     *             null, or if the provider's descriptor is not managed by this
     *             class
     */
    public Color getColor(IColorDescriptorProvider provider) {
        Validate.notNull(provider, "provider"); //$NON-NLS-1$
        return getColor(provider.getDescriptor());
    }

    /**
     * Returns the Color for the color descriptor.
     * 
     * @param descriptor
     *            the color descriptor
     * @return the color, or null if the manager is not initialized
     * @throws IllegalArgumentException
     *             if the descriptor is null, or is not managed by this class
     */
    public Color getColor(ColorDescriptor descriptor) {
        Validate.notNull(descriptor, "descriptor"); //$NON-NLS-1$
        if (!mColorDescriptors.contains(descriptor)) {
            throw new IllegalArgumentException(
                    Messages.COLOR_MANAGER_UNKNOWN_DESCRIPTOR
                            .getText(descriptor));
        }
        synchronized (this) {
            if (mResources == null) {
                return null;
            } else {
                return mResources.get(descriptor);
            }
        }
    }

    /**
     * Initializes the colors for the current {@link Display}. Calling multiple
     * times on the same thread is a no-op unless the current display has
     * changed, in which case an IllegalStateException will be thrown. The
     * exception can be avoided by first calling {@link #disposeColors()};
     * <p>
     * If this method completes successfully, {@link #getColor(ColorDescriptor)} will return a
     * valid color for each ColorDescriptor that was used to instantiate this
     * manager.
     * 
     * @throws IllegalStateException
     *             if called from a non-UI thread, i.e. one for which
     *             Display.getCurrent() is null
     * @throws IllegalStateException
     *             on subsequent invocations from the same thread if
     *             Display.getCurrent() has changed since the previous call
     */
    public void initColors() {
        SWTUtils.checkThread();
        Display display = Display.getCurrent();
        synchronized (this) {
            if (mResources != null) {
                if (!mResources.isInitializedFor(display)) {
                    throw new IllegalStateException(
                            Messages.COLOR_MANAGER_ALREADY_INITIALIZED
                                    .getText());
                } else {
                    return;
                }
            }
            mResources = new Resources(JFaceResources.getResources(display));
        }
    }

    /**
     * Disposes the colors. After this method completes, {@link #getColor(ColorDescriptor)}
     * will return null for all ColorDescriptors and {@link #initColors()} may
     * be safely called again on any UI thread.
     * <p>
     * This will automatically be called during disposal of the initializing
     * thread's {@link Display}, but can be called sooner to clean up resource
     * handles.
     * <p>
     * Successive calls are a no-op if {@link #initColors()} is not called.
     */
    public synchronized void disposeColors() {
        if (mResources != null) {
            mResources.dispose();
            mResources = null;
        }
    }

    /**
     * the color descriptors being managed
     */
    private final ImmutableCollection<ColorDescriptor> mColorDescriptors;

    /**
     * Constructor.
     * 
     * @param descriptors
     *            the color descriptors to manage
     */
    private ColorManager(Collection<ColorDescriptor> descriptors) {
        mColorDescriptors = ImmutableSet.copyOf(descriptors);
    }

    /**
     * mutable state
     */
    @GuardedBy("this")
    private Resources mResources;

    /**
     * Encapsulates mutable state. This object can be disposed manually via
     * {@link #dispose()} or automatically with the root {@link ResourceManager}
     * provided in the constructor.
     */
    @ClassVersion("$Id$")
    private final class Resources {
        private final Map<ColorDescriptor, Color> mColors;
        private final ResourceManager mRootResourceManager;
        private final ResourceManager mLocalResourceManager;
        private final Runnable mDisposeRunnable = new Runnable() {
            @Override
            public void run() {
                // dispose the ColorManager which will safely dispose this
                // Resources instance and clean up the thread local state
                disposeColors();
            }
        };

        /**
         * Constructor. If construction completes successfully, then this object
         * will provide valid colors via {@link #get(ColorDescriptor)}. If
         * construction fails, there are no side effects.
         * 
         * @param rootResourceManager
         *            root resource manager to use for creating colors
         */
        public Resources(ResourceManager rootResourceManager) {
            mRootResourceManager = rootResourceManager;
            mLocalResourceManager = new LocalResourceManager(
                    rootResourceManager);
            mColors = Maps.newHashMap();
            boolean success = false;
            try {
                for (ColorDescriptor descriptor : mColorDescriptors) {
                    mColors.put(descriptor, mLocalResourceManager
                            .createColor(descriptor));
                }
                mRootResourceManager.disposeExec(mDisposeRunnable);
                success = true;
            } finally {
                if (!success) {
                    dispose();
                }
            }
        }

        public boolean isInitializedFor(Device device) {
            return mLocalResourceManager.getDevice().equals(device);
        }

        public Color get(ColorDescriptor descriptor) {
            return mColors.get(descriptor);
        }

        public void dispose() {
            // this may be called from the dispose runnable, but it is safe to
            // call cancel regardless
            mRootResourceManager.cancelDisposeExec(mDisposeRunnable);
            mLocalResourceManager.dispose();
        }
    }
}
