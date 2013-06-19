package org.nocrala.tools.utils;

public class TextEncoder {

  public static String escapeXml(final String txt) {
    if (txt == null) {
      return null;
    }
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < txt.length(); i++) {
      char c = txt.charAt(i);
      if (c < 32 || c > 127 || c == '<' || c == '>' || c == '&' || c == '\''
          || c == '\"') {
        sb.append("&#" + (int) c + ";");
      } else {
        sb.append(c);
      }
    }
    return sb.toString();
  }

}
