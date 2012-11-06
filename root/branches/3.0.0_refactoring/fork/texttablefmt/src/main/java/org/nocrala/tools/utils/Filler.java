package org.nocrala.tools.utils;

public class Filler {

  public static String getFiller(final int width) {
    return getFiller(" ", width);
  }

  public static String getFiller(final String txt, final int width) {
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < width; i++) {
      sb.append(txt);
    }
    return sb.toString();
  }

}
