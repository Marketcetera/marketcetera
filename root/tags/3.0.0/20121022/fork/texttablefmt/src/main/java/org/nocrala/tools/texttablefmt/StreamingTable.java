package org.nocrala.tools.texttablefmt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * <p>Text table generator with reduced memory usage. This class will generate
 * text tables like:</p>
 * 
 * <pre class='example'>
 * 
 *  +---------+-----------+----------+--------+
 *  |Country  | Population|Area (km2)| Density|
 *  +---------+-----------+----------+--------+
 *  |Chile    | 17 000 000| 1 250 000|   13.60|
 *  |Argentina| 50 000 000| 3 000 000|   16.67|
 *  |Brasil   | 80 000 000| 5 000 000|   16.00|
 *  +---------+-----------+----------+--------+
 *  |Total    |147 000 000| 9 250 000|   15.89|
 *  +---------+-----------+----------+--------+
 *  </pre>
 * 
 * <p>where the border styles, shown borders/separators, column
 * widths, alignments and other are configurable.</p>
 * 
 * <p>Cells are added using the <code>addCell()</code> method and the table
 * is finished using the <code>finishTable()</code> method.</p>
 * 
 * <p>This class uses a reduced memory footprint, since it writes rows to the
 * <code>Appendable</code> provided object each time a row is completed.
 * Therefore it's suitable for rendering large tables.</p>
 * 
 * <p>All column widths default to 10 characters wide. Change this widths as
 * desired <b>BEFORE</b> adding any cell, using the method
 * <code>setColumnWidth()</code>.</p>
 * 
 * <p>After adding all cells use the method <code>finishTable()</code> to
 * flush any remaining characters to the Appendable object.</p>
 * 
 * <p>As an example, the following code</p>
 * 
 * <pre class='example'>
 * 
 *  CellStyle cs = new CellStyle(HorizontalAlign.left, AbbreviationStyle.crop,
 *      NullStyle.emptyString);
 *  StringBuffer sb = new StringBuffer();
 *  StreamingTable t = new StreamingTable(sb, 2, BorderStyle.CLASSIC,
 *      ShownBorders.ALL, false, "");
 *  t.addCell("abcdef", cs);
 *  t.addCell("123456", cs);
 *  t.addCell("mno", cs);
 *  t.addCell("45689", cs);
 *  t.addCell("xyztuvw", cs);
 *  t.addCell("01234567", cs);
 *  t.finishTable();
 *  System.out.println(sb.toString());
 *  </pre>
 * 
 * <p>will generate the table:</p>
 * 
 * <pre class='example'>
 *  
 *  +-------+--------+
 *  |abcdef |123456  | 
 *  +-------+--------+ 
 *  |mno    |45689   | 
 *  +-------+--------+ 
 *  |xyztuvw|01234567| 
 *  +-------+--------+
 *  </pre>
 * 
 * <p>The generated table can be customized using a <code>BorderStyle</code>, 
 * <code>ShownBorders</code> and cell widths. Besides, cell rendering can be 
 * customized on a cell basis using <code>CellStyle</code>s.</p>  
 * 
 *
 */
public class StreamingTable {

  private static Logger logger = Logger.getLogger(StreamingTable.class);

  private static final int DEFAULT_WIDTH = 10;

  private Appendable appendable;

  TableStyle tableStyle;

  private List<Column> columns;

  private int currentColumn;

  private Row previousRow;

  private Row currentRow;

  private int currentRowPos;

  /**
   * Creates a streaming table that will write to an <code>Appendable</code> 
   * object using <code>BorderStyle.CLASSIC</code> and 
   * <code>ShownBorders.SURROUND_HEADER_AND_COLUMNS</code>, no XML 
   * escaping and no left margin.
   * 
   * @param appendable Character stream where to write the rendered table.
   * @param totalColumns Total columns of this table.
   */
  public StreamingTable(final Appendable appendable, final int totalColumns) {
    initialize(appendable, totalColumns);
    this.tableStyle = new TableStyle(BorderStyle.CLASSIC,
        ShownBorders.SURROUND_HEADER_AND_COLUMNS, false, 0, null);
  }

  /**
   * Creates a streaming table that will write to an <code>Appendable</code> 
   * object using a specific border style, showing 
   * <code>ShownBorders.SURROUND_HEADER_AND_COLUMNS</code> separators, no XML 
   * escaping and no left margin.
   * 
   * @param appendable Character stream where to write the rendered table.
   * @param totalColumns Total columns of this table.
   * @param borderStyle The border style to use when rendering the table.
   */
  public StreamingTable(final Appendable appendable, final int totalColumns,
      final BorderStyle borderStyle) {
    initialize(appendable, totalColumns);
    this.tableStyle = new TableStyle(borderStyle,
        ShownBorders.SURROUND_HEADER_AND_COLUMNS, false, 0, null);
  }

  /**
   * Creates a streaming table that will write to an <code>Appendable</code> 
   * object using specific border style and shown borders, no XML 
   * escaping and no left margin.
   * 
   * @param appendable Character stream where to write the rendered table.
   * @param totalColumns Total columns of this table.
   * @param borderStyle The border style to use when rendering the table.
   * @param shownBorders Specifies which borders will be rendered.
   */
  public StreamingTable(final Appendable appendable, final int totalColumns,
      final BorderStyle borderStyle, final ShownBorders shownBorders) {
    initialize(appendable, totalColumns);
    this.tableStyle = new TableStyle(borderStyle, shownBorders, false, 0, null);
  }

  /**
   * Creates a streaming table that will write to an <code>Appendable</code> 
   * object using specific border style, shown borders and XML 
   * escaping and without left margin.
   * 
   * @param appendable Character stream where to write the rendered table.
   * @param totalColumns Total columns of this table.
   * @param borderStyle The border style to use when rendering the table.
   * @param shownBorders Specifies which borders will be rendered.
   * @param escapeXml Specifies if the rendered text should be escaped using 
   * XML entities.
   */
  public StreamingTable(final Appendable appendable, final int totalColumns,
      final BorderStyle borderStyle, final ShownBorders shownBorders,
      final boolean escapeXml) {
    initialize(appendable, totalColumns);
    this.tableStyle = new TableStyle(borderStyle, shownBorders, escapeXml, 0,
        null);
  }

  /**
   * Creates a streaming table that will write to an <code>Appendable</code> 
   * object using specific border style, shown borders, XML 
   * escaping and left margin.
   * 
   * @param appendable Character stream where to write the rendered table.
   * @param totalColumns Total columns of this table.
   * @param borderStyle The border style to use when rendering the table.
   * @param shownBorders Specifies which borders will be rendered.
   * @param escapeXml Specifies if the rendered text should be escaped using 
   * XML entities.
   * @param leftMargin Specifies how many blank spaces will be used as a left margin for the table.
   */
  public StreamingTable(final Appendable appendable, final int totalColumns,
      final BorderStyle borderStyle, final ShownBorders shownBorders,
      final boolean escapeXml, final int leftMargin) {
    initialize(appendable, totalColumns);
    this.tableStyle = new TableStyle(borderStyle, shownBorders, escapeXml,
        leftMargin, null);
  }

  /**
   * Creates a streaming table that will write to an <code>Appendable</code> 
   * object using specific border style, shown borders, XML 
   * escaping and left margin.
   * 
   * @param appendable Character stream where to write the rendered table.
   * @param totalColumns Total columns of this table.
   * @param borderStyle The border style to use when rendering the table.
   * @param shownBorders Specifies which borders will be rendered.
   * @param escapeXml Specifies if the rendered text should be escaped using 
   * XML entities.
   * @param prompt Text to use as left margin for the table.
   */
  public StreamingTable(final Appendable appendable, final int totalColumns,
      final BorderStyle borderStyle, final ShownBorders shownBorders,
      final boolean escapeXml, final String prompt) {
    initialize(appendable, totalColumns);
    this.tableStyle = new TableStyle(borderStyle, shownBorders, escapeXml, 0,
        prompt);
  }

  private void initialize(final Appendable writer, final int totalColumns) {
    this.appendable = writer;
    this.previousRow = null;
    this.columns = new ArrayList<Column>();
    for (int i = 0; i < totalColumns; i++) {
      this.columns.add(new Column(i, DEFAULT_WIDTH));
    }
    this.currentColumn = 0;
    this.currentRow = null;
    this.currentRowPos = 0;
  }

  /**
   * Sets the width of a specific column.
   * 
   * @param col Column whose width will be set. First column is 0 (zero).
   * @param width width of the column.
   */
  public void setColumnWidth(final int col, final int width) {
    this.columns.get(col).setWidth(width < 0 ? 0 : width);
  }

  /**
   * Adds a cell with the default CellStyle.
   * 
   * @param content Cell text.
   * @throws IOException if it is not possible to output to the Appendable
   * object.
   */
  public void addCell(final String content) throws IOException {
    addCell(content, new CellStyle());
  }

  /**
   * Adds a cell with a colspan and the default CellStyle.
   * 
   * @param content Cell text.
   * @param colSpan Columns this cell will span through.
   * @throws IOException if it is not possible to output to the Appendable
   * object.
   */
  public void addCell(final String content, final int colSpan)
      throws IOException {
    addCell(content, new CellStyle(), colSpan);
  }

  /**
   * Adds a cell with a specific cell style.
   * 
   * @param content Cell text.
   * @param style Cell style to use when rendering the cell content.
   * @throws IOException if it is not possible to output to the Appendable
   * object.
   */
  public void addCell(final String content, final CellStyle style)
      throws IOException {
    addCell(content, style, 1);
  }

  /**
   * Adds a cell with a specific cell style and colspan.
   * 
   * @param content Cell text.
   * @param style Cell style to use when rendering the cell content.
   * @param colSpan Columns this cell will span through.
   * @throws IOException if it is not possible to output to the Appendable
   * object.
   */
  public void addCell(final String content, final CellStyle style,
      final int colSpan) throws IOException {
    if (this.currentRow == null) {
      this.currentRow = new Row();
      this.currentColumn = 0;
    }
    if (this.currentColumn >= this.columns.size()) {
      renderRow(false);
      this.previousRow = this.currentRow;
      this.currentRow = new Row();
      this.currentColumn = 0;
    }
    int adjColSpan = colSpan > 0 ? colSpan : 1;
    this.currentRow.addCell(content, style, adjColSpan);
    this.currentColumn = this.currentColumn + adjColSpan;
  }

  /**
   * Finishes the table rendering and flushes any remaining characters to the
   * Appendable object.
   * @throws IOException
   */
  public void finishTable() throws IOException {
    if (this.currentRow != null) {
      renderRow(true);
    }
  }

  private void renderRow(final boolean isLast) throws IOException {
    logger.debug("this.currentRow.getSize()=" + this.currentRow.getSize());
    boolean isFirst = this.currentRowPos == 0;
    boolean isSecond = this.currentRowPos == 1;
    boolean isIntermediate = (this.currentRowPos > 1) && !isLast;
    this.tableStyle.renderRow(this.appendable, this.currentRow,
        this.previousRow, this.columns, isFirst, isSecond, isIntermediate,
        isLast);
    this.currentRowPos++;
  }

  int getTotalColumns() {
    return this.columns.size();
  }

  Column getColumn(final int index) {
    return this.columns.get(index);
  }

}
