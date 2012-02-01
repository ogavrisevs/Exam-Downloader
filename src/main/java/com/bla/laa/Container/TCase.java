package com.bla.laa.Container;

import com.bla.laa.Common.CommonS;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class TCase {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(TCase.class);
    private Question question = new Question();
    // order important !!!
    private List<Answer> answers = new ArrayList<Answer>();

    private Images pics = null;

    public TCase(Question question, List<Answer> answers) {
        this.setQuestion(question);
        this.setAnswers(answers);
    }

    public TCase() {
    }

    /**
     * @param obj
     * @return true - obj equal
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TCase))
            return false;

        TCase othisObj = (TCase) obj;
        if (!this.question.equals(othisObj.getQuestion()))
            return false;

        if (this.answers.size() != othisObj.getAnswers().size())
            return false;

        for (Answer answer : this.answers){
            if (!othisObj.getAnswers().contains(answer))
                return false;
        }

        if (this.pics != null)
            if (!this.pics.equals(othisObj.getPics()))
                return false;

        return true;
    }

    public void setPics(Images pics) {
        this.pics = pics;
    }

    public Images getPics() {
        return this.pics;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public Question getQuestion() {
        return question;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    /**
     * @return - true ans. ok ok ok
     */
    public boolean isQuestionAnswerOK() {
        if (this.question.getQuestionText().length() == 0) {
            logger.debug("QuestionText().length() == 0 !");
            return false;
        }
        if (this.question.getQuestionHash().length() == 0) {
            logger.debug("QuestionHash().length() == 0 !");
            return false;
        }

        if (this.question.getQuestionId() == 0) {
            logger.debug("QuestionId() == 0");
            return false;
        }

        if (this.answers.size() == 0) {
            logger.debug("answers.size() == 0");
            return false;
        }

        for (Answer answer : this.answers) {
            if (answer.getAnswerCsddId() == 0) {
                logger.debug("AnswerCsddId == 0");
                return false;
            }
            if (answer.getAnswerText().length() == 0) {
                logger.debug("AnswerText.length == 0");
                return false;
            }

            if (answer.getAnswerHash().length() == 0) {
                logger.debug("AnswerHash.length == 0");
                return false;
            }
        }
        if (this.getPics() != null) {
            if ((this.getPics().getImageLarge() == null) ||
                    (this.getPics().getImageSmall() == null)) {
                logger.debug("ImageLarge or ImageSmall == null");
                return false;
            }

            if ((this.getPics().getImageLargeHash().length() == 0) ||
                    (this.getPics().getImageSmallHash().length() == 0)) {
                logger.debug("ImageLargeHash or ImageSmallHash  length == 0");
                return false;
            }
        }

        return true;
    }

    /**
     * @return - saved file name
     */
    public String saveTcAsHtml(String fileName) throws com.bla.laa.Common.MyCustException {
        StringBuffer sb = new StringBuffer();

        sb.append("<html>");
        sb.append("  <head>");
        sb.append("  <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">");
        sb.append("  <title>Untitled document</title>");
        sb.append("  <style type=\"text/css\">ol{margin:0;padding:0}p{margin:0}");
        sb.append("  .c2{width:468pt;background-color:#ffffff;padding:72pt 72pt 72pt 72pt}.c0{direction:ltr}");
        sb.append("  .c1{height:11pt}");
        sb.append("  body{color:#000000;font-size:11pt;font-family:Arial}");
        sb.append("  h1{padding-top:24pt;color:#000000;font-size:24pt;font-family:Arial;");
        sb.append("  font-weight:bold;padding-bottom:6pt}h2{padding-top:18pt;");
        sb.append("  olor:#000000;font-size:18pt;font-family:Arial;font-weight:bold;padding-bottom:4pt}");
        sb.append("  h3{padding-top:14pt;color:#000000;font-size:14pt;font-family:Arial;");
        sb.append("  font-weight:bold;padding-bottom:4pt}");
        sb.append("  h4{padding-top:12pt;color:#000000;font-size:12pt;font-family:Arial;");
        sb.append("  font-weight:bold;padding-bottom:2pt}");
        sb.append("  h5{padding-top:11pt;color:#000000;font-size:11pt;font-family:Arial;");
        sb.append("  font-weight:bold;padding-bottom:2pt}h6{padding-top:10pt;color:#000000;font-size:10pt;");
        sb.append("  font-family:Arial;font-weight:bold;padding-bottom:2pt}");
        sb.append("  </style>");
        sb.append("  </head>");
        sb.append("<body class=\"c2\">");

        if ((this.pics != null) && (this.pics.getImageCsddId() > 1)) {
            String fileNameS = String.valueOf(this.pics.getImageCsddId()) + "_L." + CommonS.PICEXT;
            String fileNameL = String.valueOf(this.pics.getImageCsddId()) + "_S." + CommonS.PICEXT;
            CommonS.saveImg(this.pics.getImageLarge(), fileNameL);
            CommonS.saveImg(this.pics.getImageSmall(), fileNameS);

            sb.append("<p class=\"c0\">");
            sb.append("<img ");
            sb.append("height=\"" + this.pics.getImageLargeHeight() + "\"");
            sb.append("width=\"" + this.pics.getImageLargeWidth() + "\"");
            sb.append(" src=\"" + fileNameL + "\" ><p>");

            sb.append("<img ");
            sb.append("height=\"" + this.pics.getImageSmallHeight() + "\"");
            sb.append("width=\"" + this.pics.getImageSmallWidth() + "\"");
            sb.append(" src=\"" + fileNameS + "\" ><p>");
        } else
            sb.append("<p class=\"c0\"><span> Bildes nav </span></p>");


        sb.append("<p class=\"c0 c1\"><span></span></p>");
        sb.append("<p class=\"c0\"><span> " +
                " id : " + this.question.getQuestionId() + " " +
                this.question.getQuestionText() + " </span></p>");

        sb.append("<p class=\"c0 c1\"><span></span></p>");

        for (Answer answer : this.getAnswers()) {
            if (answer.isCorrect())
                sb.append("<p class=\"c0\"><span>" +
                        " id : " + answer.getAnswerCsddId() + " <b>" + answer.getAnswerText() + "</b></span></p>");
            else
                sb.append("<p class=\"c0\"><span>" +
                        " id : " + answer.getAnswerCsddId() + " " + answer.getAnswerText() + "</span></p>");
        }
        sb.append("</body>");
        sb.append("</html>");


        return CommonS.saveStringBuffer(sb, fileName);
    }

    /**
     * {
     * "Question" : {
     * "questionTxt": "questionnnn",
     * "questionId" : 123
     * },
     * "Answers" : {
     * "answer": {
     * "answerText": "answ1",
     * "answerId":"id",
     * "corect": "1"
     * },
     * "answer": {
     * "answerText": "answ2",
     * "answerId":"id",
     * "corect": 0
     * },
     * "answer": {
     * "answerText": "answ3",
     * "answerId":"id",
     * "corect": 1
     * }
     * },
     * "Images" :{
     * "imageL": {
     * "imageLurl": "urlll",
     * "imageLid": "1234"
     * },
     * "imageS": {
     * "imageSurl": "urlll",
     * "imageSid": "1234"
     * }
     * }
     * }
     */
    public String toJSON() {
        StringBuffer sb = new StringBuffer();
        sb.append("{");
        //Question
        sb.append("\"Question\" : {");
        sb.append("\"questionTxt\": \"" + this.getQuestion().getQuestionText() + "\",");
        sb.append("\"questionId\" : " + this.getQuestion().getQuestionId());
        sb.append("},");
        //Answers
        sb.append("\"Answers\" : {");
        for (Answer answer : this.getAnswers()) {
            sb.append("\"answer\": {");
            sb.append("\"answerText\": \"" + answer.getAnswerText() + "\",");
            sb.append("\"answerId\"  :   " + answer.getAnswerCsddId() + ",");
            sb.append("\"corect\"    :   " + (answer.isCorrect() ? 1 : 0));
            sb.append("},");
        }
        sb.replace(sb.length() - 1, sb.length(), "");
        sb.append("}");
        //Images
        if (this.getPics() != null) {
            sb.append(",\"Images\" : {");
            sb.append("\"imageL\": {");
            sb.append("\"imageSurl\": \"" + this.getPics().getImageCsddId() + "_S.jpg" + "\",");
            sb.append("\"imageSid\" : " + this.getPics().getImageCsddId());
            sb.append("},");
            sb.append("\"imageS\": {");
            sb.append("\"imageSurl\": \"" + this.getPics().getImageCsddId() + "_S.jpg" + "\",");
            sb.append("\"imageSid\" : " + this.getPics().getImageCsddId());
            sb.append("}");
            sb.append("}");
        }
        sb.append("}");
        return sb.toString();
    }


    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("TestCases");
        sb.append("{question=").append(question);
        sb.append(", answers=").append(answers);
        sb.append(", pics=").append(pics);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int result = question != null ? question.hashCode() : 0;
        result = 31 * result + (answers != null ? answers.hashCode() : 0);
        result = 31 * result + (pics != null ? pics.hashCode() : 0);
        return result;
    }
}

