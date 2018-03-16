package com.bib.esma;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;

public class SearchISIN {
    private static final Logger logger = Logger.getLogger(SearchISIN.class);
    private EsmaProperties esmaProperties;

    public SearchISIN(EsmaProperties properties){
        esmaProperties = properties;
    }

    public void parseXml (UrlList urlList,ISINList isinList) throws IOException {
        String xmlInFilePath = esmaProperties.getWorkingPath() + File.separator + urlList.getFileXml();
        try {
            File inFile = new File(xmlInFilePath);
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            XMLESMAParser saxp = new XMLESMAParser(esmaProperties, isinList);

            parser.parse(inFile, saxp);
            boolean delete = inFile.delete();
            if (!delete) {
                logger.error("Failed to delete file " + xmlInFilePath);
            }
        } catch (SAXException e){
            logger.error("Parse failed");
        } catch (ParserConfigurationException e) {
            logger.error("Parse error "+e.getMessage());
        }

    }
}
