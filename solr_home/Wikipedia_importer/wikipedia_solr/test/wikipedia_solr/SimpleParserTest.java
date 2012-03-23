/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wikipedia_solr;

import java.io.IOException;
import java.util.ArrayList;
import org.junit.*;
import static org.junit.Assert.*;
import org.xml.sax.SAXException;

/**
 *
 * @author patrickmullen
 */
public class SimpleParserTest {
    
    public SimpleParserTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of stripRefs method, of class SimpleParser.
     */


    @Test
    public void testDivsUnmolested() throws IOException, SAXException {
        System.out.println("testDivsUnmolested");
        String withRefs = "asdf<div> we should still see this </div> after ref";
        String expResult = "asdf we should still see this  after ref";
        String result = SimpleParser.stripRefs(withRefs);
        System.out.println(expResult);
        System.out.println(result);
        assertEquals(expResult, result);
    }


    @Test
    public void testStripRefs() throws IOException, SAXException {
        System.out.println("stripRefs");
        String withRefs = "asdf<ref> inside ref</ref> after ref";
        String expResult = "asdf after ref";
        String result = SimpleParser.stripRefs(withRefs);
        System.out.println(expResult);
        System.out.println(result);
        assertEquals(expResult, result);
    }
    @Test
    public void testStripRefs2() throws IOException, SAXException {
        System.out.println("stripRefs2");
        String withRefs = " no refs ";
        String expResult = " no refs ";
        String result = SimpleParser.stripRefs(withRefs);
        System.out.println(expResult);
        System.out.println(result);
        assertEquals(expResult, result);
    }
    @Test
    public void testStripRefs3() throws IOException, SAXException {
        System.out.println("stripRefs3");
        String withRefs = "<ref> blah </ref> starts with ref";
        String expResult = " starts with ref";
 
        String result = SimpleParser.stripRefs(withRefs);
        System.out.println(expResult);
        System.out.println(result);
        assertEquals(expResult, result);
    }

    @Test
    public void testStripRefs_noRef() throws IOException, SAXException {
        System.out.println("stripRefs_noRef");
        String withRefs = " blah starts with ref";
        String expResult = " blah starts with ref";
        String result = SimpleParser.stripRefs(withRefs);
        System.out.println(expResult);
        System.out.println(result);
        assertEquals(expResult, result);
    }

 
    @Test
    public void testStripRefsSelfClosing() throws IOException, SAXException {
        System.out.println("stripRefsSelfClosing");
        String withRefs = "words before <ref  blah /> starts with ref";
        String expResult = "words before  starts with ref";
        
        String result = SimpleParser.stripRefs(withRefs);
        System.out.println(expResult);
        System.out.println(result);
        assertEquals(expResult, result);
    }
    @Test
    public void testgetRest() {
        System.out.println("testGetRest");
        String corpus = " blah /> starts with ref";
        String expResult = " starts with ref";
        String result = SimpleParser.getRest(corpus, "/>", "/ref>");
        System.out.println(expResult);
        System.out.println(result);
        assertEquals(expResult, result);
    }

    @Test
    public void testgetRestSecondTerminator() {
        System.out.println("testGetRest2");
        String corpus = " blah /> </ref> starts with ref";
        String expResult = " </ref> starts with ref";
        String result = SimpleParser.getRest(corpus, "/>", "/ref>");
        System.out.println(expResult);
        System.out.println(result);
        assertEquals(expResult, result);
    }

   @Test
    public void testStripRefs_doubleRef() throws IOException, SAXException {
        System.out.println("testStripRefs_doubleRef");

        String withRefs = "before first <ref> blah </ref> in between <ref> blah " +
                           " </ref> after";

        String expResult = "before first  in between  after";
        String result = SimpleParser.stripRefs(withRefs);
        System.out.println(expResult);
        System.out.println(result);
        assertEquals(expResult, result);
    }

}
