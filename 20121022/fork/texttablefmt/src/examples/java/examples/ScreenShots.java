package examples;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.nocrala.tools.texttablefmt.BorderStyle;
import org.nocrala.tools.texttablefmt.CellStyle;
import org.nocrala.tools.texttablefmt.ShownBorders;
import org.nocrala.tools.texttablefmt.Table;
import org.nocrala.tools.texttablefmt.CellStyle.HorizontalAlign;

public class ScreenShots {

  private static final String USAGE = "ScreenShots <output-dir>";

  public static void main(final String[] args) throws IOException {
    if (args.length != 1) {
      System.out.println(USAGE);
      System.exit(1);
    }
    File basedir = new File(args[0]);
    if (!basedir.exists()) {
      System.out.println("Directory '" + args[0] + "' does not exist.");
      System.exit(1);
    }

    generateBasic(basedir);
    generateAdvanced(basedir);
    generateFancy(basedir);
    generateUnicode(basedir);
  }

  public static void generateBasic(final File basedir) throws IOException {
    Table t = new Table(3);
    t.addCell("Artist");
    t.addCell("Album");
    t.addCell("Year");
    t.addCell("Jamiroquai");
    t.addCell("Emergency on Planet Earth");
    t.addCell("1993");
    t.addCell("Jamiroquai");
    t.addCell("The Return of the Space Cowboy");
    t.addCell("1994");
    writeToFile(basedir, t.render(), "basic.txt");
  }

  public static void generateAdvanced(final File basedir) throws IOException {
    CellStyle numberStyle = new CellStyle(HorizontalAlign.right);

    Table t = new Table(3, BorderStyle.DESIGN_FORMAL,
        ShownBorders.SURROUND_HEADER_FOOTER_AND_COLUMNS);
    t.setColumnWidth(0, 6, 14);
    t.setColumnWidth(1, 4, 12);
    t.setColumnWidth(2, 4, 12);

    t.addCell("Region");
    t.addCell("Orders", numberStyle);
    t.addCell("Sales", numberStyle);

    t.addCell("North");
    t.addCell("6,345", numberStyle);
    t.addCell("$87.230", numberStyle);

    t.addCell("Center");
    t.addCell("837", numberStyle);
    t.addCell("$12.855", numberStyle);

    t.addCell("South");
    t.addCell("5,344", numberStyle);
    t.addCell("$72.561", numberStyle);

    t.addCell("Total", numberStyle, 2);
    t.addCell("$172.646", numberStyle);

    writeToFile(basedir, t.render(), "advanced.txt");
  }

  public static void generateFancy(final File basedir) throws IOException {
    CellStyle numberStyle = new CellStyle(HorizontalAlign.right);

    Table t = new Table(3, BorderStyle.DESIGN_PAPYRUS,
        ShownBorders.SURROUND_HEADER_FOOTER_AND_COLUMNS);
    t.addCell("Region");
    t.addCell("Orders", numberStyle);
    t.addCell("Sales", numberStyle);

    t.addCell("North");
    t.addCell("6,345", numberStyle);
    t.addCell("$87.230", numberStyle);

    t.addCell("Center");
    t.addCell("837", numberStyle);
    t.addCell("$12.855", numberStyle);

    t.addCell("South");
    t.addCell("5,344", numberStyle);
    t.addCell("$72.561", numberStyle);

    t.addCell("Total", numberStyle, 2);
    t.addCell("$172.646", numberStyle);

    writeToFile(basedir, t.render(), "fancy.txt");
  }

  private static void generateUnicode(final File basedir) throws IOException {
    CellStyle numberStyle = new CellStyle(HorizontalAlign.right);

    Table t = new Table(3, BorderStyle.UNICODE_BOX_DOUBLE_BORDER_WIDE,
        ShownBorders.SURROUND_HEADER_FOOTER_AND_COLUMNS, true);

    t.addCell("Region");
    t.addCell("Orders", numberStyle);
    t.addCell("Sales", numberStyle);

    t.addCell("North");
    t.addCell("6,345", numberStyle);
    t.addCell("$87.230", numberStyle);

    t.addCell("Center");
    t.addCell("837", numberStyle);
    t.addCell("$12.855", numberStyle);

    t.addCell("South");
    t.addCell("5,344", numberStyle);
    t.addCell("$72.561", numberStyle);

    t.addCell("Total", numberStyle, 2);
    t.addCell("$172.646", numberStyle);

    String[] result = t.renderAsStringArray();
    StringBuffer sb = new StringBuffer("<html><body><pre>");
    for (String line : result) {
      sb.append(line);
      sb.append("<br>");
    }
    sb.append("</pre></html>");

    writeToFile(basedir, sb.toString(), "unicode.html");
  }

  private static void writeToFile(final File basedir, final String text,
      final String fileName) throws IOException {
    BufferedWriter w = new BufferedWriter(new OutputStreamWriter(
        new FileOutputStream(new File(basedir, fileName)), "UTF-8"));
    w.write(text);
    w.close();
  }

}
