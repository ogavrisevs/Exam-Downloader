package com.bla.laa;

import com.bla.laa.Common.MyCustException;
import com.bla.laa.Container.Answer;
import com.bla.laa.Container.Images;
import com.bla.laa.Container.Question;
import com.bla.laa.Container.TCase;
import com.bla.laa.Container.TCaseCont;
import com.bla.laa.Net.Neti;
import com.bla.laa.Net.UrlFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CsddBkat implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(CsddBkat.class);
    // for every thread new Neti object !!!
    private final Neti neti = new Neti();
    private final WebPageHistory history = new WebPageHistory();
    private static Storage apacheDerbyClient = null;
    private static StorageFactory sf = null;
    private static final String startBtest = "action=eksamens&perform=type&eks_tips=21&submit=true";
    private static final List<String> initAnswers = new ArrayList(Arrays.asList("55666", "55671", "55668"));

    String postStr = "";

    @Override
    public void run() {
        try {
            logger.warn("Thread start");
            apacheDerbyClient = new Storage();
            sf = new StorageFactory(apacheDerbyClient);

            new CsddBkat().runMe();

            logger.warn("Thread normal ended");
        } catch (MyCustException mce) {
            logger.error("Thred unexpected end ", mce);
        }
    }

    private void runMe() {
        if (initTest())
            get30TestCases();
    }

    void get30TestCases() {
        try {
            TCaseCont tcAll = new TCaseCont();
            postStr = "action=default";
            get30Question(tcAll);
            get30Answers(tcAll);
            sf.storeAll(tcAll);
        } catch (MyCustException e) {
            logger.error(" T end unexpected : ", e);
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    /**
     * get questions and put random answers
     */
    private void get30Question(TCaseCont tcAll) throws Exception {
        while (tcAll.getTestCaseCount() < TCaseCont.QUESTION_COUNT) {
            logger.info("Retrieve question : " + (tcAll.getTestCaseCount() + 1) + " ------------------------ ");
            TCase tc = getOneQuestion(postStr);
            if (tcAll.dublTestCase(tc))
                throw new MyCustException("TCase dublicate");
            tcAll.setTestCase(tc);
            postStr = UrlFormatter.getNextQuestionPOSTStr(tc, "");
        }
        //chek chek
        if (tcAll.getTestCaseCount() != TCaseCont.QUESTION_COUNT)
            throw new MyCustException("tcAll.size != " + TCaseCont.QUESTION_COUNT);

    }

    /**
     * get Answers
     */
    private void get30Answers(TCaseCont tcAll) throws Exception {
        //get corect answer short-list(pointer list )
        StringBuffer buff = (StringBuffer) neti.doGETorPOST( Neti.HttpMethods.POST, postStr);
        Map<Integer, Integer> answerShortIds = new Wrapper(buff).getAnswerIds();

        //get corect answer Full
        List<String> answCsddIds = tcAll.getAnswerPOSTStrList(answerShortIds);
        for (int counter = 0; counter < TCaseCont.QUESTION_COUNT; counter++) {
            logger.info("get answer : " + (counter + 1) + " ------------------------ ");
            StringBuffer page = (StringBuffer) neti.doGETorPOST(Neti.HttpMethods.POST, answCsddIds.get(counter));
            tcAll.setCorrectAnswer(counter, new Wrapper(page).getCorrectAnswer());
        }
    }

    private TCase getOneQuestion(String postStr) throws Exception {

        StringBuffer page = (StringBuffer) neti.doGETorPOST(Neti.HttpMethods.POST, postStr);
        history.addPage(Hash.getHash(page.toString()));
        Wrapper wraper = new Wrapper(page);
        Question question = wraper.getQuestion();
        List<Answer> answers = wraper.getAnswers();
        Integer imgNr = wraper.getImageNr();

        TCase tc = new TCase(question, answers);
        if (imgNr > 1) {
            BufferedImage imgL = (BufferedImage) neti.doGETorPOST(Neti.HttpMethods.GETpic, UrlFormatter.getImgLUrl(imgNr));
            BufferedImage imgS = (BufferedImage) neti.doGETorPOST(Neti.HttpMethods.GETpic, UrlFormatter.getImgSUrl(imgNr));
            tc.setPics(new Images(imgNr, imgL, imgS));
        }

        //jaapaarbauda uz keisiem kad viens un tas pas jautaajums cikleejas
        if (!tc.isQuestionAnswerOK()) {
            logger.error("isQuestionAnswerOK  != OK");
            throw new MyCustException();
        }
        return tc;
    }

    /**
     * First 3 qestions with answers for B cat.
     */
    boolean initTest() {
        try {
            neti.doGETorPOST(Neti.HttpMethods.GET, Neti.SITE_CSDD);
            neti.doPOST("action=jauns_eksamens");
            postStr = startBtest;

            for (int index = 0; index < initAnswers.size(); index++) {
                StringBuffer page = (StringBuffer) neti.doGETorPOST(Neti.HttpMethods.POST, postStr);
                Wrapper wraper = new Wrapper(page);
                history.addPage(Hash.getHash(page.toString()));
                Question question = wraper.getQuestion();
                List<Answer> answers = wraper.getAnswers();
                TCase tc = new TCase(question, answers);

                if (!tc.isQuestionAnswerOK())
                    return false;

                postStr = UrlFormatter.getNextQuestionPOSTStr(tc, initAnswers.get(index));
            }
            neti.doPOST(postStr);

        } catch (MyCustException e) {
            logger.error("", e);
            return false;
        } catch (Exception e) {
            logger.error("", e);
            return false;
        }
        return true;
    }
}
