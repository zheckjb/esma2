package com.bib.esma;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class XmlPath {
    private static final Logger logger = Logger.getLogger(XmlPath.class);
    private EsmaProperties esmaProperties;
    private List<String> xmlPathArr = new ArrayList<>();
    private String xmlPath;
    private String searchFULPath;
    private String searchDLTNew;
    private String searchDLTEnd;

    public XmlPath(EsmaProperties properties){
        esmaProperties = properties;
        searchDLTNew = properties.getXmlDLTPathNew();
        searchDLTEnd = properties.getxmlDLTPathTerm();
        searchFULPath = properties.getXmlFULPath();
    }
    public void remElement(String value) {
        int i = xmlPathArr.size();
        if (i > 0) {
            xmlPathArr.remove(i-1);
        }
        xmlPath = buildPath();
    }

    public void addElement(String value) {
        xmlPathArr.add(value);
        xmlPath = buildPath();
    }

    public boolean compareFUL(){
        return searchFULPath.equals(xmlPath);
    }

    public boolean compareDLTnew(){
        return searchDLTNew.equals(xmlPath);
    }

    public boolean compareDLTend() {
        return searchDLTEnd.equals(xmlPath);
    }


    public String buildPath() {
        String result = null;
        for (String value : xmlPathArr) {
            if (result != null) {
                result = String.format("%s/%s",result,value);
            } else {
                result = value;
            }
        }
        return result;
    }

    @Override
    public String toString(){
        return buildPath();
    }
}
