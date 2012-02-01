package com.bla.laa;

import com.bla.laa.Common.CommonS;
import com.bla.laa.Common.MyCustException;
import com.bla.laa.Container.Answer;
import com.bla.laa.Container.Question;
import com.bla.laa.Container.TCaseCont;
import org.slf4j.LoggerFactory;

import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.ElementIterator;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Wrapper {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Wrapper.class);
    private final Map<String, String> srcIdQuest = new HashMap<String, String>() {{
        put("id", "question");
    }};
    private final Map<String, String> srcClassQuest = new HashMap<String, String>() {{
        put("class", "question");
    }};
    private final Map<String, String> srcClassAnswer = new HashMap<String, String>() {{
        put("class", "answer");
    }};
    private final Map<String, String> srcIdNavi = new HashMap<String, String>() {{
        put("id", "navi");
    }};
    private final Map<String, String> srcNameAnswer = new HashMap<String, String>() {{
        put("name", "answer");
    }};
    private Map<String, String> srcClassUnAns = new HashMap<String, String>() {{
        put("class", "answered bar30 hand");
        put("class", "answered bar30 hand");
    }};
    private final Map<String, String> srcIdChange1 = new HashMap<String, String>() {{
        put("id", "change1");
    }};
    private final Map<String, String> srcIdChange2 = new HashMap<String, String>() {{
        put("id", "change2");
    }};
    private final Map<String, String> srcIdAnswers = new HashMap<String, String>() {{
        put("id", "answers");
    }};
    private final Map<String, String> srcAnswerRight = new HashMap<String, String>() {{
        put("class", "answer right");
    }};
    private Map<String, String> srcIdPisturet = new HashMap<String, String>() {{
        put("id", "picture");
    }};


    private final List<String> questionReadAtrib = new ArrayList<String>(
            Arrays.asList("value", "title", "id", "href", "a"));
    private final List<String> allAnswers = new ArrayList<String>(
            Arrays.asList("id", "class"));

    private final String answTitle = "Jūsu atbilde ir pareiza";

    private HTMLDocument htmlDoc;
    private List<String> buff;

    public HTMLDocument getDoc() {
        return this.htmlDoc;
    }

    public Wrapper(List<String> buff) {
        initHtmlFromStringList(buff);
        this.buff = buff;
    }

    public Wrapper(StringBuffer buff) {
        List <String> stringLits = new ArrayList<String>();
        stringLits.add(buff.toString());
        initHtmlFromStringList(stringLits);
        this.buff = stringLits;
    }

    public Wrapper(String fileName) {
        initHtmlFromFile(fileName);
    }

    public Wrapper(HTMLDocument htmlDoc2) {
        this.htmlDoc = htmlDoc2;

    }

    /**
     * @return List<Answer> - order is important!
     * @throws MyCustException
     */
    public List<Answer> getAnswers() throws MyCustException {
        List<Answer> answers = new ArrayList<Answer>();

        try {
            ElementIterator it = new ElementIterator(this.htmlDoc);
            Element element;
            while ((element = it.next()) != null) {
                // <div class="answer"
                List<Element> elementAnswers = getChildElementsByTagName(
                        element, Tag.DIV, srcClassAnswer);
                if (elementAnswers.size() != 0)
                    for (Element elementAnswer : elementAnswers) {
                        // <input id="answer55667" type="radio" value="55667"
                        // name="answer">
                        Element elementAnswerInput = getChildElementByTagName(
                                elementAnswer, Tag.IMPLIED, null /* searchQusetion */);
                        if (elementAnswerInput != null) {
                            Element elementAnswerInput2 = getChildElementByTagName(
                                    elementAnswerInput, Tag.INPUT, srcNameAnswer);
                            if (elementAnswerInput2 != null) {
                                Map<String, String> foundAtrib = getAtributes(elementAnswerInput2,
                                        questionReadAtrib);
                                String csddIdText = foundAtrib.get("value");
                                Integer csddId = CommonS.strToInteger(csddIdText);
                                String questionText = getElementText(elementAnswer);
                                answers.add(new Answer(csddId, questionText));
                            }
                        }
                    }
            }
        } catch (Exception e) {
            logger.error("",e);
            CommonS.stopThread(e);
        }
        if (answers.size() == 0) {
            logger.info("Save page: " + CommonS.saveStringList(buff));
            CommonS.stopThread("Answers not found ! ");
        }
        return answers;
    }

    public Question getQuestion() throws Exception {
        Question question = new Question();
        try {
            ElementIterator it = new ElementIterator(this.htmlDoc);
            Element element;
            while ((element = it.next()) != null) {

                // <div id="question">
                Element elementQuestion = getChildElementByTagName(element, Tag.DIV, srcIdQuest);
                if (elementQuestion != null) {
                    // <div class="question"> text . . .
                    Element elementQuestionText = getChildElementByTagName(elementQuestion, Tag.DIV, srcClassQuest);
                    if (elementQuestionText != null)
                        question.setQuestionText(getElementText(elementQuestionText));
                    // <input id="question" type="hidden" value="32384"
                    // name="question">.
                    Element elementQuestionInput = getChildElementByTagName(
                            elementQuestion, Tag.IMPLIED, null /* searchQusetion */);
                    if (elementQuestionInput != null) {
                        Element elementQuestionInput2 = getChildElementByTagName(
                                elementQuestionInput, Tag.INPUT, srcIdQuest);
                        if (elementQuestionInput2 != null) {
                            Map<String, String> foundAtrib = this.getAtributes(
                                    elementQuestionInput2, questionReadAtrib);
                            String str = foundAtrib.get("value");
                            Integer sk = CommonS.strToInteger(str);
                            question.setQuestionCsddId(sk);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("",e);
            CommonS.stopThread(e);
        }
        if (!question.isQuestionOk()) {
            logger.error("question not found ! ");
            logger.error("Save page: " + CommonS.saveStringList(buff));
            throw new MyCustException("Ending thread");
            //CommonS.stopThread();
        }

        return question;
    }

    public Map<Integer, Integer> getAnswerIds() throws MyCustException {
        Map<Integer, Integer> answers = new HashMap<Integer, Integer>();
        try {
            ElementIterator it = new ElementIterator(this.htmlDoc);
            Element element;
            while ((element = it.next()) != null) {
                // <div id="navi">
                Element elementIdNav = getChildElementByTagName(element, Tag.DIV, srcIdNavi);
                if (elementIdNav != null) {
                    // <div id ='change1'>
                    Element elementIdChange1 = getChildElementByTagName(elementIdNav, Tag.DIV, srcIdChange1);
                    if (elementIdChange1 != null) {
                        // <div id ='change2'>
                        Element elementIdChange2 = getChildElementByTagName(elementIdChange1, Tag.DIV, srcIdChange2);
                        if (elementIdChange2 != null) {
                            // <div id ='question_xxxxx'>
                            List<Element> elementIdQuestions = getChildElementsByTagName(elementIdChange2, Tag.DIV, null);
                            if (elementIdQuestions.size() != 0) {
                                for (int index = 1; index < elementIdQuestions.size(); index++) {
                                    Element elemntid = elementIdQuestions.get(index);
                                    Map<String, String> rez = getAtributes(elemntid, allAnswers);
                                    String str = clearQuestion(rez.get("id"));
                                    Integer sk = CommonS.strToInteger(str);
                                    answers.put(index, sk);
                                }
                            }
                        }
                    }
                }
            } // end while
        } catch (Exception e) {
            logger.error("Save page: " + CommonS.saveStringList(buff));
            CommonS.stopThread(e);
        }

        if ((answers.size() == 0) || (answers.size() != TCaseCont.QUESTION_COUNT)) {
            CommonS.stopThread(" short answerList not corect ! ");
        }

        return answers;
    }


    public Integer getCorrectAnswer() throws MyCustException {
        Integer csddIdInt = 0;
        try {
            ElementIterator it = new ElementIterator(this.htmlDoc);
            Element element;
            while ((element = it.next()) != null) {
                // <div id="answers">
                Element elementIdAnswers = getChildElementByTagName(element, Tag.DIV, srcIdAnswers);
                if (elementIdAnswers != null) {
                    //<div>
                    Element elementDiv = getChildElementByTagName(elementIdAnswers, Tag.DIV, null);
                    if (elementDiv != null) {
                        // <div class= 'answer right'>
                        Element elementAnswerRight = getChildElementByTagName(elementDiv, Tag.DIV, srcAnswerRight);
                        if (elementAnswerRight != null) {
                            Element elementClassAnswerInp = getChildElementByTagName(elementAnswerRight, Tag.IMPLIED, null);
                            if (elementClassAnswerInp != null) {
                                Element elementClassAnswerInp2 = getChildElementByTagName(elementClassAnswerInp, Tag.INPUT, srcNameAnswer);
                                if (elementClassAnswerInp2 != null) {
                                    Map<String, String> foundAtrib = this.getAtributes(elementClassAnswerInp2, questionReadAtrib);
                                    String csddId = foundAtrib.get("id");
                                    csddId = clearAnswer(csddId);
                                    csddIdInt = CommonS.strToInteger(csddId);
                                }
                            }
                        }
                    }
                }
            }

        } catch (Exception ex) {
            logger.error("Save page: " + CommonS.saveStringList(buff));
        }
        if (csddIdInt == 0) {
            logger.info("Save page: " + CommonS.saveStringList(buff));
            CommonS.stopThread("CorrectAnswer not found !");
        }

        return csddIdInt;
    }

    public Integer getImageNr() {
        Integer url = -1;
        try {
            ElementIterator it = new ElementIterator(this.htmlDoc);
            Element element;
            while ((element = it.next()) != null) {
                // <div id="picture">
                Element elementIdAnswers = getChildElementByTagName(element, Tag.IMG, null);
                if (elementIdAnswers != null) {
                    Map<String, String> foundAtrib = this.getAtributes(elementIdAnswers, questionReadAtrib);
                    String href = foundAtrib.get("a");
                    if (href != null) {
                        href = clearHref(href);
                        url = CommonS.strToInteger(href);
                        break;
                    }
                }
            }

        } catch (Exception ex) {
            logger.error("Save page: " + CommonS.saveStringList(buff));
        }

        //if (url < 1){
        //	HtmlPrinter hp = new HtmlPrinter(this.htmlDoc);
        //	CommonS.out("Save page: "+ CommonS.saveStringList(buff));
        //	CommonS.stopThread(" Picture Href not found ! ");
        //}

        return url;
    }

    /**
     * answer56944
     */
    private String clearAnswer(String answer) {
        answer = answer.replaceAll("answer", "");
        return answer.trim();
    }

    /**
     * href=http://csnt.csdd.lv/?bilde_l=2187&end=jpg id=largeImage
     * ?bilde_e=1&end=jpg
     */
    private String clearHref(String href) {
        if (href == null)
            return "0";
        if (href.length() == 0)
            return "0";

        int posStart = href.indexOf("bilde");
        posStart += "bilde".length();
        int posEnd = href.indexOf("&end");
        href = String.valueOf(href.subSequence(posStart, posEnd));

        Pattern p = Pattern.compile("[0-9]+");
        Matcher m = p.matcher(href);
        if (m.find())
            return m.group();

        return "0";
    }

    /**
     * question_2202470
     */
    private String clearQuestion(String question) {
        question = question.replaceAll("question_", "");
        return question.trim();
    }


    private void initHtmlFromStringList(List<String> buff) {
        String htmlText = "";
        for (String str : buff)
            htmlText += str;

        try {
            InputStream is = new ByteArrayInputStream(htmlText.getBytes("UTF-8"));
            HTMLEditorKit kit = new HTMLEditorKit();
            htmlDoc = (HTMLDocument) kit.createDefaultDocument();
            Reader HTMLReader = new InputStreamReader(is, "UTF-8");
            htmlDoc.putProperty("IgnoreCharsetDirective", new Boolean(true));
            kit.read(HTMLReader, htmlDoc, 0);
            //printProps();

        } catch (UnsupportedEncodingException uee) {
            logger.error("", uee);
        } catch (Exception e) {
            logger.error("",e);
        }
    }


    private void initHtmlFromFile(String fileName) {
        try {
            FileInputStream fileS = new FileInputStream(fileName);
            HTMLEditorKit kit = new HTMLEditorKit();

            htmlDoc = (HTMLDocument) kit.createDefaultDocument();
            Reader HTMLReader = new InputStreamReader(fileS, "UTF-8");
            htmlDoc.putProperty("IgnoreCharsetDirective", new Boolean(true));
            kit.read(HTMLReader, htmlDoc, 0);

        } catch (Exception e) {
            logger.error("",e);
        }
    }

    /**
     * @return - true got all atributes
     */

    private boolean isElementAtribOk(Element element,
                                     Map<String, String> searchAtrib) {
        List<String> found = new ArrayList<String>();
        // serach for all atrib from key list
        AttributeSet attributeSet = element.getAttributes();
        Enumeration e = attributeSet.getAttributeNames();
        while (e.hasMoreElements()) {
            Object keyObj = e.nextElement();
            if (keyObj != null)
                if (keyObj instanceof HTML.Attribute) {
                    Object valObj = attributeSet.getAttribute(keyObj);
                    if (valObj != null)
                        if (searchAtrib.get(String.valueOf(keyObj)) != null) {
                            String valAtr = searchAtrib.get(String
                                    .valueOf(keyObj));
                            if (valObj.equals(valAtr))
                                found.add(String.valueOf(keyObj));
                        }
                }
        }

        // chek if got all
        if (found.size() != 0)
            if (found.containsAll(searchAtrib.keySet()))
                return true;

        return false;
    }

    private Element getChildElementByTagName(Element element,
                                             HTML.Tag searchTag, Map<String, String> searchAtrib) {
        int count = element.getElementCount();
        for (int i = 0; i < count; i++) {
            Element childElem = element.getElement(i);
            AttributeSet attributes = childElem.getAttributes();
            Object obj = attributes.getAttribute(StyleConstants.NameAttribute);
            if ((obj instanceof HTML.Tag) && ((obj == searchTag)))

                if (searchAtrib != null) {
                    if (isElementAtribOk(childElem, searchAtrib))
                        return childElem;
                } else {
                    return childElem;
                }
        }
        return null;
    }

    private List<Element> getChildElementsByTagName(Element element,
                                                    HTML.Tag searchTag, Map<String, String> searchAtrib) {
        int count = element.getElementCount();
        List<Element> elemnts = new ArrayList<Element>();
        for (int i = 0; i < count; i++) {
            Element childElem = element.getElement(i);
            AttributeSet attributes = childElem.getAttributes();
            Object obj = attributes.getAttribute(StyleConstants.NameAttribute);
            if ((obj instanceof HTML.Tag) && ((obj == searchTag)))

                if (searchAtrib != null) {
                    if (isElementAtribOk(childElem, searchAtrib))
                        elemnts.add(childElem);
                } else {
                    elemnts.add(childElem);
                }
        }
        return elemnts;
    }

    private void printMap(Map<String, String> map) {
        for (String key : map.keySet())
            logger.debug(" key : _" + key + "_ , val : _" + map.get(key)
                    + "_");
    }

    private Map<String, String> getAtributes(Element element,
                                             List<String> questionReadAtrib) {
        Map<String, String> foundAtrib = new HashMap<String, String>();

        AttributeSet as = element.getAttributes();
        Enumeration e = as.getAttributeNames();
        while (e.hasMoreElements()) {
            Object keyObj = e.nextElement();
            if (keyObj != null) {
                Object valObj = as.getAttribute(keyObj);
                if (valObj != null)
                    if (questionReadAtrib.contains(String.valueOf(keyObj)))
                        foundAtrib.put(String.valueOf(keyObj),
                                String.valueOf(valObj));
            }
        }

        return foundAtrib;
    }

    private void printAtribute(Element elem) {
        logger.debug("--------------------------------------------");
        logger.debug("Elemn name : " + elem.getName());
        AttributeSet as = elem.getAttributes();
        Enumeration e = as.getAttributeNames();
        while (e.hasMoreElements()) {
            Object key = e.nextElement();
            if (key != null) {
                System.out.print("  atrName : " + key.toString() + " , ");
                Object val = as.getAttribute(key);
                if (val != null)
                    System.out.print(" atrVal : " + val.toString());

                logger.debug(" ");
            }
        }
        logger.debug("--------------------------------------------");
    }

    private void printElement(Element element) {
        logger.debug(" elementName " + element.getName() + " : _"
                + getElementText(element));
    }

    private String getElementText(Element element) {
        String text = "";
        try {
            int startOffset = element.getStartOffset();
            int endOffset = element.getEndOffset();
            int length = endOffset - startOffset;
            text = element.getDocument().getText(startOffset, length);
        } catch (Exception e) {
            logger.error("",e);
        }
        return text.trim();
    }

    public void printProps() {
        Dictionary<Object, Object> dic = htmlDoc.getDocumentProperties();
        Enumeration e = dic.keys();

        while (e.hasMoreElements()) {

            Object keyObj = e.nextElement();
            Object valObj = dic.get(keyObj);
            logger.debug(keyObj + " " + valObj);
        }
    }

}