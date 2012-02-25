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
public class WikimediaToTextTransformer extends Transformer
{

    public Map<String, Object> transformRow(Map<String, Object> row, Context context) {

        String wikiMediaText = (String) row.get("wikimediaMarkup");
        if(wikiMediaText != null){
            SimpleParser sp = new SimpleParser(wikiMediaText);
            sp.getSections();
            row.put("articlePlainText", sp.getParagraphText());
            row.put("sectionParsed",    sp.jsonSections());
                    }   
        return row;
    }
}
