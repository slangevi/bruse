package sc.bruse.parser;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import org.xml.sax.helpers.XMLReaderFactory;
import java.io.*;
import java.util.*;


/**
  * This class is a parser for files in the XML-BIF format 
  *
  * @author Laura Zavala
  * @version v1.0
  */
public class XMLBIFParser extends DefaultHandler {
	
	/*public static int NONE = 0;
	public static int READING_VARIABLE = 1;
	public static int READING_DEFINITION = 2;
	public static String VARIABLE = "variable";
	public static String DEFINITION = "definition";
	public static String NAME = "name";
	public static String OUTCOME = "outcome";
	public static String PROPERTY = "property";
	public static String FOR = "for";
	public static String GIVEN = "given";
	public static String TABLE = "table";
	
	private BayesianNet bayNet;	
	private String currentElement = "";
	private int state = NONE;
	private String nodeName = "";
	private Vector states = new Vector();
	private double xPosition = 0;
	private double yPosition = 0;
	private String childNode = "";
	private Vector parentList = new Vector();
	private String probabilities = "";


	public XMLBIFParser (BayesianNet bn) {
		super();
		bayNet = bn;		
	}


	public void startElement(String namespaceURI,
                             String sName, // simple name (localName)
                             String qName, // qualified name
                             Attributes attrs) throws SAXException {
		String eName = sName; // element name
		if ("".equals(eName)) eName = qName; // namespaceAware = false
		currentElement = eName.toLowerCase();		
		if (currentElement.equals(VARIABLE)) state = READING_VARIABLE;
		else if (currentElement.equals(DEFINITION)) state = READING_DEFINITION;
    }

    public void endElement(String namespaceURI,
                           String sName, // simple name
                           String qName  // qualified name
                          ) throws SAXException {
    	String eName = sName; // element name
    	if ("".equals(eName)) eName = qName; // namespaceAware = false	
		eName = eName.toLowerCase();
		if (eName.equals(VARIABLE)) {
			bayNet.addNode(nodeName, xPosition, yPosition, states);
			nodeName = "";
			states = new Vector();
			xPosition = 0;
			yPosition = 0;
			state = NONE;
		}
		if (eName.equals(DEFINITION)) {
			bayNet.addProbabilities(childNode, parentList, probabilities, false);
			childNode = "";
			parentList = new Vector();
			probabilities = "";
			state = NONE;
		}
    }


    public void endDocument() throws SAXException
    {
    }

    public void characters(char buf[], int offset, int len) throws SAXException {	
    	String s = (new String(buf, offset, len)).trim();
    	if (s.length() >0) {
    		if (state == READING_VARIABLE) {		
    			if (currentElement.equals(NAME)) {
    				nodeName = s;
    			}
    			else if (currentElement.equals(OUTCOME)) {
    				states.add(s);
    			}
    			else if (currentElement.equals(PROPERTY)) {
    				// Remove by Scott - not needed
    				//int startCoordinates = s.indexOf("(");
    				//int endCoordinates = s.indexOf(",",startCoordinates);
	                //xPosition = new Double(s.substring(startCoordinates+1, endCoordinates)).doubleValue();
	                //startCoordinates = endCoordinates;
	                //endCoordinates = s.indexOf(")",startCoordinates);
	                //yPosition = new Double(s.substring(startCoordinates+1, endCoordinates)).doubleValue();
    			}		
    		}
    		else if (state == READING_DEFINITION){
    			if (currentElement.equals(FOR)) {
    				childNode = s;
    			}
    			else if (currentElement.equals(GIVEN)) {
    				parentList.addElement(s);
    			}
    			else if (currentElement.equals(TABLE)) {
    				probabilities = s;
    			}
    		}
    	}
    }*/
}




