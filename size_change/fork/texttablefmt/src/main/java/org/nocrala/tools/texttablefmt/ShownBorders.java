package org.nocrala.tools.texttablefmt;

import java.util.List;

import org.apache.log4j.Logger;
import org.nocrala.tools.utils.Filler;

/**
 * <p>A ShownBorders define which borders and internal separators are
 * rendered.</p>
 * 
 * <p>A ShownBorders does not control the specific characters (tiles) that
 * will be used to render those borders; a BorderStyle is used to define those
 * ones.</p>
 * 
 * <p>You can use predefined styles or custom styles.</p>
 * 
 * <h3>Predefined Styles</h3>
 * 
 * <p>Below all predefined ShownBorders are listed with examples. For simplicity 
 * and clarity, all these examples use the style BorderStyle.CLASSIC, but they work 
 * with any BorderStyle:</p>
 * 
 * 
 * <p><b>ShownBorders.ALL</b></p>
 * 
 * <pre class='example'>
 *  +---------+----------+----------+-------+
 *  |Country  |Population|Area (km2)|Density|
 *  +---------+----------+----------+-------+
 *  |Chile    |17 000 000| 1 250 000|  13.60|
 *  +---------+----------+----------+-------+
 *  |Argentina|50 000 000| 3 000 000|  16.67|
 *  +---------+----------+----------+-------+
 *  |Brasil   |80 000 000| 5 000 000|  16.00|
 *  +---------+----------+----------+-------+
 * </pre>
 * 
 * 
 * <p><b>ShownBorders.NONE</b></p>
 * 
 * <pre class='example'>
 *  Country  PopulationArea (km2)Density
 *  Chile    17 000 000 1 250 000  13.60
 *  Argentina50 000 000 3 000 000  16.67
 *  Brasil   80 000 000 5 000 000  16.00
 * </pre>
 * 
 * 
 * <p><b><u>Default:</u> ShownBorders.SURROUND_HEADER_AND_COLUMNS</b></p>
 * 
 * <pre class='example'>
 *  +---------+----------+----------+-------+
 *  |Country  |Population|Area (km2)|Density|
 *  +---------+----------+----------+-------+
 *  |Chile    |17 000 000| 1 250 000|  13.60|
 *  |Argentina|50 000 000| 3 000 000|  16.67|
 *  |Brasil   |80 000 000| 5 000 000|  16.00|
 *  +---------+----------+----------+-------+
 * </pre>
 * 
 * <p><b>ShownBorders.SURROUND_HEADER_FOOTER_AND_COLUMNS</b></p>
 * 
 * <pre class='example'>
 *  +---------+-----------+----------+-------+
 *  |Country  | Population|Area (km2)|Density|
 *  +---------+-----------+----------+-------+
 *  |Chile    | 17 000 000| 1 250 000|  13.60|
 *  |Argentina| 50 000 000| 3 000 000|  16.67|
 *  |Brasil   | 80 000 000| 5 000 000|  16.00|
 *  +---------+-----------+----------+-------+
 *  |Total    |147 000 000| 9 250 000|  15.89|
 *  +---------+-----------+----------+-------+
 * </pre>
 * 
 * <p><b>ShownBorders.SURROUND</b></p>
 * 
 * <pre class='example'>
 *  +------------------------------------+
 *  |Country  PopulationArea (km2)Density|
 *  |Chile    17 000 000 1 250 000  13.60|
 *  |Argentina50 000 000 3 000 000  16.67|
 *  |Brasil   80 000 000 5 000 000  16.00|
 *  +------------------------------------+
 * </pre>
 * 
 * 
 * <p><b>ShownBorders.HEADER_AND_COLUMNS</b></p>
 * 
 * <pre class='example'>
 *  Country  |Population|Area (km2)|Density
 *  ---------+----------+----------+-------
 *  Chile    |17 000 000| 1 250 000|  13.60
 *  Argentina|50 000 000| 3 000 000|  16.67
 *  Brasil   |80 000 000| 5 000 000|  16.00
 * </pre>
 * 
 * 
 * <p><b>ShownBorders.HEADER_FOOTER_AND_COLUMNS</b></p>
 * 
 * <pre class='example'>
 *  Country  |Population|Area (km2)|Density
 *  ---------+----------+----------+-------
 *  Chile    |17 000 000| 1 250 000|  13.60
 *  Argentina|50 000 000| 3 000 000|  16.67
 *  ---------+----------+----------+-------
 *  Brasil   |80 000 000| 5 000 000|  16.00
 * </pre>
 * 
 * 
 * <p><b>ShownBorders.HEADER_AND_FOOTER</b></p>
 * 
 * <pre class='example'>
 *  Country  PopulationArea (km2)Density
 *  ------------------------------------
 *  Chile    17 000 000 1 250 000  13.60
 *  Argentina50 000 000 3 000 000  16.67
 *  ------------------------------------
 *  Brasil   80 000 000 5 000 000  16.00
 * </pre>
 * 
 * 
 * <p><b>ShownBorders.HEADER_AND_FIRST_COLLUMN</b></p>
 * 
 * <pre class='example'>
 *  Country  |PopulationArea (km2)Density
 *  ---------+---------------------------
 *  Chile    |17 000 000 1 250 000  13.60
 *  Argentina|50 000 000 3 000 000  16.67
 *  Brasil   |80 000 000 5 000 000  16.00
 * </pre>
 * 
 * 
 * <p><b>ShownBorders.HEADER_FIRST_AND_LAST_COLLUMN</b></p>
 * 
 * <pre class='example'>
 *  Country  |PopulationArea (km2)|Density
 *  ---------+--------------------+-------
 *  Chile    |17 000 000 1 250 000|  13.60
 *  Argentina|50 000 000 3 000 000|  16.67
 *  Brasil   |80 000 000 5 000 000|  16.00
 * </pre>
 * 
 * 
 * <p><b>ShownBorders.HEADER_FOOTER_AND_FIRST_COLLUMN</b></p>
 * 
 * <pre class='example'>
 *  Country  |PopulationArea (km2)Density
 *  ---------+---------------------------
 *  Chile    |17 000 000 1 250 000  13.60
 *  Argentina|50 000 000 3 000 000  16.67
 *  ---------+---------------------------
 *  Brasil   |80 000 000 5 000 000  16.00
 * </pre>
 * 
 * 
 * <p><b>ShownBorders.HEADER_FOOTER_FIRST_AND_LAST_COLLUMN</b></p>
 * 
 * <pre class='example'>
 *  Country  |PopulationArea (km2)|Density
 *  ---------+--------------------+-------
 *  Chile    |17 000 000 1 250 000|  13.60
 *  Argentina|50 000 000 3 000 000|  16.67
 *  ---------+--------------------+-------
 *  Brasil   |80 000 000 5 000 000|  16.00
 * </pre>
 * 
 * <br>
 * <p>In combination with a BorderStyle you can render cleaner tables, like in the following examples:</p>
 * 
 * <p><b>ShownBorders.HEADER_FOOTER_AND_COLUMNS, BorderStyle.HORIZONTAL_ONLY</b></p>
 * 
 * <pre class='example'>
 *  Country   Population Area (km2) Density
 *  --------- ---------- ---------- -------
 *  Chile     17 000 000  1 250 000   13.60
 *  Argentina 50 000 000  3 000 000   16.67
 *  --------- ---------- ---------- -------
 *  Brasil    80 000 000  5 000 000   16.00
 * </pre>
 * 
 * 
 * <p><b>ShownBorders.HEADER_ONLY, BorderStyle.HORIZONTAL_ONLY</b></p>
 * 
 * <pre class='example'>
 *  Country   Population Area (km2) Density
 *  --------- ---------- ---------- -------
 *  Chile     17 000 000  1 250 000   13.60
 *  Argentina 50 000 000  3 000 000   16.67
 *  Brasil    80 000 000  5 000 000   16.00
 * </pre>
 * 
 *
 * <p>If no predefined style fits your needs, you can use a...</p>
 * 
 * <h3>Custom styles</h3>
 * 
 * <p> You can separately control the rendering of the following borders and
 * separators, using a custom style: </p>
 * 
 * <pre class='example'>
 *       +- top border --------+----------+----------+----------+
 *       |                     |          |          |          | 
 *     left                    |       center        |        right 
 *    border                   |     separators      |        border
 *       |                     |          |          |          |
 *       +- header separator --+----------+----------+----------+
 *       |                     |          |          |          |
 *       |                     |          |          |          |
 *       +- middle separators -+----------+----------+----------+
 *       |                     |          |          |          |
 *       |                     |          |          |          |
 *       +- footer separator --+----------+----------+----------+
 *       |                     |          |          |          |
 *       |                   left         |        right        | 
 *       |                 separator      |      separator      |
 *       |                     |          |          |          |
 *       +- bottom border -----+----------+----------+----------+       
 * </pre>
 * 
 * <p>For vertical separators, there is a single left border, left separator, right separator and right
 * border. All separator inside them are considered center separators.
 * Similarly, for horizontal separator there can be many middle separators.</p>
 * 
 * <p>For example, you can use:</p>
 * 
 * <pre class='example'>    new ShownBorders("t.t.t.t.t.")</pre>
 * 
 * <p>to generate a table like:</p>
 * 
 * <pre class='example'>
 *    +-------------------+-----------------
 *    |Country  Population|Area (km2)Density
 *    +-------------------+-----------------
 *    |Chile    17 000 000| 1 250 000  13.60
 *    |Argentina50 000 000| 3 000 000  16.67
 *    +-------------------+-----------------
 *    |Brasil   80 000 000| 5 000 000  16.00
 * </pre>
 * 
 * <p>Use any combination of <code>'t'</code> and <code>'.'</code> that fits your needs.</p>
 * 
 */

public class ShownBorders {

  private static Logger logger = Logger.getLogger(ShownBorders.class);

  /** Renders no border nor separator. */
  public static final ShownBorders NONE = new ShownBorders("..........");

  /** Render only the header separator. */
  public static final ShownBorders HEADER_ONLY = new ShownBorders("t...t.....");

  /** Render the header and footer separators. */
  public static final ShownBorders HEADER_AND_FOOTER = new ShownBorders(
      "t.t.......");

  /**
   * Render header and footer separators, and the left, center and right
   * separators.
   */
  public static final ShownBorders HEADER_FOOTER_AND_COLUMNS = new ShownBorders(
      "t.tttt....");

  /** Render the header separator and the left separator. */
  public static final ShownBorders HEADER_AND_FIRST_COLLUMN = new ShownBorders(
      "t..t......");

  /** Render the header separator, and the left and right separator. */
  public static final ShownBorders HEADER_FIRST_AND_LAST_COLLUMN = new ShownBorders(
      "t..t.t....");

  /** Render the header and footer separator, and the left separator. */
  public static final ShownBorders HEADER_FOOTER_AND_FIRST_COLLUMN = new ShownBorders(
      "t.tt......");

  /** Render the header and footer separator, and the left and right separators. */
  public static final ShownBorders HEADER_FOOTER_FIRST_AND_LAST_COLLUMN = new ShownBorders(
      "t.tt.t....");

  /** Render the header separator, and the left, center and right separators. */
  public static final ShownBorders HEADER_AND_COLUMNS = new ShownBorders(
      "t..ttt....");

  /**
   * Render all the borders, the header separator, and the left, center and
   * right separators.
   */
  public static final ShownBorders SURROUND_HEADER_AND_COLUMNS = new ShownBorders(
      "t..ttttttt");

  /**
   * Render all the borders, the header and footer separators, and the left,
   * center and right separators.
   */
  public static final ShownBorders SURROUND_HEADER_FOOTER_AND_COLUMNS = new ShownBorders(
      "t.tttttttt");

  /** Render borders only. */
  public static final ShownBorders SURROUND = new ShownBorders("......tttt");

  /** Render all borders and separators. */
  public static final ShownBorders ALL = new ShownBorders("tttttttttt");

  private boolean headerSeparator;

  private boolean middleSeparator;

  private boolean footerSeparator;

  private boolean leftSeparator;

  private boolean centerSeparator;

  private boolean rightSeparator;

  private boolean topBorder;

  private boolean bottomBorder;

  private boolean leftBorder;

  private boolean rightBorder;

  /**
   * Creates a custom border style.
   * 
   * @param headerSeparator
   * @param middleSeparator
   * @param footerSeparator
   * @param leftSeparator
   * @param centerSeparator
   * @param rightSeparator
   * @param topBorder
   * @param bottomBorder
   * @param leftBorder
   * @param rightBorder
   */
  public ShownBorders(boolean headerSeparator, boolean middleSeparator,
      boolean footerSeparator, boolean leftSeparator, boolean centerSeparator,
      boolean rightSeparator, boolean topBorder, boolean bottomBorder,
      boolean leftBorder, boolean rightBorder) {
    this.headerSeparator = headerSeparator;
    this.middleSeparator = middleSeparator;
    this.footerSeparator = footerSeparator;
    this.leftSeparator = leftSeparator;
    this.centerSeparator = centerSeparator;
    this.rightSeparator = rightSeparator;
    this.topBorder = topBorder;
    this.bottomBorder = bottomBorder;
    this.leftBorder = leftBorder;
    this.rightBorder = rightBorder;
  }

  /**
   * Creates a custom border style using a 10-character String.
   * 
   * @param separatorsAndBordersToRender
   *          <p> A ten character string. Every character whose value is 't'
   *          represents a true value for the following borders or separators,
   *          in the following order: </p> <ul> <li>headerSeparator,</li> <li>middleSeparator,</li>
   *          <li>footerSeparator,</li> <li>leftSeparator,</li> <li>centerSeparator,</li>
   *          <li>rightSeparator,</li> <li>topBorder,</li> <li>bottomBorder,</li>
   *          <li>leftBorder,</li> <li>rightBorder.</li> </ul> <p> For
   *          example, a "......tttt" String will represent a borders-only
   *          style, </p>
   */

  public ShownBorders(final String separatorsAndBordersToRender) {
    this.headerSeparator = get(separatorsAndBordersToRender, 0);
    this.middleSeparator = get(separatorsAndBordersToRender, 1);
    this.footerSeparator = get(separatorsAndBordersToRender, 2);
    this.leftSeparator = get(separatorsAndBordersToRender, 3);
    this.centerSeparator = get(separatorsAndBordersToRender, 4);
    this.rightSeparator = get(separatorsAndBordersToRender, 5);
    this.topBorder = get(separatorsAndBordersToRender, 6);
    this.bottomBorder = get(separatorsAndBordersToRender, 7);
    this.leftBorder = get(separatorsAndBordersToRender, 8);
    this.rightBorder = get(separatorsAndBordersToRender, 9);
  }

  private boolean get(final String flags, final int index) {
    if (flags == null) {
      return false;
    }
    if (index < 0 || index > flags.length()) {
      return false;
    }
    return "t".equalsIgnoreCase(flags.substring(index, index + 1));
  }

  // Rendering methods.

  String renderTopBorder(final List<Column> columns, final BorderStyle tiles,
      final Row lowerRow) {
    return renderHorizontalSeparator(columns, tiles.getTLCorner(), tiles
        .getTCCorner(), tiles.getTRCorner(), tiles.getTop(), null, lowerRow,
        null, tiles.getTCCorner(), tiles.getCenterWidth());
  }

  String renderMiddleSeparator(final List<Column> columns,
      final BorderStyle tiles, final Row upperRow, final Row lowerRow) {
    return renderHorizontalSeparator(columns, tiles.getMLCorner(), tiles
        .getMCCorner(), tiles.getMRCorner(), tiles.getMiddle(), upperRow,
        lowerRow, tiles.getUpperColSpan(), tiles.getLowerColSpan(), tiles
            .getCenterWidth());
  }

  String renderBottomBorder(final List<Column> columns,
      final BorderStyle tiles, final Row upperRow) {
    return renderHorizontalSeparator(columns, tiles.getBLCorner(), tiles
        .getBCCorner(), tiles.getBRCorner(), tiles.getBottom(), upperRow, null,
        tiles.getBCCorner(), null, tiles.getCenterWidth());
  }

  private String renderHorizontalSeparator(final List<Column> columns,
      final String left, final String cross, final String right,
      final String horizontal, final Row upperRow, final Row lowerRow,
      final String upperColSpan, final String lowerColSpan,
      final int centerWidth) {
    StringBuffer sb = new StringBuffer();

    // Upper Left Corner

    if (this.showLeftBorder()) {
      sb.append(left);
    }

    // Cells

    int totalColumns = columns.size();
    for (int j = 0; j < totalColumns; j++) {

      // cell separator

      boolean upperSep = upperRow != null ? upperRow.hasSeparator(j) : false;
      boolean lowerSep = lowerRow != null ? lowerRow.hasSeparator(j) : false;

      logger.debug("upperSep=" + upperSep + " lowerSep=" + lowerSep
          + " centerWidth=" + centerWidth);

      if (j != 0) {
        if ((j > 1 && j < totalColumns - 1) && this.showCenterSeparator()
            || ((j == 1) && (this.showLeftSeparator()))
            || ((j == (totalColumns - 1)) && (this.showRightSeparator()))) {

          if (upperSep) {
            if (lowerSep) {
              sb.append(cross);
            } else {
              sb.append(upperColSpan);
            }
          } else {
            if (lowerSep) {
              sb.append(lowerColSpan);
            } else {
              logger.debug("centerWidth=" + centerWidth);
              for (int i = 0; i < centerWidth; i++) {
                sb.append(horizontal);
              }
            }
          }
        }
      }

      // Cell content

      Column col = columns.get(j);
      sb.append(Filler.getFiller(horizontal, col.getColumnWidth()));
    }

    // Right border

    if (this.showRightBorder()) {
      sb.append(right);
    }

    return sb.toString();
  }

  // Accessors

  boolean showBottomBorder() {
    return this.bottomBorder;
  }

  boolean showCenterSeparator() {
    return this.centerSeparator;
  }

  boolean showFooterSeparator() {
    return this.footerSeparator;
  }

  boolean showHeaderSeparator() {
    return this.headerSeparator;
  }

  boolean showLeftBorder() {
    return this.leftBorder;
  }

  boolean showLeftSeparator() {
    return this.leftSeparator;
  }

  boolean showMiddleSeparator() {
    return this.middleSeparator;
  }

  boolean showRightBorder() {
    return this.rightBorder;
  }

  boolean showRightSeparator() {
    return this.rightSeparator;
  }

  boolean showTopBorder() {
    return this.topBorder;
  }

}
