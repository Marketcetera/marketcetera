package org.nocrala.tools.texttablefmt;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.nocrala.tools.texttablefmt.CellStyle.AbbreviationStyle;
import org.nocrala.tools.texttablefmt.CellStyle.HorizontalAlign;
import org.nocrala.tools.texttablefmt.CellStyle.NullStyle;

public class TableColSpanTests extends TestCase {

  private static Logger logger = Logger.getLogger(TableColSpanTests.class);

  public TableColSpanTests(final String txt) {
    super(txt);
  }

  public void testWideNullCell() {
    logger.debug("WideNullCell");
    CellStyle cs = new CellStyle(HorizontalAlign.left, AbbreviationStyle.crop,
        NullStyle.emptyString);
    Table t = new Table(5, BorderStyle.CLASSIC, ShownBorders.ALL, false, "");
    t.addCell(null, cs, 5);
    //        logger.debug("t=" + t.render());
    assertEquals("" // 
        + "+----+\n" // 
        + "|    |\n" // 
        + "+----+", t.render());
  }

  public void testWideIncompleteNullCell() {
    CellStyle cs = new CellStyle(HorizontalAlign.left, AbbreviationStyle.crop,
        NullStyle.emptyString);
    Table t = new Table(7, BorderStyle.CLASSIC, ShownBorders.ALL, false, "");
    t.addCell(null, cs, 3);
    logger.debug("t=\n" + t.render());
    assertEquals("" // 
        + "+--+++++\n" // 
        + "|  |||||\n" // 
        + "+--+++++", t.render());
  }

  public void testSetColSpan() {
    CellStyle cs = new CellStyle(HorizontalAlign.left, AbbreviationStyle.crop,
        NullStyle.emptyString);
    CellStyle csr = new CellStyle(HorizontalAlign.right,
        AbbreviationStyle.crop, NullStyle.emptyString);
    CellStyle csc = new CellStyle(HorizontalAlign.center,
        AbbreviationStyle.crop, NullStyle.emptyString);
    Table t = new Table(2, BorderStyle.CLASSIC, ShownBorders.ALL, false, "");
    t.setColumnWidth(0, 6, 10);
    t.setColumnWidth(1, 2, 7);

    t.addCell("abcd", cs);
    t.addCell("123456", cs);
    t.addCell("mno", cs, 2);
    t.addCell("xyztu", csr, 2);
    t.addCell("efgh", csc, 2);
    //        logger.debug("t=\n" + t.render());
    assertEquals("" //
        + "+------+------+\n" //
        + "|abcd  |123456|\n" // 
        + "+-------------+\n" // 
        + "|mno          |\n" // 
        + "+-------------+\n" // 
        + "|        xyztu|\n" // 
        + "+-------------+\n" // 
        + "|    efgh     |\n" // 
        + "+-------------+", t.render());
  }

  public void testCenteredColSpan() {
    CellStyle cs = new CellStyle(HorizontalAlign.left, AbbreviationStyle.crop,
        NullStyle.emptyString);
    CellStyle csr = new CellStyle(HorizontalAlign.right,
        AbbreviationStyle.crop, NullStyle.emptyString);
    Table t = new Table(4, BorderStyle.CLASSIC, ShownBorders.ALL, false, "");
    t.setColumnWidth(0, 3, 10);
    t.setColumnWidth(1, 2, 7);
    t.setColumnWidth(2, 3, 10);

    t.addCell("abcd", cs);
    t.addCell("123456", cs);
    t.addCell("efgh", cs);
    t.addCell("789012", cs);

    t.addCell("ijkl", cs);
    t.addCell("mno", cs, 2);
    t.addCell("345678", cs);

    t.addCell("mnop", cs);
    t.addCell("901234", cs);
    t.addCell("qrst", cs);
    t.addCell("567890", cs);
    //    logger.debug("t=\n" + t.render());
    assertEquals("" //
        + "+----+------+----+------+\n" //
        + "|abcd|123456|efgh|789012|\n" // 
        + "+----+-----------+------+\n" // 
        + "|ijkl|mno        |345678|\n" // 
        + "+----+-----------+------+\n" // 
        + "|mnop|901234|qrst|567890|\n" // 
        + "+----+------+----+------+", t.render());
  }

  public void testSetColSpanWide() {
    CellStyle cs = new CellStyle(HorizontalAlign.left, AbbreviationStyle.crop,
        NullStyle.emptyString);
    CellStyle csr = new CellStyle(HorizontalAlign.right,
        AbbreviationStyle.crop, NullStyle.emptyString);
    Table t = new Table(2, BorderStyle.CLASSIC_WIDE, ShownBorders.ALL, false,
        "");
    t.setColumnWidth(0, 6, 10);
    t.setColumnWidth(1, 2, 7);

    t.addCell("abcd", cs);
    t.addCell("123456", cs);
    t.addCell("mno", cs, 2);
    t.addCell("xyztu", csr, 2);
    //    logger.debug("t=\n" + t.render());
    assertEquals(""//
        + "+--------+--------+\n" //
        + "| abcd   | 123456 |\n" // 
        + "+-----------------+\n" // 
        + "| mno             |\n" // 
        + "+-----------------+\n" // 
        + "|           xyztu |\n" // 
        + "+-----------------+", t.render());
  }

  public void testTooWideCell() {
    CellStyle cs = new CellStyle(HorizontalAlign.left, AbbreviationStyle.crop,
        NullStyle.emptyString);
    Table t = new Table(3, BorderStyle.CLASSIC, ShownBorders.ALL, false, "");
    t.addCell("abc", cs, 5);
    t.addCell("defg", cs, 5);
    //        logger.debug("t=\n" + t.render());
    assertEquals("" // 
        + "+----+\n" // 
        + "|abc |\n" // 
        + "+----+\n" // 
        + "|defg|\n" // 
        + "+----+", t.render());
  }

}
