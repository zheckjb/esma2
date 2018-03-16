package com.bib.esma;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class Esma {
    private static final Logger logger = Logger.getLogger(Esma.class);
    private static final String LOG4J_PROPS = "log4j.properties";
    private static final String CONF_PROPS = "esma.properties";
    private static final String DAY_START = "T00:00:00Z";
    private static final String DAY_END = "T23:59:59Z";
    private static final String ERR_NO_ARGS = "No arguments found: expected <start date>#<end date>";
    private static final String inMsgDelim = "#";
    private static List<UrlList> linksArray= new ArrayList<>();

    public static void main(String[] args){
        try {
            new Esma().makeRequest(args[0]);
        } catch (Exception e) {
            System.out.println(ERR_NO_ARGS);
        }
    }

    public static void makeRequest(String inputString){
        try {
            EsmaProperties esmaProperties = new EsmaProperties();
            esmaProperties.initLogger(LOG4J_PROPS);
            esmaProperties.loadProperties(CONF_PROPS);
            String[] input = checkInput(inputString);
            if (input.length == 3 && input[2] != null) {
                esmaProperties.setDefFileType(input[2].toUpperCase());
            }
            String requestUrl  = buildUrl(buildDate(input[0],DAY_START),buildDate(input[1],DAY_END));
            logger.info("Request url: "+requestUrl);
            String listOfFiles = getListOfFiles(requestUrl);
            logger.debug(listOfFiles);
            getLinksbyJson(listOfFiles, esmaProperties.getDefFileType());
            if (linksArray.size() > 0 ) {
                ISINList isinList = new ISINList(esmaProperties);
                isinList.loadIsinList();
                DownloadEsmaFile downloadEsmaFile = new DownloadEsmaFile(esmaProperties);
                for (UrlList urlList : linksArray) {
                            downloadEsmaFile.downloadFile(urlList);
                }

            } else {
                logger.info(String.format("Links with type %s are not found",esmaProperties.getDefFileType()));
            }
        } catch (IOException e) {
            System.out.println("Fail "+e.getMessage());
        } catch (NullPointerException ex) {
            System.out.println("Fail  2 "+ex.getMessage());
        } catch (Exception ex2){
            logger.error(ex2.getMessage());
            System.out.println(ex2.getMessage());
        }
    }
    private static String getListOfFiles(String url) throws Exception  {
        System.setProperty("java.net.useSystemProxies", "true");
        StringBuilder response = new StringBuilder();
        URL reqhttp = new URL(url);
        HttpURLConnection con = (HttpURLConnection) reqhttp.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        int responseCode = con.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
        }else {
            throw new RuntimeException("Failed to download");
        }
        return response.toString();
    }

    private static void getLinksbyJson(String jsonText, String type) {
        JSONObject jsonObj = new JSONObject(jsonText);
        JSONObject jsonResp = jsonObj.getJSONObject("response");
        int jsonArrayNum = jsonResp.getInt("numFound");
        logger.info("Number of elements received: "+jsonArrayNum);
        JSONArray jsonArray = jsonResp.getJSONArray("docs");
        for(int i = 0; i < jsonArray.length(); i++) {
            UrlList filesList = new UrlList();
            JSONObject jsonElement = jsonArray.getJSONObject(i);
            String fileType = jsonElement.getString("file_type");
            if(fileType.equals(type)) {
                logger.info(String.format("File name: %s from link: %s",jsonElement.getString("file_name"),jsonElement.getString("download_link")));
                filesList.setFileName(jsonElement.getString("file_name"));
                filesList.setFileUrl(jsonElement.getString("download_link"));
                filesList.setFileType(fileType);
                linksArray.add(filesList);
            }
        }
    }

    private static String[] checkInput(String inputString) {
        logger.info("Input parameters: "+inputString);
        String[] input = inputString.split(inMsgDelim);
        if (input.length < 2) {
            throw new RuntimeException(ERR_NO_ARGS);
        }
        return input;
    }

    private static String buildDate(String indate, String addon) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate date = LocalDate.parse(indate,formatter);
        return String.format("%s%s",date.toString(),addon);
    }

    private static String buildUrl(String startDate,String endDate) {
        StringBuilder http = new StringBuilder();
        http.append("https://registers.esma.europa.eu/solr/esma_registers_firds_files/select?");
        http.append("q=*&");
        http.append("fq=publication_date:%5B");
        http.append(startDate);
        http.append("%20TO%20");
        http.append(endDate);
        http.append("%5D&wt=json&indent=true&start=0&rows=100");
        return http.toString();
    }

}
