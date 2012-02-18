/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wikipedia_solr;

import de.tudarmstadt.ukp.wikipedia.api.exception.WikiApiException;
import de.tudarmstadt.ukp.wikipedia.parser.ParsedPage;
import de.tudarmstadt.ukp.wikipedia.parser.Paragraph;

import de.tudarmstadt.ukp.wikipedia.parser.Section;
import de.tudarmstadt.ukp.wikipedia.parser.mediawiki.MediaWikiParser;
import de.tudarmstadt.ukp.wikipedia.parser.mediawiki.MediaWikiParserFactory;

/**
 *
 * @author patrickmullen
 */
public class SimpleParser {

    private MediaWikiParserFactory mwpf;
    private MediaWikiParser parser;
    private ParsedPage pp;

    SimpleParser(String documentText) {
        this.mwpf = new MediaWikiParserFactory();
        this.parser = mwpf.createParser();
        this.pp = parser.parse(documentText);

    }

    public String getParagraphText() {
        StringBuilder sf = new StringBuilder();
        for (Paragraph p : pp.getParagraphs()) {
            sf.append(p.getText());
            System.out.println(p.getText());
        }
        return sf.toString();
    }
    
    public void foo(){
        Section s  = pp.getSection(0);
        
    }
}
