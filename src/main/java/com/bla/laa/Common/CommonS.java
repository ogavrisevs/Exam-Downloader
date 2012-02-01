package com.bla.laa.Common;

import com.bla.laa.Net.Neti;
import com.bla.laa.Storage;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.Blob;
import java.text.SimpleDateFormat;
import java.util.*;

public class CommonS {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CommonS.class);
    //!!!  actual thread count =  -1, awt-Windows thread is created for all awt.image class by defualt
    public static int threadMaxCount = 2;
    public static int threadDelay = 3000 /* 10000 = 10 sec.*/;

    public static final String PICEXT = "jpg";
    public static final String HTMLEXT = "html";
    public static final String PRGPROPERTIES = "prg.properties";
    public static final String LINE_SEP = System.getProperty("line.separator", "\n");

    public static Integer strToInteger(String str) {
        Integer sk;
        try {
            sk = Integer.valueOf(str);
        } catch (NumberFormatException nfe) {
            logger.error("", nfe);
            return 0;
        }
        return sk;
    }

    public static String getUniqFileName() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyMMdd_HHmmss");
        return String.valueOf(formatter.format(new Date()));
    }

    public static void sleepThread() {
        try {
            Thread t = Thread.currentThread();
            //logger.debug( t.getName() +" : delay for "+  threadDelay + "  ");
            Thread.sleep(threadDelay);
        } catch (Exception e) {
            logger.error("", e);
            System.exit(1);
        }
    }

    public static void sleepThread(long millis) {
        try {
            Thread.sleep(millis);
        } catch (Exception e) {
            logger.error("", e);
            System.exit(1);
        }
    }

    public static void stopThread(Exception ex) throws MyCustException {
        logger.debug(ex.getMessage());
        stopThread();
    }

    public static void stopThread(String msg) throws MyCustException {
        logger.info(msg);
        stopThread();
    }

    public static void stopThread() throws MyCustException {
        Thread t = Thread.currentThread();
        logger.debug(t.getName() + " : stoping thread");
        printStackTrace();
        throw new MyCustException();
        //t.stop();
    }

    public static void printStackTrace() {
        try {
            Thread t = Thread.currentThread();
            StackTraceElement[] stack = t.getStackTrace();

            for (StackTraceElement ste : stack)
                if (!ste.getClassName().contains("CommonS"))
                    logger.debug("  " + ste.toString());

        } catch (Exception e) {
            logger.error("", e);
        }
    }

    /*
    public synchronized static void out(String msg) {
        try {
            Thread t = Thread.currentThread();
            logger.debug(t.getName() + " : " + msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    } */

    public static int getRandomInt(Integer randomizer) {
        Random rGen = new Random();
        return rGen.nextInt(randomizer);
    }

    public static String getRandomStr(Integer randomizer) {
        return String.valueOf(getRandomInt(randomizer));
    }

    /**
     * Save image, returns uniq file name
     */
    public static String saveImg(BufferedImage img) throws MyCustException {
        String fileName = "";
        try {
            fileName = CommonS.getUniqFileName() + "." + CommonS.PICEXT;
            File outputfile = new File(Neti.getTempDir() + fileName);
            ImageIO.write(img, CommonS.PICEXT, outputfile);
        } catch (Exception ex) {
            logger.error("", ex);
            CommonS.stopThread(ex.getMessage());
        }
        return fileName;
    }

    /**
     * Save image, with given fileName
     */
    public static void saveImg(BufferedImage img, String fileName) throws MyCustException {
        try {
            File outputfile = new File(Neti.getTempDir() + fileName);
            ImageIO.write(img, CommonS.PICEXT, outputfile);
        } catch (Exception ex) {
            logger.error("", ex);
            CommonS.stopThread(ex.getMessage());
        }
    }

    public static String saveBlob(Blob pic) {
        String fileName = "";
        try {
            fileName = Neti.getTempDir() + CommonS.getUniqFileName() + "." + CommonS.PICEXT;
            File fileOut = new File(fileName);
            FileOutputStream fileOutStr = new FileOutputStream(fileOut);
            byte[] buffer = new byte[0x1000];

            InputStream inputStr = pic.getBinaryStream();
            int read;
            while ((read = inputStr.read(buffer, (int) 0,
                    (int) buffer.length)) > 0)
                fileOutStr.write(buffer, 0, read);

            fileOutStr.close();
            inputStr.close();
        } catch (Exception e) {
            logger.error("", e);
        }
        return fileName;

    }

    /**
     * Savel list as web page on hdd
     *
     * @return - file name
     */
    public static String saveStringList(List<String> sList) {
        String longStr = "";
        for (String str : sList)
            longStr += str + LINE_SEP;
        return saveStringToFile(longStr, "");

    }

    public static String saveStringBuffer(StringBuffer sb, String fileName) {
        return saveStringToFile(sb.toString(), fileName);
    }

    public static String saveStringToFile(String longString, String fileName) {
        try {
            if ((fileName == null) || (fileName.length() == 0))
                fileName = Neti.getTempDir() + CommonS.getUniqFileName() + "." + CommonS.HTMLEXT;

            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8"));
            out.write(longString);
            out.close();

        } catch (Exception e) {
            logger.error("", e);
        }
        return fileName;

    }

    /**
     * parse string to integer
     */
    public static Integer parseInt(String intStr) {
        Integer sk = 0;
        try {
            sk = Integer.parseInt(intStr);
        } catch (NumberFormatException nfe) {
            logger.error("", nfe);
            return 0;
        }
        return sk;
    }

    void saveStream(InputStream response) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(response));
        String inputLine;
        File file = new File(Neti.getTempDir() + CommonS.getUniqFileName() + ".html");

        FileWriter fstream = new FileWriter(file);
        BufferedWriter out = new BufferedWriter(fstream);

        while ((inputLine = in.readLine()) != null)
            out.write(inputLine);

        out.close();
        in.close();
    }

    public void printStream(InputStream is) throws Exception {
        logger.debug("-------------------------------");
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        String inputLine;
        while ((inputLine = in.readLine()) != null)
            logger.info(inputLine);
        in.close();
    }

    /**
     * Load properties file, common props
     */
    public static void loadProps() {
        logger.info("CommonS.loadProps()");
        Properties prop = getProperties();

        threadMaxCount = Integer.valueOf((String) prop.get("threadMaxCount"));
        threadDelay = Integer.valueOf((String) prop.get("threadDelay"));

        Storage.servIP = (String) prop.get("servIP");
        Storage.servPort = Integer.valueOf(((String) prop.get("servPort")).trim());
        Storage.dbaseName = ((String) prop.get("dbaseName")).toUpperCase();
        Storage.dbaseDir = ((String) prop.get("dbaseDir")).toUpperCase();
        Storage.schemName = ((String) prop.get("schemName")).toUpperCase();


        String useTrace = ((String) prop.get("traceOn")).toUpperCase();
        Storage.traceOn = Boolean.valueOf(useTrace);
        if (Storage.traceOn)
            Storage.traceDir = ((String) prop.get("traceDir")).toUpperCase();
    }

    /**
     * Load properties file, proxy props
     */
    public static void loadProxyProps() {
        Properties prop = getProperties();

        String useDinamicProxy = ((String) prop.get("useDinamicProxy")).toUpperCase();
        Neti.setUseDinamicProxy(Boolean.valueOf(useDinamicProxy));

        String useStaticProxy = ((String) prop.get("useStaticProxy")).toUpperCase();
        Neti.setUseStaticProxy(Boolean.valueOf(useStaticProxy));
        if (Neti.getUseStaticProxy()) {
            Neti.setStaticProxyHost(((String) prop.get("staticProxyHost")).toUpperCase());
            Neti.setStaticProxyPort(((Integer) prop.get("staticProxyPort")));
        }

        String proxyBlackList = ((String) prop.get("proxyBlackList")).toUpperCase();
        if (!proxyBlackList.isEmpty()) {
            for (String proxy : proxyBlackList.split(" "))
                Neti.addProxyBlackList(proxy.trim());
        }

    }


    public static Properties getProperties() {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(PRGPROPERTIES));
        } catch (IOException e) {
            logger.error("", e);
        }
        System.getenv();
        /*
        logger.debug("--- read properties file : " + CommonS.PRGPROPERTIES);
        for (Object obj : properties.keySet())
            logger.debug(obj + " : " + properties.get(obj));

        logger.debug("--- end ---");
        */
        return properties;
    }

    public static List<String> splitEqually(String text, int size) {
        List<String> ret = new ArrayList<String>((text.length() + size - 1) / size);

        for (int start = 0; start < text.length(); start += size) {
            ret.add(text.substring(start, Math.min(text.length(), start + size)));
        }
        return ret;
    }

    public static boolean containsStartArgs(String[] array, String arg) {
        for (String arrayItme : array) {
            if (arg.contains(arrayItme))
                return true;
        }
        return false;
    }
}

