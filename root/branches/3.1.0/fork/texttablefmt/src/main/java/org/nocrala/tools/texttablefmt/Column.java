package org.nocrala.tools.texttablefmt;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

class Column {

  private static Logger logger = Logger.getLogger(Column.class);

  private int colIndex;

  private List<Cell> cells;

  private int minWidth;

  private int maxWidth;

  private boolean widthAlreadyCalculated;

  private int width;

  Column(final int colIndex, final int minWidth, final int maxWidth) {
    this.colIndex = colIndex;
    this.minWidth = minWidth;
    this.maxWidth = maxWidth;
    this.cells = new ArrayList<Cell>();
    this.widthAlreadyCalculated = false;
    this.width = 0;
  }

  Column(final int colIndex, final int width) {
    this.colIndex = colIndex;
    this.minWidth = width;
    this.maxWidth = width;
    this.cells = new ArrayList<Cell>();
    this.widthAlreadyCalculated = false;
    this.width = width;
  }

  void calculateWidth(final List<Column> columns, final int separatorWidth) {
    this.width = this.minWidth;
    for (Cell cell : this.cells) {
      int previousWidth = 0;
      if (cell.getColSpan() > 1) {
        for (int pos = this.colIndex - cell.getColSpan() + 1; pos < this.colIndex; pos++) {
          previousWidth = previousWidth + columns.get(pos).getColumnWidth()
              + separatorWidth;
          logger.debug("pos[" + pos + "] --> " + previousWidth);
        }
      }
      int cellTightWidth = cell != null ? cell.getTightWidth(this.maxWidth) : 0;
      int tw = cellTightWidth - previousWidth;
      logger.debug("cellTightWidth=" + cellTightWidth + " tw=" + tw);
      if (tw > this.width) {
        this.width = tw;
      }
    }
  }

  int getColumnWidth() {
    return this.width;
  }

  void add(final Cell cell) {
    this.cells.add(cell);
  }

  int getSize() {
    return this.cells.size();
  }

  Cell get(final int index) {
    return this.cells.get(index);
  }

  List<Cell> getCells() {
    return cells;
  }

  void setWidthRange(final int minWidth, final int maxWidth) {
    this.minWidth = minWidth;
    this.maxWidth = maxWidth;
  }

  void setWidth(final int width) {
    this.minWidth = minWidth;
    this.maxWidth = maxWidth;
    this.width = width;
  }

}
