/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wikipedia_solr;

import java.util.ArrayList;
import java.util.HashMap;

import de.tudarmstadt.ukp.wikipedia.api.exception.WikiApiException;
import de.tudarmstadt.ukp.wikipedia.parser.ParsedPage;
import de.tudarmstadt.ukp.wikipedia.parser.Paragraph;

import de.tudarmstadt.ukp.wikipedia.parser.Section;
import de.tudarmstadt.ukp.wikipedia.parser.mediawiki.MediaWikiParser;
import de.tudarmstadt.ukp.wikipedia.parser.mediawiki.MediaWikiParserFactory;
import de.tudarmstadt.ukp.wikipedia.parser.mediawiki.ModularParser;
import de.tudarmstadt.ukp.wikipedia.parser.mediawiki.FlushTemplates;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;

/**
 *
 * @author patrickmullen
 */
public class SimpleParser {

    private MediaWikiParserFactory mwpf;
    private MediaWikiParser parser;
    //private ModularParser parser;
    private ParsedPage pp;
    private StringBuilder cacheString;
    SimpleParser(String documentText) {

        this.mwpf = new MediaWikiParserFactory();
        this.mwpf.setTemplateParserClass(FlushTemplates.class);

        this.parser = mwpf.createParser();
        //parser = new ModularParser();
        //FlushTemplates ft = new FlushTemplates();
        //parser.setTemplateParser(ft);
        StringBuffer sb = new StringBuffer();
        String beginningPart =  StringUtils.substringBefore(documentText, "<ref");
        String rest = StringUtils.substringAfter(documentText, "ref>");
        /*
        System.out.println(beginningPart);
        System.out.println(rest);
        System.out.println("------------");
        */
        if(beginningPart == null || beginningPart == ""){            
           sb.append(documentText);
        }
        else {
            while(beginningPart != null && rest.indexOf("<ref") != -1){
                sb.append(beginningPart);
                //System.out.println(beginningPart);
                beginningPart =  StringUtils.substringBefore(rest, "<ref");
                rest = StringUtils.substringAfter(rest, "ref>");                               
            }
             sb.append(rest);
             //System.out.println(sb.toString());
        }
//        this.pp = parser.parse(documentText);
          this.pp = parser.parse(sb.toString());
        
//        this.pp = parser.parse(StringEscapeUtils.unescapeXml(documentText));
        this.cacheString = new StringBuilder();
    }

    public String getParagraphText2() {
        StringBuilder sf = new StringBuilder();
        try {
            for (Paragraph p : pp.getParagraphs()) {
                sf.append(p.getText());
                //System.out.println(p.getText());
            }
        } catch (java.lang.Exception e) {
            System.out.println(e.toString());

        }
        return sf.toString();
    }
    
    public String getParagraphText() {
        
        return  this.cacheString.toString();
    }
    
    public ArrayList <HashMap<String, ArrayList<String>>>getSections() {
        ArrayList <HashMap<String, ArrayList<String>>> sections = 
                new ArrayList<HashMap<String, ArrayList<String>>>();
        
        for (Section s : pp.getSections()) {
            HashMap <String, ArrayList<String>> section = 
                    new HashMap<String, ArrayList<String>>();
            String t = s.getTitle();
            if(t == null){
                t = " ";
                
            }
            this.cacheString.append(t);
            this.cacheString.append(" ");
            
            
            ArrayList <String> paragraphTexts =  new ArrayList<String>();
            for (Paragraph p: s.getParagraphs()){
                //String pt = StringEscapeUtils.unescapeXml(p.getText());
                String pt = p.getText();

                paragraphTexts.add(pt);
                this.cacheString.append(pt);
            }
//            System.out.println(s.getTitle());
            
            section.put(t, paragraphTexts);
            
            sections.add(section);
        }
        return sections;
    }
    
    public String jsonSections() {
        
     Gson gson = new Gson();

       return gson.toJson(this.getSections());
    
    }
     
    public void foo() {
        Section s = pp.getSection(0);

    }
}
