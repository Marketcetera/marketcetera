package org.nocrala.tools.texttablefmt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.nocrala.tools.utils.TextEncoder;
import org.nocrala.tools.utils.Filler;

class TableStyle {

  private static Logger logger = Logger.getLogger(TableStyle.class);

  private ShownBorders shownBorders;

  BorderStyle borderStyle;

  private boolean escapeXml;

  private int leftMargin;

  private String prompt;

  public TableStyle(final BorderStyle borderStyle,
      final ShownBorders shownBorders, final boolean escapeXml,
      final int leftMargin, final String prompt) {
    this.borderStyle = borderStyle;
    this.shownBorders = shownBorders;
    this.escapeXml = escapeXml;
    this.leftMargin = leftMargin;
    this.prompt = prompt;
    if (this.prompt == null) {
      if (this.leftMargin > 0) {
        this.prompt = Filler.getFiller(this.leftMargin);
      } else {
        this.prompt = "";
      }
    }
  }

  private String escapeXmlIfRequired(final String txt) {
    if (this.escapeXml) {
      StringBuffer sb = new StringBuffer();
      sb.append(TextEncoder.escapeXml(txt));
      return sb.toString();
    }
    return txt;
  }

  private String escapeXmlIfRequired(final String txt1, final String txt2) {
    if (this.escapeXml) {
      StringBuffer sb = new StringBuffer();
      sb.append(TextEncoder.escapeXml(txt1));
      sb.append(TextEncoder.escapeXml(txt2));
      return sb.toString();
    }
    return txt1 + txt2;
  }

  String renderTable(final Table table) {
    StringBuffer sb = new StringBuffer();
    int totalRows = table.getRows().size();
    Row previousRow = null;
    boolean firstRenderedRow = true;
    for (int i = 0; i < totalRows; i++) {
      Row r = table.getRows().get(i);
      boolean isFirst = i == 0;
      boolean isSecond = i == 1;
      boolean isIntermediate = (i > 1 && i < totalRows - 1);
      boolean isLast = i == (totalRows - 1);
      List<String> rr = renderRow(r, previousRow, table.getColumns(), isFirst,
          isSecond, isIntermediate, isLast);
      for (String line : rr) {
        if (firstRenderedRow) {
          firstRenderedRow = false;
        } else {
          sb.append("\n");
        }
        sb.append(line);
      }
      previousRow = r;
    }
    return sb.toString();
  }

  String[] renderAsStringArray(final Table table) {
    int totalRows = table.getRows().size();
    Row previousRow = null;
    List<String> allLines = new ArrayList<String>();
    for (int i = 0; i < totalRows; i++) {
      Row r = table.getRows().get(i);
      boolean isFirst = i == 0;
      boolean isSecond = i == 1;
      boolean isIntermediate = (i > 1 && i < totalRows - 1);
      boolean isLast = i == (totalRows - 1);
      List<String> rr = renderRow(r, previousRow, table.getColumns(), isFirst,
          isSecond, isIntermediate, isLast);
      for (String line : rr) {
        allLines.add(line);
      }
      previousRow = r;
    }

    String[] result = new String[allLines.size()];
    int i = 0;
    for (String line : allLines) {
      result[i] = line;
      i++;
    }
    return result;
  }

  void renderRow(final Appendable ap, final Row r, final Row previousRow,
      final List<Column> columns, final boolean isFirst,
      final boolean isSecond, final boolean isIntermediate, final boolean isLast)
      throws IOException {
    List<String> rr = renderRow(r, previousRow, columns, isFirst, isSecond,
        isIntermediate, isLast);
    boolean firstRenderedRow = isFirst;
    for (String line : rr) {
      if (firstRenderedRow) {
        firstRenderedRow = false;
      } else {
        ap.append("\n");
      }
      ap.append(line);
    }
  }

  private List<String> renderRow(final Row r, final Row previousRow,
      final List<Column> columns, final boolean isFirst,
      final boolean isSecond, final boolean isIntermediate, final boolean isLast) {
    List<String> list = new ArrayList<String>();
    if (isFirst) {
      if (this.shownBorders.showTopBorder()) {
        list.add(escapeXmlIfRequired(this.prompt, this.shownBorders
            .renderTopBorder(columns, this.borderStyle, r)));
      }
    } else {
      if (isIntermediate && this.shownBorders.showMiddleSeparator() || //
          isSecond && this.shownBorders.showHeaderSeparator() // 
          || isLast && this.shownBorders.showFooterSeparator()) {
        list.add(escapeXmlIfRequired(this.prompt, this.shownBorders
            .renderMiddleSeparator(columns, this.borderStyle, previousRow, r)));
      }
    }

    logger.debug("+++++++ r.getSize()=" + r.getSize());
    list.add(escapeXmlIfRequired(this.prompt, renderContentRow(r, columns)));

    if (isLast) {
      if (this.shownBorders.showBottomBorder()) {
        list.add(escapeXmlIfRequired(this.prompt, this.shownBorders
            .renderBottomBorder(columns, this.borderStyle, r)));
      }
    }

    return list;
  }

  private String renderContentRow(final Row r, final List<Column> columns) {
    StringBuffer sb = new StringBuffer();

    // Left border

    if (this.shownBorders.showLeftBorder()) {
      sb.append(this.borderStyle.getLeft());
    }

    // Cells

    int totalColumns = columns.size();
    int j = 0;
    for (Cell cell : r.getCells()) {

      // cell separator

      logger.debug("j=" + j);
      logger.debug("this.shownBorders.showCenterSeparator()="
          + this.shownBorders.showCenterSeparator());
      logger.debug("this.shownBorders.showLeftSeparator()="
          + this.shownBorders.showLeftSeparator());
      logger.debug("this.shownBorders.showRightSeparator()="
          + this.shownBorders.showRightSeparator());
      logger.debug("this.borderTiles.getCenter()="
          + this.borderStyle.getCenter());
      if (j != 0) {
        if ((j > 1 && j < totalColumns - 1)
            && this.shownBorders.showCenterSeparator()
            || ((j == 1) && (this.shownBorders.showLeftSeparator()))
            || ((j == (totalColumns - 1)) && (this.shownBorders
                .showRightSeparator()))) {
          logger.debug("--- appending '" + this.borderStyle.getCenter()
              + "' ---");
          logger.debug("this.borderTiles.getLeftWidth()="
              + this.borderStyle.getLeftWidth());
          logger.debug("this.borderTiles.getCenterWidth()="
              + this.borderStyle.getCenterWidth());
          logger.debug("this.borderTiles.getRightWidth()="
              + this.borderStyle.getRightWidth());
          logger.debug("this.borderTiles.getHorizontalWidth()="
              + this.borderStyle.getHorizontalWidth());
          sb.append(this.borderStyle.getCenter());
        }
      }

      // Cell content

      int sepWidth = this.borderStyle.getCenter().length();
      int width = -sepWidth;
      logger.debug("* width=" + width);
      for (int pos = j; pos < j + cell.getColSpan(); pos++) {
        width = width + sepWidth + columns.get(pos).getColumnWidth();
        logger.debug("** columns.get(" + j + ").getColumnWidth()="
            + columns.get(pos).getColumnWidth() + "  width=" + width);
      }
      logger.debug("*** width=" + width);
      String renderedCell = cell.render(width);
      logger.debug("content='" + cell.getContent() + "' --> renderedCell="
          + renderedCell);
      sb.append(renderedCell);

      j = j + cell.getColSpan();
    }

    // Render missing cells

    for (; j < totalColumns; j++) {

      // cell separator

      if (j != 0) {
        if ((j > 1 && j < totalColumns - 1)
            && this.shownBorders.showCenterSeparator()
            || ((j == 1) && (this.shownBorders.showLeftSeparator()))
            || ((j == (totalColumns - 1)) && (this.shownBorders
                .showRightSeparator()))) {
          sb.append(this.borderStyle.getCenter());
        }
      }

      // Cell content

      Column col = columns.get(j);
      String renderedCell = CellStyle.renderNullCell(col.getColumnWidth());
      sb.append(renderedCell);
    }

    // Right border

    if (this.shownBorders.showRightBorder()) {
      sb.append(this.borderStyle.getRight());
    }

    return sb.toString();
  }

}
