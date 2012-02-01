/*
 * @(#)ProxyItme.java
 *
 * Copyright Swiss Reinsurance Company, Mythenquai 50/60, CH 8022 Zurich. All rights reserved.
 */
package com.bla.laa.Net;

public class ProxyItem {
    private String proxyHost = "";
    private Integer proxyPort = 0;
    public final static int TIMEOUT_MAX_COUNT = 3;
    private int proxyErrorCount = 0;

    public ProxyItem(String proxyHost, String proxyPort) {
        this.proxyHost = proxyHost;
        this.proxyPort = Integer.valueOf(proxyPort);
    }

    public Integer getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(Integer proxyPort) {
        this.proxyPort = proxyPort;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public void setProxyError() {
        this.proxyErrorCount += 1;
    }

    public int getProxyErrorCount() {
        return proxyErrorCount;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("{").append(proxyHost);
        sb.append(" : ").append(proxyPort);
        sb.append('}');
        return sb.toString();
    }
}

