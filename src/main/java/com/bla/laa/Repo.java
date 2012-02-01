package com.bla.laa;

import com.bla.laa.Common.CommonS;
import com.bla.laa.Common.MyCustException;
import com.bla.laa.Container.TCase;
import com.bla.laa.Net.Neti;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Repo {
    private static final Logger logger = LoggerFactory.getLogger(Repo.class);
    public static Storage apacheDerbyClient = null;
    public static StorageFactory sf = null;

    static void init() throws MyCustException {
        logger.warn("create Repo()");
        apacheDerbyClient = new Storage();
        sf = new StorageFactory(apacheDerbyClient);
    }

    public static void printOneQuestion(String... questionCsddIds) throws MyCustException {
        init();
        logger.warn("printOneQuestion(), args.lenght : " + questionCsddIds.length);
        try {
            for (String questionCsddId : questionCsddIds) {
                logger.info("print : " + questionCsddId);
                if (CommonS.containsStartArgs(Main.startOpt, questionCsddId))
                    continue;
                try {
                    Integer.parseInt(questionCsddId);
                } catch (NumberFormatException nfe) {
                    logger.error("question number not correct " + questionCsddId, nfe);
                    continue;
                }

                List<TCase> tCases = sf.loadTicketFromDb(Integer.valueOf(questionCsddId));
                for (TCase tCase : tCases) {
                    String fileName = Neti.getTempDir() + tCase.getQuestion().getQuestionId() + "." + CommonS.HTMLEXT;
                    tCase.saveTcAsHtml(fileName);
                    logger.info("save : " + fileName);
                }
                if (tCases.isEmpty())
                    logger.info("question : " + questionCsddId + " not found !!! ");

            }
        } catch (MyCustException mce) {
            logger.error("", mce);
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    public static void printAllQuestions() throws MyCustException {
        init();
        logger.warn("printAllQuestions()");
        try {
            List<Integer> questionList = sf.getQuestionList();
            for (Integer questionCSDDid : questionList) {
                String fileName = "";

                List<TCase> tCases = sf.loadTicketFromDb(Integer.valueOf(questionCSDDid));
                for (TCase tCase : tCases) {
                    logger.debug(tCase.toString());
                    fileName = Neti.getTempDir() + tCase.getQuestion().getQuestionId() + "." + CommonS.HTMLEXT;
                    tCase.saveTcAsHtml(fileName);
                    logger.debug("save : " + fileName);
                }
            }
        } catch (MyCustException mce) {
            logger.error("", mce);
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    /**
     * Prints Db stat on screean
     */
    public static void printDbStatus() {
        //:TODO in future
    }
}

