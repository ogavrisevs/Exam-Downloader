package com.bla.laa.html.swing;

import com.bla.laa.Wrapper;
import org.slf4j.LoggerFactory;

import javax.swing.JToggleButton.ToggleButtonModel;
import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

public class HtmlReader extends Wrapper {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Wrapper.class);
    private final HtmlModel htmlCustDoc = new HtmlModel();
    private HTMLDocument htmlDoc = null;

    public static final String SPLITTER = ":";

    public static void main(String argv[]) {
        //new HtmlReader("C:/Documents and Settings/gavrishevs/Desktop/tst/short.html");
        new HtmlReader("C:/Documents and Settings/gavrishevs/Desktop/tst/aecdh.html");
        //new HtmlReader("C:/Documents and Settings/gavrishevs/Desktop/tst/small_title2.html");
    }

    public HtmlReader(String fileName) {
        super(fileName);

        this.htmlDoc = this.getDoc();

        this.readStruc();
        logger.debug(htmlCustDoc.toStringElementsWithId());
        logger.debug("-----------------------------------");
        logger.debug(htmlCustDoc.toStringElements());
        logger.debug("-----------------------------------");

        this.readText();
        logger.debug(htmlCustDoc.toStringTextList());
        logger.debug("-----------------------------------");

        logger.debug(htmlCustDoc.toStringElements());
        logger.debug("-----------------------------------");
    }

    public void readStruc() {
        Element elementRoot[] = this.getDoc().getRootElements();
        for (Element elem : elementRoot) {
            logger.debug("----------------------------");
            readHtmlElemts(elem, new HtmlId());
        }
    }

    public void readText() {
        for (Elem openElem : htmlCustDoc.getElements()) {
            HtmlId closeElemId = htmlCustDoc.getClosingTagId(openElem);
            if (closeElemId != null) {

                Integer startOffset = openElem.getStartOffset();
                Elem closeElem = htmlCustDoc.getElemById(closeElemId.toString());
                Integer endOffset = closeElem.getEndOffset();
                Integer lenghtOffset = endOffset - startOffset;

                if ((lenghtOffset < 1) || (startOffset < 1)) {
                    logger.debug("somsing wrong !");
                    continue;
                }

                String text = "";
                try {
                    text = htmlDoc.getText(startOffset, lenghtOffset);
                    text = text.replaceAll("\n", "");
                    text = text.trim();
                } catch (Exception e) {
                    logger.error("", e);

                }
                if (text.length() != 0)
                    htmlCustDoc.addElemText(new ElemText(text, openElem.getElemId(), closeElemId));
            }
        }
    }


    /**
     * Read from model
     */
    private void readHtmlElemts(Element element, HtmlId id) {
        HtmlId childId = id.genChildId();
        for (Element childElement : getChield(element)) {
            Elem elem = readElemnts(childElement.getAttributes());
            elem.setOffset(childElement.getEndOffset(), childElement.getStartOffset());
            htmlCustDoc.addElem(elem, childId);
            readHtmlElemts(childElement, childId);
            childId = childId.genNextId();
        }
    }

    private List<Element> getChield(Element rootElement) {
        List<Element> child = new ArrayList<Element>();
        int count = rootElement.getElementCount();
        for (int i = 0; i < count; i++)
            child.add(rootElement.getElement(i));

        return child;
    }

    private String getSpaces(int count) {
        String str = "";
        for (int index = 0; index < count; index++)
            str += " ";
        return str;
    }

    /**
     * SAve to class
     */
    private Elem readElemnts(AttributeSet as) {
        Elem elem = new Elem();

        String msg = "";
        Enumeration e = as.getAttributeNames();
        while (e.hasMoreElements()) {
            Object keyObj = e.nextElement();

            if (keyObj != null) {
                Object valObj = as.getAttribute(keyObj);

                String tmpTag = getTag(keyObj, valObj);
                if (tmpTag.length() != 0) {
                    elem.addTag(tmpTag);
                    continue;
                }

                Properties propAtr = getAtrProp(keyObj, valObj);
                if (!propAtr.isEmpty()) {
                    elem.addAtrib(keyObj, valObj);
                    continue;
                }

                elem.addAtribUnknow(keyObj, valObj);
            }
        }
        return elem;
    }

    private String getTag(Object keyObj, Object valObj) {
        if (keyObj == null)
            return "";
        if (valObj == null)
            return "";

        if (keyObj instanceof StyleConstants)
            if (valObj instanceof HTML.Tag)
                if (String.valueOf(keyObj).contentEquals("name"))
                    return String.valueOf(valObj);

        return "";
    }

    private String getAtr(Object keyObj, Object valObj) {
        if (keyObj == null)
            return "";
        if (valObj == null)
            return "";

        if (keyObj instanceof HTML.Attribute)
            if (valObj instanceof String)
                return String.valueOf(keyObj) + "=" + String.valueOf(valObj);
        return "";
    }

    private Properties getAtrProp(Object keyObj, Object valObj) {
        Properties prop = new Properties();
        if (keyObj == null)
            return prop;
        if (valObj == null)
            return prop;

        if (keyObj instanceof HTML.Attribute)
            if (valObj instanceof String)
                prop.put(String.valueOf(keyObj), String.valueOf(valObj));

        return prop;
    }

    private boolean isAtr(Object keyObj, Object valObj) {
        Properties prop = new Properties();
        if (keyObj == null)
            return false;
        if (valObj == null)
            return false;

        if (keyObj instanceof HTML.Attribute)
            if (valObj instanceof String)
                return true;

        return false;
    }

    public static String castObjToType(Object obj) {
        if (obj == null)
            return "";

        if (obj instanceof HTML.Tag)
            return ("Tag");
        if (obj instanceof HTML.Attribute)
            return ("Atr");
        else if (obj instanceof HTML.UnknownTag)
            return ("Utag");
        else if (obj instanceof String)
            return ("Str");
        else if (obj instanceof Boolean)
            return ("Boo");
        else if (obj instanceof StyleConstants)
            return ("SC");
        else if (obj instanceof SimpleAttributeSet)
            //return ("[[(SimpAtr)"+ printAtributes((AttributeSet) obj, 0) + "]]");
            return ("(SimpAtr)");
        else if (obj instanceof ToggleButtonModel)
            return ("TBM");
        else if (obj instanceof Integer)
            return ("Int");
        else if (obj.getClass().getName().contains("javax.swing.text.html.CSS"))
            return ("Css");
        else {
            obj.getClass().getName();
            return ("Unknow!");
        }
    }

}

