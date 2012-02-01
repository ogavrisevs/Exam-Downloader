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

public class HtmlPrinter extends Wrapper {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Wrapper.class);
    public static void main(String argv[]) {
        new HtmlPrinter("C:/Documents and Settings/gavrishevs/Desktop/tst/short.html");
    }

    public HtmlPrinter(String fileName) {
        super(fileName);
        this.printStruc();
    }

    public HtmlPrinter(HTMLDocument htmlDoc) {
        super(htmlDoc);
        this.printStruc();
    }

    public void printStruc() {
        Element elementRoot[] = this.getDoc().getRootElements();
        for (Element elem : elementRoot) {
            logger.debug("----------------------------");
            printChildElem(elem, 1);
        }
    }

    /**
     * Simple printing
     */
    public void printChildElem(Element element, int ident) {
        for (Element childElement : getChield(element)) {
            System.out.print(getSpaces(ident) + printAtributesF(childElement.getAttributes(), ident));
            logger.debug(" ");
            printChildElem(childElement, (2 + ident));
        }
    }

    private String printAtributesF(AttributeSet as, int ident) {
        List<String> tags = new ArrayList<String>();
        List<java.util.Properties> atributes = new ArrayList<java.util.Properties>();

        String msg = "";
        Enumeration e = as.getAttributeNames();
        while (e.hasMoreElements()) {
            Object keyObj = e.nextElement();
            if (keyObj != null) {
                Object valObj = as.getAttribute(keyObj);

                String tmpTag = getTag(keyObj, valObj);
                if (tmpTag.length() != 0) {
                    tags.add(tmpTag);
                    continue;
                }

                Properties propAtr = getAtrProp(keyObj, valObj);
                if (!propAtr.isEmpty()) {
                    atributes.add(propAtr);
                    continue;
                }


                String keyStr = castObjToTypeVal(keyObj);
                msg += "[ k:" + keyStr;
                if (valObj != null) {
                    String valStr = castObjToTypeVal(valObj);
                    msg += " , v:" + valStr;
                }
                msg += " ]";
            }
        }

        String atrStr = "";
        if (atributes.size() != 0)
            for (java.util.Properties atribute : atributes)
                atrStr += atribute.toString();

        atrStr = atrStr.replace("{", " ");
        atrStr = atrStr.replace("}", " ");

        String tagStr = "";
        if (tags.size() != 0)
            for (String tag : tags)
                tagStr += tag + " ";

        msg = "<" + tagStr + " " + atrStr + ">" + msg;

        return msg;
    }


    public List<Element> getChield(Element rootElement) {
        List<Element> child = new ArrayList<Element>();
        int count = rootElement.getElementCount();
        for (int i = 0; i < count; i++)
            child.add(rootElement.getElement(i));

        return child;
    }

    public String getSpaces(int count) {
        String str = "";
        for (int index = 0; index < count; index++)
            str += " ";
        return str;
    }

    private static String printAtributes(AttributeSet as, int ident) {
        String msg = "";
        Enumeration e = as.getAttributeNames();
        while (e.hasMoreElements()) {
            Object keyObj = e.nextElement();
            if (keyObj != null) {
                Object valObj = as.getAttribute(keyObj);

                String keyStr = castObjToTypeVal(keyObj);
                msg += "[ k:" + keyStr;
                if (valObj != null) {
                    String valStr = castObjToTypeVal(valObj);
                    msg += " , v:" + valStr;
                }
                msg += " ]";
            }
        }

        return msg;
    }

    public String getTag(Object keyObj, Object valObj) {
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

    public String getAtr(Object keyObj, Object valObj) {
        if (keyObj == null)
            return "";
        if (valObj == null)
            return "";

        if (keyObj instanceof HTML.Attribute)
            if (valObj instanceof String)
                return String.valueOf(keyObj) + "=" + String.valueOf(valObj);
        return "";
    }

    public Properties getAtrProp(Object keyObj, Object valObj) {
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

    public boolean isAtr(Object keyObj, Object valObj) {
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


    public static String castObjToTypeVal(Object obj) {
        if (obj == null)
            return "";

        if (obj instanceof HTML.Tag)
            return ("(Tag)" + String.valueOf(obj));
        if (obj instanceof HTML.Attribute)
            return ("(Atr)" + String.valueOf(obj));
        else if (obj instanceof HTML.UnknownTag)
            return ("(Utag)" + String.valueOf(obj));
        else if (obj instanceof String)
            return ("(Str)" + String.valueOf(obj));
        else if (obj instanceof Boolean)
            return ("(Boo)" + String.valueOf(obj));
        else if (obj instanceof StyleConstants)
            return ("(SC)" + String.valueOf(obj));
        else if (obj instanceof SimpleAttributeSet)
            return ("[[(SimpAtr)" + printAtributes((AttributeSet) obj, 0) + "]]");
        else if (obj instanceof ToggleButtonModel)
            return ("(TBM)" + String.valueOf(obj));
        else if (obj instanceof Integer)
            return ("(Int)" + String.valueOf(obj));
        else {
            obj.getClass();
            return ("( )" + String.valueOf(obj));
        }
    }

    public String getElemntText(Element elemnt) {
        String tagText = "";
        try {
            int startOffset = elemnt.getStartOffset();
            int endOffset = elemnt.getEndOffset();
            int length = endOffset - startOffset;
            tagText = this.getDoc().getText(startOffset, length);
        } catch (Exception e) {
            logger.error("", e);
        }
        if (tagText.length() > 5)
            return (tagText.substring(0, 5));
        else
            return tagText;
    }

}

