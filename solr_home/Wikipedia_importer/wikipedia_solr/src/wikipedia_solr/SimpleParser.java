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
import de.tudarmstadt.ukp.wikipedia.api.WikiConstants.Language;
import java.io.IOException;
import org.xml.sax.SAXException;

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

        this.mwpf = new MediaWikiParserFactory(Language.english);
        //this.mwpf = new MediaWikiParserFactory();
        this.mwpf.setTemplateParserClass(FlushTemplates.class);
        this.mwpf.setShowImageText(false);
        this.mwpf.setDeleteTags(false);
        this.parser = mwpf.createParser();
        //parser = new ModularParser();
        //FlushTemplates ft = new FlushTemplates();
        //parser.setTemplateParser(ft);

        //String a = stripRefs(documentText);
        //System.out.println(a.length());
        //this.pp = parser.parse(a);
        this.pp = parser.parse(documentText);
        this.cacheString = new StringBuilder();
    }

    public static String getRest(String corpus, String fastTerminator, String secondTerminator){

        int end2 = corpus.indexOf(secondTerminator);
        int end1;


        if (end2 == -1){
            end1 = corpus.indexOf(fastTerminator);
            if (end1 == -1) {
                return "";
            }
            else {
                return corpus.substring(end1 + fastTerminator.length());
            }
        }
        String fastTerminatorSearchSpace = corpus.substring(0, end2);
        end1 = fastTerminatorSearchSpace.indexOf(fastTerminator);
        if(end1 != -1 && end1 < end2){
            return  corpus.substring(end1+ fastTerminator.length());
        }
        return corpus.substring(end2 + secondTerminator.length());
    }
    public String getParagraphText2() {
        StringBuilder sf = new StringBuilder();
        try {
            for (Paragraph p : pp.getParagraphs()) {
                sf.append(p.getText());
            }
        } catch (java.lang.Exception e) {
            System.out.println(e.toString());

        }
        return sf.toString();
    }

    public String getParagraphText() {

        return this.cacheString.toString();
    }

    public static String stripTags(String corpus, String start, String end1, String end2) {


        String beginningPart = StringUtils.substringBefore(corpus, start);
        if (beginningPart.length() == corpus.length()){
            return corpus;
        }
        StringBuffer sb = new StringBuffer();        
        String rest = getRest(corpus, end1, end2);

        sb.append(beginningPart);
        int lastBeginningIndex = rest.indexOf(start);
        /*
        System.out.println("lastBeginningIndex");
        System.out.println(lastBeginningIndex);
        System.out.println("beginningPart");
        System.out.println(beginningPart);
        System.out.println("rest");
        System.out.println(rest);
        */

        //while (beginningPart != null && lastBeginningIndex != -1) {
        while (lastBeginningIndex != -1) {


            //System.out.println(beginningPart);
            beginningPart = rest.substring(0, lastBeginningIndex) ;
            //System.out.println("beginningPart");
            //System.out.println(beginningPart);
            rest = getRest(rest, end1, end2);
            //System.out.println("rest");
            //System.out.println(rest);

            sb.append(beginningPart);
            lastBeginningIndex = rest.indexOf(start);
            //System.out.println("lastBeginningIndex");
            //System.out.println(lastBeginningIndex);
            //System.out.println("end loop");
        }
        sb.append(rest);

        return sb.toString();
    }



    public static String stripRefsOrig(String withRefs){
        return stripTags(withRefs, "<ref", "/>", "/ref>");
    }
    public static String stripRefs(String withRefs) throws IOException, SAXException{
            HTMLTextParser htp = new HTMLTextParser();
            return htp.htmltoText(withRefs);

            //          return stripTags(withRefs, "<ref", "/>", "/ref>");

    }

    public ArrayList<HashMap<String, ArrayList<String>>> getSections() throws WikipediaParseException, IOException, SAXException {
        ArrayList<HashMap<String, ArrayList<String>>> sections =
                new ArrayList<HashMap<String, ArrayList<String>>>();

        java.util.List<Section> sectionList;
        try {
            sectionList = pp.getSections();
        } catch (java.lang.NullPointerException e) {
            throw new WikipediaParseException(e);
        }

        for (Section s : sectionList) {
            HashMap<String, ArrayList<String>> section =
                    new HashMap<String, ArrayList<String>>();
            String t = s.getTitle();
            if (t == null) {
                t = " ";
            }
            this.cacheString.append(t);
            this.cacheString.append(" ");
            ArrayList<String> paragraphTexts = new ArrayList<String>();
            for (Paragraph p : s.getParagraphs()) {
                //String pt = StringEscapeUtils.unescapeXml(p.getText());
                //String pt = p.getText();
                String pt = stripRefs(StringUtils.strip(p.getText()));
                //String pt = StringEscapeUtils.unescapeXml(StringUtils.strip(p.getText()));
                paragraphTexts.add(pt);
                this.cacheString.append(pt);
            }
            section.put(t, paragraphTexts);
            sections.add(section);
        }
        return sections;
    }

    public String jsonSections() throws WikipediaParseException, IOException, SAXException {
        Gson gson = new Gson();
        return gson.toJson(this.getSections());
    }
}
