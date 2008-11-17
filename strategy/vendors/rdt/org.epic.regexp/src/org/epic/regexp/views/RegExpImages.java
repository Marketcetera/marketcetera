package org.epic.regexp.views;

import org.eclipse.jface.resource.ImageDescriptor;

import org.epic.regexp.RegExpPlugin;

import java.net.MalformedURLException;
import java.net.URL;


/**
 * Convenience class for storing references to image descriptors used by the JS editor.
 * 
 * @author Addi
 */
public class RegExpImages
{
   static final URL BASE_URL = RegExpPlugin.getDefault().getBundle().getEntry("/");
   static final String iconPath = "icons/";
   
   public static final ImageDescriptor ICON_VIEW = createImageDescriptor(iconPath + "rx.png");
   public static final ImageDescriptor ICON_RUN = createImageDescriptor(iconPath + "run.gif");
   public static final ImageDescriptor RESULT_GRAY = createImageDescriptor(iconPath + "result_gray.png");
   public static final ImageDescriptor RESULT_GREEN = createImageDescriptor(iconPath + "result_green.png");
   public static final ImageDescriptor RESULT_RED = createImageDescriptor(iconPath + "result_red.png");
   public static final ImageDescriptor EDIT_CUT = createImageDescriptor(iconPath + "cut_edit.gif");
   public static final ImageDescriptor EDIT_COPY = createImageDescriptor(iconPath + "copy_edit.gif");
   public static final ImageDescriptor EDIT_PASTE = createImageDescriptor(iconPath + "paste_edit.gif");
   public static final ImageDescriptor ICON_DEBUG_STOP = createImageDescriptor(iconPath + "debug_stop.gif");
   public static final ImageDescriptor ICON_DEBUG_BACK = createImageDescriptor(iconPath + "debug_back.gif");
   public static final ImageDescriptor ICON_DEBUG_FORWARD = createImageDescriptor(iconPath + "debug_forward.gif");
  
   /**
    * Utility method to create an <code>ImageDescriptor</code> from a path to a file.
    * @param path
    * 
    * @return
    */
   private static ImageDescriptor createImageDescriptor(String path)
   {
      try
      {
         URL url = new URL(BASE_URL, path);

         return ImageDescriptor.createFromURL(url);
      }
      catch(MalformedURLException e)
      {
      }

      return ImageDescriptor.getMissingImageDescriptor();
   }
}