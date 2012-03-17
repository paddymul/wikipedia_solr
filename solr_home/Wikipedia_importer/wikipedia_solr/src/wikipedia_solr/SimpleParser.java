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


        this.pp = parser.parse(stripRefs(documentText));
        //this.pp = parser.parse(documentText);
        this.cacheString = new StringBuilder();
    }

    public static String getRest(String corpus, String fastTerminator, String secondTerminator){
        int end1 = corpus.indexOf(fastTerminator);
        int end2 = corpus.indexOf(secondTerminator);


        if (end1 == -1 && end2 == -1) {
            //return corpus;
            return "";
        }
        if (end1 == -1 || end2 == -1) {
            // we wan't the indexof greater than -1
            if(end1 < end2){
                return  StringUtils.substringAfter(corpus, secondTerminator);
            }
            return StringUtils.substringAfter(corpus, fastTerminator);
        }
        if(end1 < end2){
            return StringUtils.substringAfter(corpus, fastTerminator);        
        }
        return  StringUtils.substringAfter(corpus, secondTerminator);        
    }

    public static String stripTags(String corpus, String start, String end1, String end2) {

        StringBuffer sb = new StringBuffer();
        String beginningPart = StringUtils.substringBefore(corpus, start);

        
        String rest = getRest(corpus, end1, end2);
        // StringUtils.substringAfter(withRefs, "/ref>");

        sb.append(beginningPart);
        while (beginningPart != null && rest.indexOf(start) != -1) {

            beginningPart = StringUtils.substringBefore(rest, start);
            //rest = StringUtils.substringAfter(rest, "/ref>");
            rest = getRest(rest, end1, end2);
            sb.append(beginningPart);
        }
        sb.append(rest);

        return sb.toString();
    }

    public static String stripRefs(String withRefs){
        return stripTags(withRefs, "<ref", "/>", "/ref>");
        /*
        StringBuffer sb = new StringBuffer();
        String beginningPart = StringUtils.substringBefore(withRefs, "<ref");

        String rest = StringUtils.substringAfter(withRefs, "/ref>");

        sb.append(beginningPart);
        while (beginningPart != null && rest.indexOf("<ref") != -1) {

            beginningPart = StringUtils.substringBefore(rest, "<ref");
            rest = StringUtils.substringAfter(rest, "/ref>");
            sb.append(beginningPart);
        }
        sb.append(rest);

        return sb.toString();
        */
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

    public ArrayList<HashMap<String, ArrayList<String>>> getSections() throws WikipediaParseException {
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
                String pt = p.getText();
                paragraphTexts.add(StringUtils.strip(pt));
                this.cacheString.append(pt);
            }
            section.put(t, paragraphTexts);
            sections.add(section);
        }
        return sections;
    }

    public String jsonSections() throws WikipediaParseException {
        Gson gson = new Gson();
        return gson.toJson(this.getSections());
    }
}
