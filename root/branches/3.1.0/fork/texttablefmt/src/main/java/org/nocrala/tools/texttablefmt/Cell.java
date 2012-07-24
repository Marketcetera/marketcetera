package org.nocrala.tools.texttablefmt;

class Cell {

  private String content;

  private CellStyle style;

  private int colSpan;

  Cell(final String content, final CellStyle style, final int colSpan) {
    this.content = content;
    this.style = style;
    this.colSpan = colSpan;
  }

  int getTightWidth(final int maxWidth) {
    int width = this.style.getWidth(this.content);
    return width > maxWidth ? maxWidth : width;
  }

  public String render(final int width) {
    return this.style.render(this.content, width);
  }

  public String getContent() {
    return this.content;
  }

  public CellStyle getStyle() {
    return this.style;
  }

  public int getColSpan() {
    return this.colSpan;
  }

}
