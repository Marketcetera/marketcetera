package org.nocrala.tools.texttablefmt;

import java.io.IOException;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.nocrala.tools.texttablefmt.CellStyle.AbbreviationStyle;
import org.nocrala.tools.texttablefmt.CellStyle.HorizontalAlign;
import org.nocrala.tools.texttablefmt.CellStyle.NullStyle;

public class StreamingTableTests extends TestCase {

  private static Logger logger = Logger.getLogger(StreamingTableTests.class);

  public StreamingTableTests(final String txt) {
    super(txt);
  }

  public void testEmpty() throws IOException {
    logger.debug("Empty");
    StringBuffer sb = new StringBuffer();
    StreamingTable t = new StreamingTable(sb, 10, BorderStyle.CLASSIC,
        ShownBorders.ALL, false, "");
    t.finishTable();
    assertEquals("", sb.toString());
  }

  public void testOneCell() throws IOException {
    CellStyle cs = new CellStyle(HorizontalAlign.left, AbbreviationStyle.crop,
        NullStyle.emptyString);
    StringBuffer sb = new StringBuffer();
    StreamingTable t = new StreamingTable(sb, 1, BorderStyle.CLASSIC,
        ShownBorders.ALL, false, "");
    logger.debug("width[0]= " + t.getColumn(0).getColumnWidth());
    t.setColumnWidth(0, 11);
    t.addCell("abcdef", cs);

    t.finishTable();
    logger.info("sb=\n" + sb.toString());
    assertEquals("" //
        + "+-----------+\n" //
        + "|abcdef     |\n" //
        + "+-----------+", sb.toString());
  }

  public void testTwoCellsHorizontal() throws IOException {
    CellStyle cs = new CellStyle(HorizontalAlign.left, AbbreviationStyle.crop,
        NullStyle.emptyString);
    StringBuffer sb = new StringBuffer();
    StreamingTable t = new StreamingTable(sb, 2, BorderStyle.CLASSIC,
        ShownBorders.ALL, false, "");
    t.addCell("abcdef", cs);
    t.addCell("123456", cs);
    t.finishTable();
    assertEquals("+----------+----------+\n" + "|abcdef    |123456    |\n"
        + "+----------+----------+", sb.toString());
  }

  public void testTwoCellsVertical() throws IOException {
    CellStyle cs = new CellStyle(HorizontalAlign.left, AbbreviationStyle.crop,
        NullStyle.emptyString);
    StringBuffer sb = new StringBuffer();
    StreamingTable t = new StreamingTable(sb, 1, BorderStyle.CLASSIC,
        ShownBorders.ALL, false, "");
    t.addCell("abcdef", cs);
    t.addCell("123456", cs);
    t.finishTable();
    assertEquals("+----------+\n" + "|abcdef    |\n" + "+----------+\n"
        + "|123456    |\n" + "+----------+", sb.toString());
  }

  public void testMarginPrompt() throws IOException {
    CellStyle cs = new CellStyle(HorizontalAlign.left, AbbreviationStyle.crop,
        NullStyle.emptyString);
    StringBuffer sb = new StringBuffer();
    StreamingTable t = new StreamingTable(sb, 1, BorderStyle.CLASSIC,
        ShownBorders.ALL, false, "prompt");
    t.addCell("abcdef", cs);
    t.addCell("123456", cs);
    t.finishTable();
    assertEquals("prompt+----------+\n" + "prompt|abcdef    |\n"
        + "prompt+----------+\n" + "prompt|123456    |\n"
        + "prompt+----------+", sb.toString());
  }

  public void testMarginSpaces() throws IOException {
    CellStyle cs = new CellStyle(HorizontalAlign.left, AbbreviationStyle.crop,
        NullStyle.emptyString);
    StringBuffer sb = new StringBuffer();
    StreamingTable t = new StreamingTable(sb, 1, BorderStyle.CLASSIC,
        ShownBorders.ALL, false, 4);
    t.addCell("abcdef", cs);
    t.addCell("123456", cs);
    t.finishTable();
    assertEquals("    +----------+\n" + "    |abcdef    |\n"
        + "    +----------+\n" + "    |123456    |\n" + "    +----------+", sb
        .toString());
  }

  public void testAutomaticWidth() throws IOException {
    CellStyle cs = new CellStyle(HorizontalAlign.left, AbbreviationStyle.crop,
        NullStyle.emptyString);
    StringBuffer sb = new StringBuffer();
    StreamingTable t = new StreamingTable(sb, 2, BorderStyle.CLASSIC,
        ShownBorders.ALL, false, "");
    t.addCell("abcdef", cs);
    t.addCell("123456", cs);
    t.addCell("mno", cs);
    t.addCell("45689", cs);
    t.addCell("xyztuvw", cs);
    t.addCell("01234567", cs);
    t.finishTable();
    assertEquals("" //
        + "+----------+----------+\n" //
        + "|abcdef    |123456    |\n" // 
        + "+----------+----------+\n" // 
        + "|mno       |45689     |\n" // 
        + "+----------+----------+\n" // 
        + "|xyztuvw   |01234567  |\n" // 
        + "+----------+----------+", sb.toString());
  }

  public void testSetWidth() throws IOException {
    CellStyle cs = new CellStyle(HorizontalAlign.left, AbbreviationStyle.crop,
        NullStyle.emptyString);
    StringBuffer sb = new StringBuffer();
    StreamingTable t = new StreamingTable(sb, 2, BorderStyle.CLASSIC,
        ShownBorders.ALL, false, "");
    t.setColumnWidth(0, 6);
    t.setColumnWidth(1, 7);

    t.addCell("abcd", cs);
    t.addCell("123456", cs);
    t.addCell("mno", cs);
    t.addCell("45689", cs);
    t.addCell("xyztu", cs);
    t.addCell("01234567", cs);
    t.finishTable();
    assertEquals("+------+-------+\n" //
        + "|abcd  |123456 |\n" // 
        + "+------+-------+\n" // 
        + "|mno   |45689  |\n" // 
        + "+------+-------+\n" // 
        + "|xyztu |0123456|\n" // 
        + "+------+-------+", sb.toString());
  }

}
