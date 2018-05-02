package com.bib.esma;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

public class EsmaProperties {
    private  String workingPath;
    private  String defFileType;
    private  String isinFilePath;
    private  String smFilePath;
    private  String xmlDLTPathNew;
    private  String xmlDLTPathTerm;
    private  String xmlFULPath;
    private  Integer bufferValue;
    private  Logger logger;


    public void initLogger(String propFileName) throws IOException {
        Properties properties = new Properties();
        URL url = ClassLoader.getSystemResource(propFileName);
        try (InputStream in = url.openStream()) {
            properties.load(in);
            PropertyConfigurator.configure(properties);
            logger = Logger.getLogger(EsmaProperties.class);
            logger.debug("Logger properties loaded "+url.toString());
        } catch (IOException e) {
            System.out.println("Unable to load "+ propFileName);
//        } catch (NullPointerException e) {
//            System.out.println("Unable to load "+propFileNamePath);
        }
    }

    public void loadProperties(String propFileName) throws Exception,RuntimeException,IOException,NullPointerException{
        Properties properties = new Properties();
        URL url = ClassLoader.getSystemResource(propFileName);
        logger.debug("Properties file: "+url.toString());
        try (InputStream in = url.openStream()){
            properties.load(in);
            setDefFileType(properties.getProperty("default.type").trim().toUpperCase());
            setWorkingPath(properties.getProperty("working.path"));
            setSmFilePath(properties.getProperty("isin.path").trim()+ File.separator+properties.getProperty("isin.in.file").trim());
            setIsinFilePath(properties.getProperty("isin.path").trim()+ File.separator+properties.getProperty("isin.out.file").trim());
            setXmlDLTPathNew(properties.getProperty("search.DLTINS.new").trim());
            setxmlDLTPathTerm(properties.getProperty("search.DLTINS.end").trim());
            setXmlFULPath(properties.getProperty("search.FULINS").trim());
        } catch (IOException e) {
//            System.out.println("Unable to load "+propFileNamePath);
            logger.error("1. Unable to load "+ propFileName);
            throw new RuntimeException("1. Unable to load "+ propFileName);
        } catch (NullPointerException e) {
//            System.out.println("Unable to load "+propFileNamePath);
            logger.error("2. Unable to load "+ propFileName);
            throw new RuntimeException("2. Unable to load "+ propFileName);
        } catch (Exception e) {
//            System.out.println("Unable to load "+propFileNamePath);
            logger.error("3. Unable to load "+ propFileName);
            throw new RuntimeException("3. Unable to load "+ propFileName);

        }
    }

    public  String getWorkingPath() {
        return workingPath;
    }

    private  void setWorkingPath(String workingPath) {
       logger.debug("Working path: "+ workingPath);
        this.workingPath = workingPath;
    }

    public  String getIsinFilePath() {
        return isinFilePath;
    }

    private  void setIsinFilePath(String isinFilePath) {
        logger.debug("Output file path: "+isinFilePath);
        this.isinFilePath = isinFilePath;
    }

    public  String getSmFilePath() {
        return smFilePath;
    }

    private  void setSmFilePath(String smFilePath) {
        logger.debug("Input file path: "+smFilePath);
        this.smFilePath = smFilePath;
    }

    public  Integer getBufferValue() {
        return bufferValue;
    }

    private  void setBufferValue(String bufferValue) {
        logger.debug("Buffer size: "+ bufferValue);
        this.bufferValue = Integer.parseInt(bufferValue);
    }

    public  String getXmlDLTPathNew() {
        return xmlDLTPathNew;
    }

    private  void setXmlDLTPathNew(String xmlDLTPathNew) {
        logger.debug("DLTINS new record search path: "+xmlDLTPathNew);
        this.xmlDLTPathNew = xmlDLTPathNew;
    }

    public  String getxmlDLTPathTerm() {
        return xmlDLTPathTerm;
    }

    private  void setxmlDLTPathTerm(String xmlDLTPathTerm) {
        logger.debug("DLTINS terminated record search path: "+xmlDLTPathTerm);
        this.xmlDLTPathTerm = xmlDLTPathTerm;
    }

    public  String getXmlFULPath() {
        return xmlFULPath;
    }

    private  void setXmlFULPath(String xmlFULPath) {
        logger.debug("FULINS record search path: "+ xmlFULPath);
        this.xmlFULPath = xmlFULPath;
    }

    public  String getDefFileType() {
        return defFileType;
    }

    public  void setDefFileType(String defFileType) {
        logger.debug("Files type: "+ defFileType);
        this.defFileType = defFileType;
    }
}
