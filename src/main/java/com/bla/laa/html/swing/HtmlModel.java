package com.bla.laa.html.swing;

import javax.swing.text.html.HTML;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HtmlModel {
    private final List<Elem> elemList = new ArrayList<Elem>();
    private final List<ElemText> elemTList = new ArrayList<ElemText>();

    public void addElem(Elem elem, HtmlId elemId) {
        elem.setElemId(elemId);
        this.elemList.add(elem);
    }

    public void addElemText(ElemText elemText) {
        this.elemTList.add(elemText);
    }

    public List<Elem> getElements() {
        return elemList;
    }

    public String toStringElementsWithId() {
        StringBuffer sb = new StringBuffer();
        for (Elem elem : elemList) {
            sb.append(elem.getElemId().getIdentSpaces());
            sb.append(elem.getTags());
            sb.append(" ");
            sb.append(elem.getElemId().toString());
            sb.append("\n");
        }
        return sb.toString();
    }

    public String toStringElementsNative() {
        StringBuffer sb = new StringBuffer();
        for (Elem elem : elemList) {
            sb.append(elem.getElemId().getIdentSpaces());
            sb.append(elem.toStringNative());
            sb.append("\n");
        }
        return sb.toString();
    }

    public String toStringElements() {
        StringBuffer sb = new StringBuffer();
        HtmlId prievId = null;
        HtmlId curId = null;

        for (Elem elem : elemList) {
            curId = elem.getElemId();
            String text = getElemText(prievId, curId);
            if (text != null)
                if (text.length() != 0) {
                    sb.append(elem.getElemId().getIdentSpaces());
                    if (text.length() > 10)
                        sb.append(text.substring(0, 10) + "...");
                    else
                        sb.append(text);
                    sb.append("\n");
                }

            sb.append(elem.getElemId().getIdentSpaces());
            sb.append(elem.toString());
            sb.append("\n");

            prievId = curId;
        }
        return sb.toString();
    }

    public String toStringTextList() {
        StringBuffer sb = new StringBuffer();
        for (ElemText elemT : elemTList) {
            sb.append("--- start : ");
            sb.append(elemT.getIdAfter().toString());
            sb.append("\n");

            sb.append(elemT.getText());
            sb.append("\n");

            sb.append("--- end   : ");
            sb.append(elemT.getIdBefore().toString());
            sb.append("\n");
        }
        return sb.toString();
    }


    /**
     * 1.2     <- parentId
     * 1.2.1  <- retrun
     * 1.2.2  <- retrun
     * ...
     */
    public List<Elem> getElemChildsById(String parentId) {
        List<Elem> foundElem = new ArrayList<Elem>();
        for (Elem elem : elemList) {
            String parnetId = elem.getElemId().getParentId();
            if (parnetId.contentEquals(parentId))
                foundElem.add(elem);
        }
        return foundElem;
    }

    public Elem getElemById(String elemId) {
        for (Elem elem : elemList) {
            if (elem.getElemId().toString().contentEquals(elemId))
                return elem;
        }
        return null;
    }


    public String getElemText(HtmlId beginId, HtmlId endId) {
        if (beginId == null)
            return null;

        if (endId == null)
            return null;

        String beginIdstr = beginId.toString();
        String endIdStr = endId.toString();

        for (ElemText elemT : this.elemTList) {
            if ((elemT.getIdBefore().toString().contentEquals(beginIdstr))
                    && (elemT.getIdAfter().toString().contentEquals(endIdStr)))
                return elemT.getText();
        }

        return null;
    }

    /**
     * determ if tag has closing tag, search from elem poz
     * <p/>
     * 1.2.1  ...
     * 1.2.2  ...
     * 1.2.3 <title>  <- elem, search start poz.
     * 1.2.4
     * 1.2.5 </title> <- search for
     *
     * @return closing element id
     */
    public HtmlId getClosingTagId(Elem elem) {

        String tag = elem.getTags();
        if (tag.length() == 0)
            return null;

        Integer weight = elem.getElemId().getIdentWeight();
        String parentId = elem.getElemId().getParentId();
        if (parentId.length() == 0)
            return null;

        List<Elem> foundelemList = getElemChildsById(parentId);
        for (Elem elemF : foundelemList) {
            // chek only elements after search elemnt
            if (elemF.getElemId().getIdentWeight() > weight)
                if (elemF.getTags().contentEquals(tag)) {
                    List<Atrib> atribList = elemF.getAtributes();
                    for (Atrib atrib : atribList)
                        if (atrib.isCloseTag())
                            return elemF.getElemId();
                }
        }
        return null;
    }

}

class Atrib {
    private Object atrKey = null;
    private String atrKeyType = "";
    private Object atrVal = null;
    private String atrValType = "";
    private boolean isHtmlAtribute = false;


    Atrib(Object atrKey, Object atrVal) {
        this.atrKey = atrKey;
        this.atrKeyType = HtmlReader.castObjToType(atrKey);
        this.atrVal = atrVal;
        this.atrValType = HtmlReader.castObjToType(atrVal);
        this.isHtmlAtribute = false;
    }

    Atrib(Object atrKey, Object atrVal, boolean isAtrb) {
        this.atrKey = atrKey;
        this.atrKeyType = HtmlReader.castObjToType(atrKey);
        this.atrVal = atrVal;
        this.atrValType = HtmlReader.castObjToType(atrVal);
        this.isHtmlAtribute = isAtrb;
    }


    /**
     * k:(String)CR = v:(Boolean)true
     * or endtag=true
     * ident end of tag
     */
    public boolean isCloseTag() {
        if (hasKeyVal())
            return false;

        if (atrKey instanceof String)
            if (String.valueOf(atrKey).contains("CR"))
                if (atrVal instanceof Boolean)
                    if ((Boolean) atrVal == Boolean.TRUE)
                        return true;

        if (atrKey instanceof HTML.Attribute)
            if (String.valueOf(atrKey).contains("endtag"))
                if (atrVal instanceof String)
                    if (String.valueOf(atrVal).contentEquals("true"))
                        return true;

        return false;
    }

    /**
     * Determs Atrib has Key and value
     */
    public boolean hasKeyVal() {
        if ((this.atrKey != null)
                && (this.atrVal != null))
            return false;

        return true;
    }


    public Object getAtrKey() {
        return atrKey;
    }

    public void setAtrKey(Object atrKey) {
        this.atrKey = atrKey;
    }

    public Object getAtrVal() {
        return atrVal;
    }

    public void setAtrVal(Object atrVal) {
        this.atrVal = atrVal;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.valueOf(this.atrKey));
        sb.append("=");
        if (this.atrVal != null)
            sb.append(String.valueOf(this.atrVal));

        return sb.toString();
    }

    public String toStringUnknow() {
        StringBuilder sb = new StringBuilder();
        sb.append("k:");
        sb.append("(");
        sb.append(String.valueOf(this.atrKeyType));
        sb.append(")");
        sb.append(String.valueOf(this.atrKey));

        if (this.atrVal != null) {
            sb.append(" = ");
            sb.append("v:");
            sb.append("(");
            sb.append(String.valueOf(this.atrValType));
            sb.append(")");
            sb.append(String.valueOf(this.atrVal));


        }
        return sb.toString();
    }

    public boolean isHtmlAtribute() {
        return isHtmlAtribute;
    }

    public void setHtmlAtribute(boolean isHtmlAtribute) {
        this.isHtmlAtribute = isHtmlAtribute;
    }

}

class Elem {
    private final List<String> tags = new ArrayList<String>();
    private final List<Atrib> atributes = new ArrayList<Atrib>();
    // Format: 1.1.2.n
    private HtmlId elemId = null;
    private boolean isTagClosed = false;
    private Integer endOffset = 0;
    private Integer startOffset = 0;

    public void setOffset(Integer endOffset, Integer startOffset) {
        this.endOffset = endOffset;
        this.startOffset = startOffset;
    }

    public void addTag(String tagName) {
        tags.add(tagName);
    }

    public String getTags() {
        String tagStr = "";
        for (String tag : tags)
            tagStr += tag + " ";

        return tagStr;
    }

    public void setElemId(HtmlId id) {
        this.elemId = id;
    }

    public HtmlId getElemId() {
        return this.elemId;
    }

    /**
     * Smart formating
     */
    @Override
    public String toString() {

        String atrStr = "";
        String atrUnkStr = "";
        if (atributes.size() != 0)
            for (Atrib atribute : atributes)
                if (!atribute.isCloseTag()) //not close tag
                    if (atribute.isHtmlAtribute())
                        atrStr += atribute.toString() + " ";
                    else
                        atrUnkStr += atribute.toStringUnknow() + " ";

        String tagStr = "";
        if (tags.size() != 0)
            for (String tag : tags)
                tagStr += tag + " ";

        StringBuilder sb = new StringBuilder();
        if ((tagStr.length() != 0) || (atrStr.length() != 0)) {
            if (this.isTagClosed)
                sb.append("</");
            else
                sb.append("<");
            sb.append(tagStr.trim());
            sb.append(atrStr.trim());
            sb.append(">");

        }
        if (atrUnkStr.length() != 0) {
            sb.append("[");
            sb.append(atrUnkStr);
            sb.append("]");
        }


        return sb.toString();
    }

    public String toStringNative() {

        String atrStr = "";
        String atrUnkStr = "";
        if (atributes.size() != 0)
            for (Atrib atribute : atributes)
                if (atribute.isHtmlAtribute())
                    atrStr += atribute.toString() + " ";
                else
                    atrUnkStr += atribute.toStringUnknow() + " ";

        String tagStr = "";
        if (tags.size() != 0)
            for (String tag : tags)
                tagStr += tag + " ";

        StringBuilder sb = new StringBuilder();
        if ((tagStr.length() != 0) || (atrStr.length() != 0)) {
            sb.append("<");
            sb.append(tagStr);
            sb.append(atrStr);
            sb.append(">");
        }
        if (atrUnkStr.length() != 0) {
            sb.append("[");
            sb.append(atrUnkStr);
            sb.append("]");
        }

        return sb.toString();
    }

    public void addAtrib(Object atrKey, Object atrVel) {
        Atrib atribute = new Atrib(atrKey, atrVel);
        atributes.add(atribute);
        chkForCloseTag(atribute);
    }

    public void addAtribUnknow(Object atrKey, Object atrVel) {
        Atrib atribute = new Atrib(atrKey, atrVel, true);
        this.atributes.add(atribute);
        chkForCloseTag(atribute);
    }

    public List<Atrib> getAtributes() {
        return this.atributes;
    }

    public void chkForCloseTag(Atrib atribute) {
        if (!this.isTagClosed)
            if (atribute.isCloseTag())
                this.isTagClosed = true;
    }


    public Integer getEndOffset() {
        return endOffset;
    }

    public void setEndOffset(Integer endOffset) {
        this.endOffset = endOffset;
    }

    public Integer getStartOffset() {
        return startOffset;
    }

    public void setStartOffset(Integer startOffset) {
        this.startOffset = startOffset;
    }

    //isTagClosed()
}

class ElemText {
    private String text = "";
    private HtmlId idBefore = null;
    private HtmlId idAfter = null;

    public ElemText(String text, HtmlId idBefore, HtmlId idAfter) {
        super();
        this.text = text;
        this.idBefore = idBefore;
        this.idAfter = idAfter;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public HtmlId getIdBefore() {
        return idBefore;
    }

    public void setIdBefore(HtmlId idBefore) {
        this.idBefore = idBefore;
    }

    public HtmlId getIdAfter() {
        return idAfter;
    }

    public void setIdAfter(HtmlId idAfter) {
        this.idAfter = idAfter;
    }

    @Override
    public String toString() {
        return "ElemText [text=" + text + ", idBefore=" + idBefore
                + ", idAfter=" + idAfter + "]";
    }

}

class HtmlId {
    /**
     * [1, 1] , [2 , 2] , [3 , 1] = > 1.2.1
     */
    private Map<Integer, Integer> idMap = new HashMap<Integer, Integer>();

    /**
     * Constructor , first id
     */
    HtmlId() {
        //idMap.put(1, 1);
    }

    /**
     * Constructor , from String (transfer from string to Map)
     */
    HtmlId(String idStr) {
        String idArray[] = idStr.split(HtmlReader.SPLITTER);
        if (idArray.length == 0) {
            idMap.put(1, 1);
            return;
        }

        for (int index = 0; index < idArray.length; index++)
            this.idMap.put((index + 1), Integer.valueOf(idArray[index]));
    }


    /**
     * Constructor, gen child id from current id
     */
    public HtmlId genChildId() {
        HtmlId newHtmlId = new HtmlId();
        newHtmlId.getIdMap().clear();
        newHtmlId.getIdMap().putAll(this.idMap);
        newHtmlId.incId();
        return newHtmlId;
    }

    /**
     * Constructor, gen next id from current id
     */
    public HtmlId genNextId() {
        HtmlId newHtmlId = new HtmlId();
        newHtmlId.getIdMap().clear();
        newHtmlId.getIdMap().putAll(this.idMap);

        Integer nextId = newHtmlId.getMaxId();
        Integer val = newHtmlId.idMap.get(nextId);
        newHtmlId.idMap.put(nextId, ++val);
        return newHtmlId;
    }


    @Override
    public String toString() {
        String idStr = "";
        Object keyList[] = idMap.keySet().toArray();

        for (int index = 0; index < keyList.length; index++) {
            Integer key = (Integer) keyList[index];
            idStr += String.valueOf(idMap.get(key));
            if (index < keyList.length - 1)
                idStr += HtmlReader.SPLITTER;
        }
        return idStr;
    }

    /**
     * 1.2.2  -> 1.2.2.1
     */
    private void incId() {
        Integer maxKey = this.getMaxId();
        maxKey++;
        this.idMap.put(maxKey, 1);
    }


    private Integer getMaxId() {
        Integer lastKey = 0;
        for (Integer key : this.idMap.keySet()) {
            if (key > lastKey)
                lastKey = key;
        }
        return lastKey;
    }

    /**
     * 1.2.3.4 = level 4
     *
     * @return id level
     */
    private Integer getIdentLevel() {
        return this.idMap.size();
    }

    /**
     * 1.2.3 - > 3
     *
     * @return
     */
    public Integer getIdentWeight() {
        Integer key = this.getMaxId();
        return (this.idMap.get(key));
    }


    /**
     * 1.2.5  -> 1.2
     */
    public String getParentId() {
        String idStr = "";
        Object keyList[] = idMap.keySet().toArray();

        for (int index = 0; index < keyList.length - 1; index++) {
            Integer key = (Integer) keyList[index];
            idStr += String.valueOf(idMap.get(key));
            if (index < keyList.length - 2)
                idStr += HtmlReader.SPLITTER;
        }
        return idStr;

    }


    public String getIdentSpaces() {
        return getSpaces(getIdentLevel());
    }

    private String getSpaces(int count) {
        String str = "";
        for (int index = 0; index < count; index++)
            str += "  ";
        return str;
    }


    private Map<Integer, Integer> getIdMap() {
        return idMap;
    }

    private void setIdMap(Map<Integer, Integer> idMap) {
        this.idMap = idMap;
    }

}
	
	


