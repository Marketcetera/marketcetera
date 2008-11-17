/**
 * @author Stefan Haustein
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 */
import java.io.*;
import org.xmlpull.v1.*;
import org.kxml2.io.*;

class Node {
    private String text;
    private Node yes;  // if null, it's an answer
    private Node no;
    
    Node(String answer) {
        this.text = answer;
    }
    
    Node(String question, Node yes, Node no) {
        this.text = question;
        this.yes = yes;
        this.no = no;        
    }
    
    void run () throws IOException {
       
        if (yes == null) 
            System.out.println ("Answer: "+text);
        else {
            System.out.println (text+ " (y/n)");    

            while (true) {
                int i = System.in.read();
                if (i == 'y' || i == 'Y') {
                    yes.run();
                    break;
                }
                else if (i == 'n' || i == 'N') {
                    no.run();            
                    break;
                }
            }
        }                
    }

}

public class YesNoGame {

    public static Node parseAnswer(XmlPullParser p) throws IOException, XmlPullParserException {
        p.require(p.START_TAG, "", "answer");
        Node result = new Node (p.nextText());
        p.require(p.END_TAG, "", "answer");
        return result;
    }

    public static Node parseQuestion(XmlPullParser p) throws IOException, XmlPullParserException {
        p.require(p.START_TAG, "", "question");
        String text = p.getAttributeValue("", "text");
        Node yes = parseNode (p);
        Node no = parseNode (p);
        p.nextTag();
        p.require(p.END_TAG, "", "question");
        return new Node (text, yes, no);
    }

    public static Node parseNode (XmlPullParser p) throws IOException, XmlPullParserException {
        p.nextTag ();
        p.require(p.START_TAG, "", null);
        if (p.getName().equals("question"))
            return parseQuestion(p);
        else 
            return parseAnswer(p);       
    }


    public static void main(String[] args) throws IOException, XmlPullParserException {
        String sample = "<question text='Is it round?'>\n"
                      + " <question text='Is it bright?'>\n"
                      + "  <answer>It is the Sun!</answer>\n"
                      + "  <answer>It is a ball!</answer>\n"
                      + " </question>\n"
                      + " <answer>I do not know!</answer>\n"
                      + "</question>\n";

        XmlPullParser p = new KXmlParser();
        p.setInput (new StringReader (sample));
        
        Node game = parseNode (p);
        
        game.run();        
    }
}
