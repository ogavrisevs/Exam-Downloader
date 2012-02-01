package com.bla.laa.html.jsoup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Expermental , html parsing using jsoup
 *
 * @author oskars
 */
public class HtmlRead {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(HtmlRead.class);
    public static void main(String argv[]) {
        new HtmlRead();
    }

    final List<List> allKandList = new ArrayList<List>();

    HtmlRead() {
        Document doc = null;
        try {
            File input = new File("C:/Documents and Settings/gavrishevs/Desktop/tst/aecdh.html");
            doc = Jsoup.parse(input, "UTF-8");
        } catch (Exception e) {
            logger.error("", e);
        }

        readRecursy(doc.getElementsByTag("html").get(0), 1);
        readCandidates(doc.getElementsByTag("html").get(0));
        printAllKandList();
    }


    public void printAllKandList() {
        List<List> uniqLlist = getUniqList(this.allKandList);
        for (List<String> list : uniqLlist) {
            String line = "";
            for (String str : list)
                line += str + " = ";
            logger.debug(line);
        }
    }

    public List<List> getUniqList(List<List> allList) {
        List<List> uniqList = new ArrayList<List>();
        for (List<String> allListItme : allList)
            if (!isInList(uniqList, allListItme))
                uniqList.add(allListItme);
        return uniqList;
    }

    /**
     * @return true - alredy contains
     */
    public boolean isInList(List<List> list, List<String> kandList) {
        boolean rez = false;
        for (List<String> listItme : list) {
            if (listItme.containsAll(kandList)) {
                rez = true;
                break;
            }
        }
        return rez;
    }

    public void readCandidates(Element element) {
        for (Element elem : element.children()) {
            //if (elem.tag().getName().contentEquals("td")){
            //	allKandList.add(getKandid(elem.parent(), "td"));
            //}
            if (elem.tag().getName().contentEquals("span")) {
                allKandList.add(getKandid(elem.parent(), "span"));
            }
            readCandidates(elem);
        }
    }

    public List<String> getKandid(Element element, String plitter) {
        List<String> kandList = new ArrayList<String>();
        for (Element childElem : element.children()) {
            if (childElem.tag().getName().contentEquals(plitter)) {
                String txt = extractText(childElem);
                if (txt.length() != 0)
                    kandList.add(txt);
            }
        }
        return kandList;
    }

    public String extractText(Element element) {
        String txt = element.text().trim();
        txt = txt.replace("\n", "");
        txt = txt.replace("\t", "");
        txt = txt.replace(":", "");
        txt = txt.trim();
        //if (txt.length() > 20 )
        //	return txt.substring(0, 20);
        //else
        return txt;
    }


    public void readRecursy(Element element, int pos) {
        for (Element elem : element.children()) {
            logger.debug(getSpaces(pos) + "<" + elem.tag() + " " + elem.attributes().html() + ">");
            /*
                 logger.debug( getSpaces(pos) +
                       " isBlock:"+elem.tag().isBlock() +
                       " isEmpty:"+elem.tag().isEmpty() +
                       " isInline:"+elem.tag().isInline() +
                       " isKnownTag:"+elem.tag().isKnownTag() +
                       " isData:"+elem.tag().isData() +
                       " isSelfClosing:"+elem.tag().isSelfClosing() );
               */
            if ((elem.hasText()) && (elem.ownText().length() != 0)) {
                String msg = "";
                if (elem.ownText().length() > 20)
                    msg = elem.ownText().substring(0, 20);
                else
                    msg = elem.ownText();

                msg = msg.replace("\n", "");
                msg = msg.trim();
                if (msg.length() != 0)
                    logger.debug(getSpaces(pos + 1) + msg);

                elem.nodeName();
                List<Node> nn = elem.childNodes();
            }

            if (!elem.tag().isEmpty())
                readRecursy(elem, pos + 1);

            if ((elem.tag().isBlock() && (elem.tag().isSelfClosing()))
                    || (elem.tag().getName().contentEquals("span")))
                logger.debug(getSpaces(pos) + "</" + elem.tag() + ">");

        }
    }

    public String getSpaces(int count) {
        String str = "";
        for (int index = 0; index < count; index++)
            str += "  ";
        return str;
    }


}
