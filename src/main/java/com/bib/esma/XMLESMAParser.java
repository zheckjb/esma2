package com.bib.esma;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLESMAParser extends DefaultHandler {
    private static final Logger logger = Logger.getLogger(XMLESMAParser.class);
    private static final Integer CNTR_MOD = 1000;
    private XmlPath xmlPath;
    private ISINList isinList;
    private Integer cntr = 0;
    private Boolean flagAddIsin = false;
    private Boolean flagRemIsin = false;

    public XMLESMAParser(EsmaProperties properties, ISINList inList){
        isinList = inList;
        xmlPath = new XmlPath(properties);
    }
    @Override
    public void startDocument() throws SAXException {
        logger.debug("Start parse XML...");
    }

    @Override
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
        xmlPath.addElement(qName);
        if (xmlPath.compareFUL() || xmlPath.compareDLTnew()){
            flagAddIsin = true;
        } else if (xmlPath.compareDLTend()) {
            flagRemIsin = true;
        }
        if (flagAddIsin || flagRemIsin) {
            cntr ++;
            if (cntr != 0 && cntr % CNTR_MOD == 0) {
                logger.debug(String.format("%d records processed",cntr));
            }
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (flagAddIsin) {
            isinList.addIsin(new String(ch, start, length));
            flagAddIsin = false;
        }

        if (flagRemIsin) {
            isinList.remIsin(new String(ch, start, length));
            flagRemIsin = false;
        }
    }

    @Override
    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        xmlPath.remElement(qName);

    }

    @Override
    public void endDocument() {
        logger.debug("End file parse");
    }


}
