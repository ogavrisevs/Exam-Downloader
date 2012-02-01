package com.bla.laa.Net;

import com.bla.laa.Common.MyCustException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ProxyGetter {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ProxyGetter.class);
    private Neti neti = null;
    private static List<ProxyItem> proxyList = new ArrayList<ProxyItem>();
    private static ProxyItem currentProxy = null; // null = not set

    public ProxyGetter() {
        logger.info("constructor ProxyGetter()");
    }

    public ProxyGetter(Neti netiPointer) {
        this.neti = netiPointer;
    }

    protected void getProxyList() {
        Boolean useDinamicProxy = neti.getUseDinamicProxy();
        neti.setUseDinamicProxy(Boolean.FALSE);
        Boolean useStaticProxy = neti.getUseStaticProxy();
        neti.setUseStaticProxy(Boolean.FALSE);

        Document doc = null;
        try {
            StringBuffer sb = neti.doGET(Neti.SITE_PROXY);
            if ((sb == null) || (sb.length() <= 1)) {
                logger.error("Unable to retrieve proxy list from web site !!!");
                throw new MyCustException();
            }
            doc = Jsoup.parse(sb.toString(), "UTF-8");

            if (doc == null) {
                logger.error("Unable to parse doc(html) from proxy web page !!!");
                throw new MyCustException();
            }
        } catch (Exception e) {
            logger.error("", e);
        }
        proxyList.clear();
        readRecursy(doc.getElementsByTag("html").get(0));
        logger.info("getProxyList retrive : " + proxyList.size());

        neti.setUseDinamicProxy(useDinamicProxy);
        neti.setUseStaticProxy(useStaticProxy);
    }

    public synchronized ProxyItem getProxy() throws MyCustException {

        if (proxyList.isEmpty())
            getProxyList();

        if (this.currentProxy == null)
            setNewProxyFromList();

        if (this.currentProxy != null)
            return this.currentProxy;
        else
            throw new RuntimeException("no proxy found !");
    }

    protected void removeProxyFromList() {
        logger.info("rem proxy : " + this.currentProxy);
        if ((this.currentProxy != null) && (!this.proxyList.isEmpty())) {
            this.proxyList.remove(this.currentProxy);
            this.currentProxy = null;
        }
    }

    protected void setNewProxyFromList() throws MyCustException {
        if ((currentProxy == null)) {
            this.currentProxy = proxyList.get(0);
            logger.info("set new proxy : " + this.currentProxy);
            if (neti.isProxInBlackList(this.currentProxy)) {
                logger.warn("proxy in blackList !!! " + this.currentProxy);
                removeProxyFromList();
                getProxy();
            }
            if (!neti.testProxy(this.currentProxy)) {
                logger.warn("proxy test failed! : " + this.currentProxy);
                removeProxyFromList();
                getProxy();
            }
        }
    }

    protected void setProxyError() throws MyCustException {
        this.currentProxy.setProxyError();
        logger.warn("set proxy Error : " + this.currentProxy.getProxyErrorCount());
        if (this.currentProxy.getProxyErrorCount() >= ProxyItem.TIMEOUT_MAX_COUNT) {
            removeProxyFromList();
            getProxy();
        }
    }

    protected void readRecursy(Element element) {
        for (Element elem : element.children()) {
            for (Element innerElement0 : elem.getElementsByTag("tr")) {
                for (Element innerElement01 : innerElement0.getElementsByAttributeValueMatching("class", "row")) {
                    String proxyHost = "";
                    String proxyPort = "";
                    for (Element innerElement02 : innerElement01.getElementsByTag("td")) {
                        for (Element innerElement03 : innerElement02.getElementsByTag("a")) {
                            for (Element innerElement04 : innerElement03.getElementsByAttributeValueMatching("title", "View this Proxy details"))
                                proxyHost = innerElement04.text().trim();
                            for (Element innerElement04 : innerElement03.getElementsByAttributeValueMatching("title", "Select proxies with port number"))
                                proxyPort = innerElement04.text().trim();
                        }
                    }
                    if ((!proxyHost.isEmpty()) && (!proxyPort.isEmpty())) {
                        ProxyItem proxyItem = new ProxyItem(proxyHost, proxyPort);
                        proxyList.add(proxyItem);
                        logger.debug(proxyItem.toString());
                    }
                }
            }
            if (!elem.tag().isEmpty())
                readRecursy(elem);
        }
    }
}
