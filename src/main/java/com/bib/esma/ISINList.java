package com.bib.esma;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class ISINList {
    private static final Logger logger = Logger.getLogger(ISINList.class);
    private static final String inMsgDelim = "#";
    private static Map<String,RecordISIN> isinListMap = new HashMap<>();
    private EsmaProperties esmaProperties;

    public ISINList(EsmaProperties properties){
        esmaProperties = properties;
    }
    public void loadIsinList (){
        logger.info("Loading "+esmaProperties.getSmFilePath());
        try {
            BufferedReader reader = new BufferedReader(new FileReader(esmaProperties.getSmFilePath()));
            String fline;
            while ((fline = reader.readLine()) != null) {
                String[] value = fline.split(inMsgDelim);
                RecordISIN sm = new RecordISIN();
                if (isinListMap.containsKey(value[1])) {
                    logger.debug("Duplicate ISIN record found:  " + value[0] + " " + value[1]);
                    sm = isinListMap.get(value[1]);
                    sm.addSmId(value[0]);
                } else {
                    sm.addSmId(value[0]);
                    sm.setSmTicker(value[1]);
                    if (value[3].equalsIgnoreCase("yes")) {
                        sm.setSmStatus(true);
                    } else {
                        sm.setSmStatus(false);
                    }
                    isinListMap.put(value[1],sm);
                }
            }
            logger.info("ISIN List loaded: "+isinListMap.size());
        } catch (IOException e) {
            logger.error("Unable to read from: "+esmaProperties.getSmFilePath());
        }

    }

    public void addIsin(String value) {
        if (isinListMap.containsKey(value)) {
            RecordISIN sm = isinListMap.get(value);
            sm.setSmStatus(true);
            isinListMap.put(value,sm);
            logger.debug(String.format("SM %s with ISIN %s updated with YES",sm.getSmId(0),value));
        }
    }

    public void remIsin(String value) {
        if (isinListMap.containsKey(value)) {
            RecordISIN sm = isinListMap.get(value);
            sm.setSmStatus(false);
            isinListMap.put(value,sm);
            logger.debug(String.format("SM %s with ISIN %s updated with NO",sm.getSmId(0),value));
        }
    }
    public boolean matchIsin (String value){
        return isinListMap.containsKey(value);
    }

    public void saveIsinList () {
        logger.info("Saving "+esmaProperties.getIsinFilePath());
        try {
            File file = new File(esmaProperties.getIsinFilePath());
            FileWriter fileHandle = new FileWriter(file);
            for (Map.Entry<String, RecordISIN> value : isinListMap.entrySet()) {
                if (value.getValue().smIdLen() > 1) {
                    for (int i = 0; i < value.getValue().smIdLen(); i ++) {
                        fileHandle.append(String.format("%s#%s#%s \r\n", value.getValue().getSmId(i), value.getKey(), value.getValue().getSmStatus()));
                    }
                } else {
                    fileHandle.append(String.format("%s#%s#%s \r\n", value.getValue().getSmId(0), value.getKey(), value.getValue().getSmStatus()));
                }
            }
            fileHandle.flush();
            fileHandle.close();
        } catch ( IOException e) {
            logger.error("Unable to write to "+esmaProperties.getIsinFilePath());
        }
    }
}
