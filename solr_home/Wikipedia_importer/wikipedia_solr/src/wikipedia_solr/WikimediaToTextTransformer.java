/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wikipedia_solr;

import org.apache.solr.handler.dataimport.Transformer;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.solr.handler.dataimport.Context;
//import wikipedia_solr.SimpleParser;

/**
 *
 * @author patrickmullen
 */
public class WikimediaToTextTransformer extends Transformer {

    public Map<String, Object> transformRow(Map<String, Object> row, Context context) {
        row.put("error", "false");
        String wikiMediaText = (String) row.get("wikimediaMarkup");
        int wmCount = wikiMediaText.length();
        row.put("wikimediaMarkupCount", wmCount);

        try {
            if (row.get("$skipDoc") == "true") {
                System.out.println("fast skipping" + row.get("id"));
                row.put("error", "true");
                row.put("exception", "RegexTransformerSucces");

                return row;

            }
            if (wikiMediaText.startsWith("#REDIRECT")) {
                System.out.println("regex transfromer failed on " + row.get("id"));
                row.put("error", "true");
                row.put("execption", "RegexTransformerFail");
                return row;
            }
            if (wikiMediaText != null) {
                //System.out.println("----input wikimediaMarkup -----");
                //System.out.println(wikiMediaText);
                SimpleParser sp = new SimpleParser(wikiMediaText);
                String jsp = sp.jsonSections();
                //System.out.println(jsp);
                row.put("sectionParsed", jsp);
                row.put("articlePlainText", sp.getParagraphText());
                int pltCount = sp.getParagraphText().length();

                row.put("articlePlainTextCount", pltCount);
                row.put("markupPlainRatio", ((float) pltCount) / (1 + wmCount));

                //System.out.println(sp.getParagraphText());
            }
        } catch (java.lang.Exception e) {
            System.out.println("error with  " + row.get("title"));
            System.out.println(e);
            row.put("exception", e);
            row.put("stackTrace", ExceptionUtils.getStackTrace(e));
            row.put("error", "true");
        }

        return row;
    }
}
