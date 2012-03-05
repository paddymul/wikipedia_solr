/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wikipedia_solr;

import org.apache.solr.handler.dataimport.Transformer;
import java.util.List;
import java.util.Map;
import org.apache.solr.handler.dataimport.Context;
//import wikipedia_solr.SimpleParser;

/**
 *
 * @author patrickmullen
 */
public class WikimediaToTextTransformer extends Transformer {

    public Map<String, Object> transformRow(Map<String, Object> row, Context context) {
        try {
            String wikiMediaText = (String) row.get("wikimediaMarkup");
            if (wikiMediaText != null) {
                //System.out.println("----input wikimediaMarkup -----");
                //System.out.println(wikiMediaText);
                SimpleParser sp = new SimpleParser(wikiMediaText);
                String jsp = sp.jsonSections();
                //System.out.println(jsp);
                row.put("sectionParsed", jsp);
                row.put("articlePlainText", sp.getParagraphText());
                //System.out.println(sp.getParagraphText());
            }
        } catch (java.lang.Exception e) {
            System.out.println("error with  " + row.get("title"));
            System.out.println(e);

        }
        return row;
    }
}
