package com.bla.laa.Net;

import com.bla.laa.Common.CommonS;
import com.bla.laa.Container.TCase;

public class UrlFormatter {

    /**
     * Get img Url
     * ?bilde_l=3037&end=jpg
     * ?bilde_s=3037&end=jpg
     * http://csnt.csdd.lv/?bilde_l=4627&end=jpg
     * ?bilde_e=1&end=jpg --- nav bildes
     */
    public static String getImgLUrl(Integer imgId) {
        StringBuffer sb = new StringBuffer();
        sb.append(Neti.SITE_CSDD);
        sb.append("/?bilde_l=");
        sb.append(imgId);
        sb.append("&end=jpg");
        return sb.toString();
    }

    public static String getImgSUrl(Integer imgId) {
        StringBuffer sb = new StringBuffer();
        sb.append(Neti.SITE_CSDD);
        sb.append("/?bilde_s=");
        sb.append(imgId);
        sb.append("&end=jpg");
        return sb.toString();
    }

    /**
     * question=32383&action=eksamens&perform=insertAtb&answer=55666&submit=true
     */
    public static String getNextQuestionPOSTStr(TCase tc, String corectAnswer) {

        if (!tc.isQuestionAnswerOK())
            return null;

        StringBuffer strBuff = new StringBuffer();
        strBuff.append("question=");
        strBuff.append(tc.getQuestion().getQuestionId());
        strBuff.append("&");
        strBuff.append("action=eksamens");
        strBuff.append("&");
        strBuff.append("perform=insertAtb");
        strBuff.append("&");
        strBuff.append("answer=");

        if (corectAnswer.length() != 0)
            strBuff.append(corectAnswer);
        else { // random one of answers
            int randAns = CommonS.getRandomInt(tc.getAnswers().size());
            strBuff.append(tc.getAnswers().get(randAns).getAnswerCsddId());
        }

        strBuff.append("&");
        strBuff.append("submit=true");

        return strBuff.toString();
    }


}

