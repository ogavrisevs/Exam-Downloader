package com.bla.laa.Net;

import org.slf4j.LoggerFactory;

import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class Cookies {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Cookies .class);
    private static final String SET_COOKIE = "Set-Cookie";
    private static final String COOKIE = "Cookie";

    private final Map<String, String> cookieList = new HashMap<String, String>();

    public void chekForNewCookies(URLConnection con) {
        Map<String, String> newCookies = getCookies(con);
        if (newCookies.size() != 0) {
            logger.info("Found new cokies ! : " + printCokkieList(newCookies));
            setReplaceNewCokkie(newCookies);
        }
    }

    public URLConnection addCookieToCon(URLConnection con) {
        if (con.getRequestProperty(COOKIE) == null) {
            Set<String> cokieKeys = cookieList.keySet();
            for (String key : cokieKeys)
                con.addRequestProperty(COOKIE, key + "=" + cookieList.get(key));
        }
        return con;
    }

    private Map<String, String> getCookies(URLConnection con) {
        HashMap<String, String> cokieList = new HashMap<String, String>();

        String headerName = null;
        for (int i = 1; (headerName = con.getHeaderFieldKey(i)) != null; i++)
            if (headerName.equals(SET_COOKIE)) {
                String cokie = con.getHeaderField(i);
                //logger.debug( cokie);
                String cokieListh[] = cokie.split("; ");
                for (String cokieOne : cokieListh) {
                    String sp[] = cokieOne.split("=");
                    cokie = "";
                    for (int j = 1; j < sp.length; j++)
                        cokie += sp[j];
                    if (!sp[0].contains("path"))
                        cokieList.put(sp[0], cokie);
                }
            }
        return cokieList;
    }


    public String printCokkieList(Map<String, String> cokieList) {
        StringBuffer strBuff = new StringBuffer();

        Set<String> cokieKeys = cokieList.keySet();
        for (String key : cokieKeys) {
            strBuff.append(cokieKeys);
            strBuff.append(" : ");
            strBuff.append(cokieList.get(key));
            strBuff.append("    ");
            //strBuff.append("\n");
        }
        return strBuff.toString();
    }

    public void setReplaceNewCokkie(Map<String, String> newCokieList) {
        for (String key : newCokieList.keySet()) {
            if (cookieList.get(key) != null)
                logger.info("replace : " + key +
                        " old : " + cookieList.get(key) +
                        " new : " + newCokieList.get(key));
            cookieList.put(key, newCokieList.get(key));
        }

    }


}
