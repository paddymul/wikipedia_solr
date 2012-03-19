/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wikipedia_solr;

import com.google.gson.Gson;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.cli.*;

/**
 *
 * @author patrickmullen
 */
public class Wikipedia_solr {

    /**
     * @param args the command line arguments
     */
    public static String readFile(String filename) throws FileNotFoundException, IOException {

        FileInputStream fstream = new FileInputStream(filename);
        // Get the object of DataInputStream
        DataInputStream in = new DataInputStream(fstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String strLine;
        StringBuilder sb = new StringBuilder();
        while((strLine=br.readLine())!= null){
            
            sb.append(strLine);
        }
        return sb.toString();
        
    }

    public static void main(String[] args) throws ParseException, FileNotFoundException, IOException {
        // TODO code application logic here


        Gson gson = new Gson();

        Options o = new Options();
        o.addOption("inputFile", true, "path to input file");
        CommandLineParser parser = new PosixParser();
        CommandLine cmd = parser.parse( o, args);

        //String documentText2 = readFile(cmd.getOptionValue("inputFile"));
        String documentText2 = readFile("../../../test_docs/12");
        
        
        
        
        
        
        
                       //get a ParsedPage object
        SimpleParser sp = new SimpleParser(documentText2);
        System.out.println("------------------------------------------");
        
        try {
            sp.getSections();
            //System.out.println(sp.jsonSections());
        } catch (WikipediaParseException ex) {
            Logger.getLogger(Wikipedia_solr.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(sp.getParagraphText());
       }
}
