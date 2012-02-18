/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wikipedia_solr;

import java.io.IOException;

import de.tudarmstadt.ukp.wikipedia.api.exception.WikiApiException;
import de.tudarmstadt.ukp.wikipedia.parser.ParsedPage;
import de.tudarmstadt.ukp.wikipedia.parser.Paragraph;

import de.tudarmstadt.ukp.wikipedia.parser.Section;
import de.tudarmstadt.ukp.wikipedia.parser.mediawiki.MediaWikiParser;
import de.tudarmstadt.ukp.wikipedia.parser.mediawiki.MediaWikiParserFactory;
import wikipedia_solr.SimpleParser;
/**
 *
 * @author patrickmullen
 */
public class Wikipedia_solr {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        String documentText = de.tudarmstadt.ukp.wikipedia.parser.tutorial.TestFile.getFileText();

        //get a ParsedPage object
            SimpleParser sp = new SimpleParser(documentText);
            System.out.println(sp.getParagraphText());
        /*
		MediaWikiParserFactory pf = new MediaWikiParserFactory();
		MediaWikiParser parser = pf.createParser();
		ParsedPage pp = parser.parse(documentText);
		for(Paragraph p : pp.getParagraphs()){
                    
                    System.out.println(p.getText());
                }
                
		//get the sections
		for(Section section : pp.getSections()) {
			System.out.println("section : " + section.getTitle());
			System.out.println(" nr of paragraphs      : " + section.nrOfParagraphs());
			System.out.println(" nr of tables          : " + section.nrOfTables());
			System.out.println(" nr of nested lists    : " + section.nrOfNestedLists());
			System.out.println(" nr of definition lists: " + section.nrOfDefinitionLists());
		}
                */
    }
}
