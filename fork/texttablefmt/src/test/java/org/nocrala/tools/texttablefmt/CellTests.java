package org.nocrala.tools.texttablefmt;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.nocrala.tools.texttablefmt.CellStyle.AbbreviationStyle;
import org.nocrala.tools.texttablefmt.CellStyle.HorizontalAlign;
import org.nocrala.tools.texttablefmt.CellStyle.NullStyle;

public class CellTests extends TestCase {

  private static Logger logger = Logger.getLogger(CellTests.class);

  public CellTests(final String txt) {
    super(txt);
  }

  public void testAlignLeft() {
    CellStyle cs = new CellStyle(HorizontalAlign.left, AbbreviationStyle.crop,
        NullStyle.emptyString);
    Cell c = new Cell("abcdef", cs, 1);
    assertEquals("", c.render(0));
    assertEquals("a", c.render(1));
    assertEquals("ab", c.render(2));
    assertEquals("abc", c.render(3));
    assertEquals("abcd", c.render(4));
    assertEquals("abcde", c.render(5));
    assertEquals("abcdef", c.render(6));
    assertEquals("abcdef ", c.render(7));
    assertEquals("abcdef  ", c.render(8));
    assertEquals("abcdef   ", c.render(9));
    assertEquals("abcdef    ", c.render(10));
    assertEquals("abcdef     ", c.render(11));
  }

  public void testAlignCenter() {
    CellStyle cs = new CellStyle(HorizontalAlign.center,
        AbbreviationStyle.crop, NullStyle.emptyString);
    Cell c = new Cell("abcdef", cs, 1);
    assertEquals("", c.render(0));
    assertEquals("a", c.render(1));
    assertEquals("ab", c.render(2));
    assertEquals("abc", c.render(3));
    assertEquals("abcd", c.render(4));
    assertEquals("abcde", c.render(5));
    assertEquals("abcdef", c.render(6));
    assertEquals("abcdef ", c.render(7));
    assertEquals(" abcdef ", c.render(8));
    assertEquals(" abcdef  ", c.render(9));
    assertEquals("  abcdef  ", c.render(10));
    assertEquals("  abcdef   ", c.render(11));
  }

  public void testAlignRight() {
    CellStyle cs = new CellStyle(HorizontalAlign.right, AbbreviationStyle.crop,
        NullStyle.emptyString);
    Cell c = new Cell("abcdef", cs, 1);
    assertEquals("", c.render(0));
    assertEquals("a", c.render(1));
    assertEquals("ab", c.render(2));
    assertEquals("abc", c.render(3));
    assertEquals("abcd", c.render(4));
    assertEquals("abcde", c.render(5));
    assertEquals("abcdef", c.render(6));
    assertEquals(" abcdef", c.render(7));
    assertEquals("  abcdef", c.render(8));
    assertEquals("   abcdef", c.render(9));
    assertEquals("    abcdef", c.render(10));
    assertEquals("     abcdef", c.render(11));
  }

  public void testAbbreviateCrop() {
    CellStyle cs = new CellStyle(HorizontalAlign.left, AbbreviationStyle.crop,
        NullStyle.emptyString);
    Cell c = new Cell("abcdef", cs, 1);
    assertEquals("", c.render(0));
    assertEquals("a", c.render(1));
    assertEquals("ab", c.render(2));
    assertEquals("abc", c.render(3));
    assertEquals("abcd", c.render(4));
    assertEquals("abcde", c.render(5));
    assertEquals("abcdef", c.render(6));
    assertEquals("abcdef ", c.render(7));
  }

  public void testAbbreviateDots() {
    CellStyle cs = new CellStyle(HorizontalAlign.left, AbbreviationStyle.dots,
        NullStyle.emptyString);
    Cell c = new Cell("abcdef", cs, 1);
    assertEquals("", c.render(0));
    assertEquals(".", c.render(1));
    assertEquals("..", c.render(2));
    assertEquals("...", c.render(3));
    assertEquals("a...", c.render(4));
    assertEquals("ab...", c.render(5));
    assertEquals("abcdef", c.render(6));
    assertEquals("abcdef ", c.render(7));
  }

  public void testNullEmpty() {
    logger.debug("Null");
    CellStyle cs = new CellStyle(HorizontalAlign.left, AbbreviationStyle.crop,
        NullStyle.emptyString);
    Cell c = new Cell(null, cs, 1);
    assertEquals("", c.render(0));
    assertEquals(" ", c.render(1));
    assertEquals("  ", c.render(2));
    assertEquals("   ", c.render(3));
    assertEquals("    ", c.render(4));
    assertEquals("          ", c.render(10));
    assertEquals("           ", c.render(11));
  }

  public void testNullText() {
    CellStyle cs = new CellStyle(HorizontalAlign.left, AbbreviationStyle.crop,
        NullStyle.nullText);
    Cell c = new Cell(null, cs, 1);
    assertEquals("", c.render(0));
    assertEquals("<", c.render(1));
    assertEquals("<n", c.render(2));
    assertEquals("<nu", c.render(3));
    assertEquals("<nul", c.render(4));
    assertEquals("<null", c.render(5));
    assertEquals("<null>", c.render(6));
    assertEquals("<null> ", c.render(7));
    assertEquals("<null>     ", c.render(11));
  }

}
