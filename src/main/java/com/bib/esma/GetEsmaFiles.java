package com.bib.esma;

import org.apache.log4j.Logger;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class GetEsmaFiles {
    private static final Logger logger = Logger.getLogger(GetEsmaFiles.class);
    private static final int BUFFER_SIZE = 4096;
    private EsmaProperties esmaProperties;

    public GetEsmaFiles(EsmaProperties properties){
        esmaProperties = properties;
    }

    public void downloadFile(UrlList urlList) throws IOException {
        URL url = new URL(urlList.getFileUrl());
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        int responseCode = httpConn.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            // opens input stream from the HTTP connection
            InputStream inputStream = httpConn.getInputStream();
            //String saveFilePath = saveDir + File.separator + fileName;
            String saveFilePath = esmaProperties.getWorkingPath() + File.separator + urlList.getFileName();
            logger.info("Process download to: "+saveFilePath);
            // opens an output stream to save into file
            FileOutputStream outputStream = new FileOutputStream(saveFilePath);
            int bytesRead = -1;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.close();
            inputStream.close();
            logger.info("File downloaded");
        } else {
            logger.error("No file to download. Server replied HTTP code: " + responseCode);
        }
        httpConn.disconnect();
    }

    public void unZipIt(UrlList urlList) throws IOException {
        //get the zip file content
        String savedFilePath = esmaProperties.getWorkingPath() + File.separator + urlList.getFileName();
        logger.info("Process to unpack file: " + savedFilePath);
        ZipInputStream inputStream = new ZipInputStream(new FileInputStream(savedFilePath));
        //get the zipped file list entry
        ZipEntry ze = inputStream.getNextEntry();
        while (ze != null) {
            if (!ze.isDirectory()) {
                String fileName = esmaProperties.getWorkingPath() + File.separator + ze.getName();
                File newFile = new File(fileName);
                logger.info("Unpacking file: " + ze.getName());
                FileOutputStream outputStream = new FileOutputStream(newFile);
                byte[] buffer = new byte[BUFFER_SIZE];
                int bytesRead = -1;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.close();
                urlList.setFileXml(ze.getName());
            }
            ze = inputStream.getNextEntry();
        }
        inputStream.closeEntry();
        inputStream.close();
        logger.info("Removing zip fle: " + savedFilePath);
        File f = new File(savedFilePath);
        boolean delete = f.delete();
        if (!delete) {
            logger.error("Failed to delete file " + savedFilePath);
        }
    }

}
