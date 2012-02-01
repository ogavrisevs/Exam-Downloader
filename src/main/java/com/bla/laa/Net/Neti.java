package com.bla.laa.Net;

import com.bla.laa.Common.CommonS;
import com.bla.laa.Common.MyCustException;
import com.bla.laa.Common.ProxyTimeoutException;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Neti {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Neti.class);
    private static final String CHARSET = "UTF-8";
    public static final String SITE_PROXY = "http://www.xroxy.com/proxylist.php?port=&type=Distorting&ssl=&country=&latency=&reliability=#table";
    public static final String SITE_CSDD = "http://csnt.csdd.lv";
    public static final String SITE_TESTER = "http://www.draugiem.lv/robots.txt";
    private static final String TMPDIR = "tmp\\";

    private static final Integer CONNECTION_TIME_OUT = 5000; /*5 sec*/
    private static final Integer READ_TIME_OUT = 15000; /*15 sec*/
    private static final Integer TIME_OUT_RETRAYS = 3;

    private static boolean useDinamicProxy = false;
    private static boolean useStaticProxy = false;
    private static String staticProxyHost = "";
    private static Integer staticProxyPort = 0;
    private static List<String> proxyBlackList = new ArrayList<String>();

    public enum HttpMethods {GET, POST, GETpic}

    ;

    private Cookies cookies = null;
    private static ProxyGetter proxys = null;

    private String currentAgent = UserAgents.getAggent();

    static {
        System.setProperty("http.keepAlive", "false");
    }

    public Neti() {
        logger.info("constructor Neti()");
        this.cookies = new Cookies();
        CommonS.loadProxyProps();
        this.proxys = new ProxyGetter(this);
    }

    /**
     * chek temp dir exists, if not create
     */
    public static String getTempDir() {
        File tDir = new File(Neti.TMPDIR);
        if (!tDir.isDirectory())
            tDir.mkdir();
        return TMPDIR;
    }

    public StringBuffer doPOST(String postData) throws MyCustException, ProxyTimeoutException {
        if (postData == null)
            CommonS.stopThread(" postData == null ");
        if (postData.length() == 0)
            CommonS.stopThread("postData.length() == 0");

        CommonS.sleepThread();
        logger.info("try Post : " + postData);

        URLConnection con = this.getConnection(SITE_CSDD);
        con.setDoOutput(true);
        cookies.addCookieToCon(con);

        con.setRequestProperty("Accept-Charset", CHARSET);
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + CHARSET);
        con.setRequestProperty("Accept", "text/javascript, text/html, application/xml, text/xml, */*");

        OutputStream output = null;
        try {
            output = con.getOutputStream();
            output.write(postData.getBytes(CHARSET));
        } catch (Exception e) {
            processNetworkError(e);
        } finally {
            if (output != null)
                try {
                    output.close();
                } catch (IOException ioe) {
                    processNetworkError(ioe);
                }
        }
        StringBuffer sb = new StringBuffer();
        try {
            InputStream response = con.getInputStream(); // actual call
            chkConnecionStatus(con);
            sb = saveStreamToList(response);
        } catch (Exception e) {
            processNetworkError(e);
        }

        cookies.chekForNewCookies(con);
        return sb;
    }

    /**
     * @param data for GET = site, for POST = post data
     */
    public Object doGETorPOST(HttpMethods httpMethod, String data) throws MyCustException {
        int retrayCount = 0;
        while (true) {
            try {
                if (httpMethod == HttpMethods.GET) {
                    return doGET(data);
                } else if (httpMethod == HttpMethods.POST) {
                    return doPOST(data);
                } else if (httpMethod == HttpMethods.GETpic) {
                    return doGetPic(data);
                } else {
                    logger.error("unknown Method !!!");
                    throw new MyCustException();
                }

            } catch (ProxyTimeoutException pte) {
                if ((retrayCount++) > Neti.TIME_OUT_RETRAYS) {
                    logger.warn("too much retrays");
                    throw new MyCustException();
                }
                logger.debug("set retrayCount = " + retrayCount);
            }
        }
    }

    public StringBuffer doGET(String site) throws MyCustException, ProxyTimeoutException {
        logger.info("try Get : " + site);
        URLConnection con = this.getConnection(site);
        CommonS.sleepThread();

        BufferedReader in = null;
        StringBuffer sb = new StringBuffer();
        try {
            con.setDoInput(true);                                  /* actual connection time !!!*/
            in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            chkConnecionStatus(con);
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                sb.append(inputLine);
            cookies.chekForNewCookies(con);
        } catch (Exception ex) {
            processNetworkError(ex);
        } finally {
            if (in != null) {
                try {
                    in.close(); // free network resources (URLConnection)
                } catch (IOException e) {
                    logger.error("", e);
                }
            }
        }
        return sb;
    }

    public BufferedImage doGetPic(String url) throws MyCustException, ProxyTimeoutException {
        logger.info("try Get pic : " + url);
        CommonS.sleepThread();
        BufferedImage img = null;
        URLConnection con = this.getConnection(url);
        InputStream is = null;
        cookies.addCookieToCon(con);
        try {
            con.setDoInput(true);
            is = con.getInputStream();
            img = ImageIO.read(is);
            chkConnecionStatus(con);
            cookies.chekForNewCookies(con);
        } catch (Exception ex) {
            processNetworkError(ex);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                logger.error("", e);
            }
        }
        return img;
    }

    public void chkConnecionStatus(URLConnection con) {
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) con;
            int resCode = httpURLConnection.getResponseCode();
            if (resCode != HttpURLConnection.HTTP_OK)
                logger.warn("http con respones : " + resCode + " ( " + httpURLConnection.getResponseMessage() + " ) ");
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    /**
     * Set system prox, for all connections
     */
    private void setSysProxy(String proxyHostLoc, String proxyPortLoc) {
        Properties properties = System.getProperties();
        properties.put("http.proxyHost", proxyHostLoc);
        properties.put("http.proxyPort", proxyPortLoc);
    }

    private Proxy crtConnProxy() throws MyCustException {
        Proxy proxy = null;
        if ((useDinamicProxy) || (useStaticProxy)) {
            SocketAddress proxyAddr = null;
            if (useDinamicProxy) {
                ProxyItem proxyL = this.proxys.getProxy();
                proxyAddr = new InetSocketAddress(proxyL.getProxyHost(), proxyL.getProxyPort());
            } else if (useStaticProxy)
                proxyAddr = new InetSocketAddress(this.staticProxyHost, staticProxyPort);
            proxy = new Proxy(Proxy.Type.HTTP, proxyAddr);
        } else
            proxy = new Proxy(Proxy.Type.DIRECT, null);

        return proxy;
    }

    public URLConnection getConnection(String urlS) throws MyCustException {
        URLConnection urlCon = null;
        try {
            URL url = new URL(urlS);
            if ((useDinamicProxy) || (useStaticProxy))
                urlCon = url.openConnection(crtConnProxy());
            else
                urlCon = url.openConnection();
            urlCon.getConnectTimeout();
            urlCon.setConnectTimeout(CONNECTION_TIME_OUT);
            urlCon.setReadTimeout(READ_TIME_OUT);
            urlCon.setRequestProperty("User-Agent", this.currentAgent);
        } catch (MalformedURLException mue) {
            logger.error("", mue);
        } catch (IOException ioe) {
            logger.error("", ioe);
        }
        return urlCon;
    }

    public StringBuffer saveStreamToList(InputStream is) throws Exception {
        StringBuffer buff = new StringBuffer();
        BufferedReader in = new BufferedReader(new InputStreamReader(is, CHARSET));
        String inputLine;
        while ((inputLine = in.readLine()) != null)
            buff.append(inputLine);
        in.close();

        return buff;
    }


    String getHeadField(URLConnection con, String fieldKey) {
        String headerName = null;
        for (int i = 1; (headerName = con.getHeaderFieldKey(i)) != null; i++)
            if (headerName.equals(fieldKey))
                return con.getHeaderField(i);
        return null;
    }

    void printAllHeadFields(URLConnection con) {
        String headerName = null;
        for (int i = 1; (headerName = con.getHeaderFieldKey(i)) != null; i++)
            logger.debug(headerName + " = " + con.getHeaderField(i));
    }

    /**
     * @return - true - proxys ok
     */
    public Boolean testProxy(ProxyItem proxyItme) throws MyCustException {
        SocketAddress proxyAddr = new InetSocketAddress(proxyItme.getProxyHost(), proxyItme.getProxyPort());
        try {
            Proxy proxy = new Proxy(Proxy.Type.HTTP, proxyAddr);
            URL url = new URL(Neti.SITE_TESTER);
            URLConnection urlCon = url.openConnection(proxy);
            urlCon.setRequestProperty("User-Agent", this.currentAgent);

            String sb = "";
            urlCon.setDoInput(true);
            BufferedReader in = new BufferedReader(new InputStreamReader(urlCon.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                sb += inputLine;
            in.close();

            if (sb.contentEquals("User-agent: *Disallow: /user/*"))
                return true;
            else
                return false;
        } catch (Exception ex) {
            try {
                processNetworkError(ex);
            } catch (ProxyTimeoutException e) {
                // dont care here :TODO think about this code
            }
            return false;
        }
    }

    /**
     * if dynamic proxy set proxy error and continiue
     * else throw custum exception for thread stop
     */
    public void processNetworkError(Exception ex) throws MyCustException, ProxyTimeoutException {
        logger.error(ex.getMessage());
        logger.debug("", ex);

        if (((ex instanceof SocketTimeoutException) || (ex instanceof ConnectException))
                && (useDinamicProxy)) {
            this.proxys.setProxyError();
            throw new ProxyTimeoutException();
        }


        if ((ex instanceof SocketTimeoutException) ||
                (ex instanceof UnknownHostException) ||
                (ex instanceof NoRouteToHostException) ||
                (ex instanceof MalformedURLException) ||
                (ex instanceof IOException)) {
            if (useDinamicProxy)
                this.proxys.setProxyError();
            else
                throw new MyCustException(ex.getMessage()); // terminates current thread
        } else {
            throw new MyCustException(ex.getMessage());
        }
    }

    public Boolean isProxInBlackList(ProxyItem newProxy) {
        for (String proxy : proxyBlackList)
            if (proxy.contentEquals(newProxy.getProxyHost()))
                return true;
        return false;
    }

    public static boolean getUseDinamicProxy() {
        return useDinamicProxy;
    }

    public static void setUseDinamicProxy(boolean useDinamicProxy) {
        Neti.useDinamicProxy = useDinamicProxy;
        logger.info("useDinamicProxy : " + Neti.getUseDinamicProxy());
    }

    public static boolean getUseStaticProxy() {
        return useStaticProxy;
    }

    public static void setUseStaticProxy(boolean useStaticProxy) {
        Neti.useStaticProxy = useStaticProxy;
        logger.info("useStaticProxy : " + Neti.getUseStaticProxy());
    }

    public static String getStaticProxyHost() {
        return staticProxyHost;
    }

    public static void setStaticProxyHost(String staticProxyHost) {
        Neti.staticProxyHost = staticProxyHost;
    }

    public static Integer getStaticProxyPort() {
        return staticProxyPort;
    }

    public static void setStaticProxyPort(Integer staticProxyPort) {
        Neti.staticProxyPort = staticProxyPort;
    }

    public static List<String> getProxyBlackList() {
        return proxyBlackList;
    }

    public static void addProxyBlackList(String proxyIP) {
        if (!Neti.proxyBlackList.contains(proxyIP))
            Neti.proxyBlackList.add(proxyIP);
    }
}
