package examples;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.nocrala.tools.texttablefmt.StreamingTable;

public class StreamingExample {

  public static void main(final String[] args) throws IOException {
    BufferedWriter w = new BufferedWriter(new FileWriter("build/output.txt"));

    StreamingTable t = new StreamingTable(w, 3);
    t.setColumnWidth(0, 8);
    t.setColumnWidth(1, 7);
    t.setColumnWidth(2, 4);
    
    t.addCell("Brand");
    t.addCell("Model");
    t.addCell("Year");
    t.addCell("Fiat");
    t.addCell("600");
    t.addCell("1976");
    t.addCell("Mitsubishi");
    t.addCell("Eclipse");
    t.addCell("1994");
    t.finishTable();

    w.close();
  }

}
