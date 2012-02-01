package com.bla.laa.Net;

import com.bla.laa.Common.CommonS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserAgents {

    protected static final List<String> userAgentList = new ArrayList<String>(
            Arrays.asList(
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_1) AppleWebKit/535." + CommonS.getRandomStr(10) + " (KHTML, like Gecko) Chrome/13." + CommonS.getRandomStr(10) + ".782.218 Safari/535.1",
                    "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; .NET CLR 2.0.50727; .NET CLR 3." + CommonS.getRandomStr(10) + ".04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3." + CommonS.getRandomStr(10) + ".30729)",
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.6; rv:6.0.1) Gecko/20100101 Firefox/6." + CommonS.getRandomStr(10) + ".1",
                    "Opera/9.00 (Windows NT 5.1; U; en) v" + CommonS.getRandomStr(10) + "." + CommonS.getRandomStr(10) + "",
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_6_" + CommonS.getRandomStr(10) + ") AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0." + CommonS.getRandomStr(100) + ".218 Safari/535.1",
                    "Opera/9.80 (Windows NT 5.2; U; ru) Presto/2." + CommonS.getRandomStr(10) + ".168 Version/11.50",
                    "Mozilla/5.0 (X11; U; Linux i686; nl-NL; rv:1.9.1b4) Gecko/20090423 Firefox/3.5b4",
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10." + CommonS.getRandomStr(10) + "; rv:6.0.1) Gecko/20100101 Firefox/6.0.1",
                    "Mozilla/5.0 (iPad; U; CPU OS 4_" + CommonS.getRandomStr(10) + "_4 like Mac OS X; en-us) AppleWebKit/533.17.9 (KHTML, like Gecko) Version/5.0.2 Mobile/8K2 Safari/6533.18.5",
                    "Mozilla/5.0 (Linux; U; Android 2.3.3; en-gb; GT-I9100 Build/GINGERBREAD) AppleWebKit/533.1 (KHTML, like Gecko) Version/4." + CommonS.getRandomStr(10) + " Mobile Safari/533.1",
                    "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:6.0.1) Gecko/20100101 Firefox/6.0." + CommonS.getRandomStr(10) + "",
                    "Mozilla/5.0 (X11; U; Linux i686; nl-NL; rv:1.9.1b" + CommonS.getRandomStr(10) + ") Gecko/20090423 Firefox/3.5b4",
                    "Mozilla/5.0 (iPad; U; CPU OS 4_3_" + CommonS.getRandomStr(10) + " like Mac OS X; en-us) AppleWebKit/533.17.9 (KHTML, like Gecko) Version/5.0.2 Mobile/8K2 Safari/6533.18.5Mozilla/5.0 (X11; Linux armv7l) AppleWebKit/534.36 (KHTML, like Gecko) Chrome/13.0.782.15 Safari/534.36",
                    "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.2.21) Gecko/20110830 Firefox/3.6." + CommonS.getRandomStr(10)));

    public static String getAggent() {
        return UserAgents.userAgentList.get(CommonS.getRandomInt(userAgentList.size()));
    }
}
