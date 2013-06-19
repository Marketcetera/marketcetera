package examples;

import org.nocrala.tools.texttablefmt.Table;

public class Basic {

  public static void main(final String[] args) {
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
    System.out.println(t.render());
  }

}
