/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wikipedia_solr;

import java.io.*;
import java.io.FileInputStream;
import java.io.PrintWriter;

import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.InputSource;

import org.cyberneko.html.parsers.DOMFragmentParser;

import org.apache.xerces.dom.CoreDocumentImpl;
import org.xml.sax.SAXException;

public class HTMLTextParser {

    InputSource inSource = null;

    // HTMLTextParser Constructor 
    public HTMLTextParser() {
    }

    //Gets the text content from Nodes recursively
    String processNode(Node node) {
        if (node == null) {
            return "";
        }

        //Process a text node
        String nn = node.getNodeName();
        if (nn.equalsIgnoreCase("REF") || nn.equalsIgnoreCase("CITE")) {
            return "";
        } else {

            return getNodeTextContent(node);
        }
    }

    public String getNodeTextContent(Node node) {
        StringBuffer sb = new StringBuffer();

        if (node.getNodeType() == node.TEXT_NODE) {
            sb.append(node.getNodeValue());
        } else if (node.hasChildNodes()) {
            //Process the Node's children 

            NodeList childList = node.getChildNodes();
            int childLen = childList.getLength();

            for (int count = 0; count < childLen; count++) {
                sb.append(processNode(childList.item(count)));
            }
        }
        return sb.toString();
    }

    // Extracts text from HTML Document
    String htmltoText(String inputText) throws IOException, SAXException {

        String wrappedInput = "<foo>" + inputText + "</foo>";
        DOMFragmentParser parser = new DOMFragmentParser();

        CoreDocumentImpl codeDoc = new CoreDocumentImpl();
        DocumentFragment doc = codeDoc.createDocumentFragment();

        InputSource iSource = new InputSource(new StringReader(wrappedInput));

        parser.parse(iSource, doc);
        //Node is a super interface of DocumentFragment, so no typecast needed
        return processNode(doc);
    }
}
