/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wikipedia_solr;

import java.util.ArrayList;
import java.util.HashMap;

import de.tudarmstadt.ukp.wikipedia.parser.ParsedPage;
import de.tudarmstadt.ukp.wikipedia.parser.Paragraph;

import de.tudarmstadt.ukp.wikipedia.parser.Section;
import de.tudarmstadt.ukp.wikipedia.parser.mediawiki.MediaWikiParser;
import de.tudarmstadt.ukp.wikipedia.parser.mediawiki.MediaWikiParserFactory;
import de.tudarmstadt.ukp.wikipedia.parser.mediawiki.FlushTemplates;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import de.tudarmstadt.ukp.wikipedia.api.WikiConstants.Language;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
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

    SimpleParser(String documentText) throws UnsupportedEncodingException {

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
        this.pp = parser.parse(toUTF8(documentText));
        this.cacheString = new StringBuilder();
    }

    public String getParagraphText() throws UnsupportedEncodingException {

        //return this.toUTF8( this.cacheString.toString());
        return this.cacheString.toString();
    }

    public String toUTF8(String original) throws UnsupportedEncodingException {

        //String s = "A função, Ãugent";
        String r = original.replaceAll("\\P{InBasic_Latin}", "");
        return r;
    }

    public String jsonSections() throws WikipediaParseException, IOException, SAXException {
        Gson gson = new Gson();
        
        
        return stripSingleQuotes(gson.toJson(this.getSections()));
    }
    public static String stripSingleQuotes(String original){

        String r = original.
                replaceAll(".u003d","=").
                replaceAll(".u003a",":").
                replaceAll(".u003b",";").
                replaceAll(".u003c","<").
                replaceAll(".u003e",">").
                replaceAll(".u003f","?").
                replaceAll(".u007b","{").
                replaceAll(".u007c","|").
                replaceAll(".u007d","}").
                replaceAll(".u007e","~").
                replaceAll(".u0021","!").
                replaceAll(".u0023","#").
                replaceAll(".u0024","$").
                replaceAll(".u0025","%").
                replaceAll(".u0026","&").
                replaceAll(".u0027","'").
                replaceAll(".u0028","(").
                replaceAll(".u0029",")").
                replaceAll(".u002a","*").
                replaceAll(".u002b","+").
                replaceAll(".u002c",",").
                replaceAll(".u002d","-").
                replaceAll(".u002e",".").
                replaceAll(".u002f","/");
                
                
                
        return r;
    }

    /*
     * public String toUTF8(String original ) throws
     * UnsupportedEncodingException{
     *
     * byte[] defaultBytes = original.getBytes();
     *
     * String roundTrip = new String(defaultBytes,"UTF8"); return roundTrip;
     *
     *
     * }
     *
     */
   
    public static String getRest(String corpus, String fastTerminator, String secondTerminator) {

        int end2 = corpus.indexOf(secondTerminator);
        int end1;


        if (end2 == -1) {
            end1 = corpus.indexOf(fastTerminator);
            if (end1 == -1) {
                return "";
            } else {
                return corpus.substring(end1 + fastTerminator.length());
            }
        }
        String fastTerminatorSearchSpace = corpus.substring(0, end2);
        end1 = fastTerminatorSearchSpace.indexOf(fastTerminator);
        if (end1 != -1 && end1 < end2) {
            return corpus.substring(end1 + fastTerminator.length());
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

    public static String stripTags(String corpus, String start, String end1, String end2) {


        String beginningPart = StringUtils.substringBefore(corpus, start);
        if (beginningPart.length() == corpus.length()) {
            return corpus;
        }
        StringBuffer sb = new StringBuffer();
        String rest = getRest(corpus, end1, end2);

        sb.append(beginningPart);
        int lastBeginningIndex = rest.indexOf(start);
        /*
         * System.out.println("lastBeginningIndex");
         * System.out.println(lastBeginningIndex);
         * System.out.println("beginningPart");
         * System.out.println(beginningPart); System.out.println("rest");
         * System.out.println(rest);
         */

        //while (beginningPart != null && lastBeginningIndex != -1) {
        while (lastBeginningIndex != -1) {


            //System.out.println(beginningPart);
            beginningPart = rest.substring(0, lastBeginningIndex);
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

    public static String stripRefsOrig(String withRefs) {
        return stripTags(withRefs, "<ref", "/>", "/ref>");
    }

    public static String stripRefs(String withRefs) throws IOException, SAXException {
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

}
