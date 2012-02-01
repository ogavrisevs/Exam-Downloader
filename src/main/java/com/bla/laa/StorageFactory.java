package com.bla.laa;

import com.bla.laa.Common.CommonS;
import com.bla.laa.Common.MyCustException;
import com.bla.laa.Container.*;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class StorageFactory {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CsddBkat.class);
    private final Storage apacheDerbyClient;

    public StorageFactory(Storage apacheDerbyClient) {
        this.apacheDerbyClient = apacheDerbyClient;
    }

    /**
     * Chek if got this test case
     *
     * @return - true got this Test case
     */
    private boolean chekForDubl(TCase tc) throws MyCustException {
        if (chkRowExists(tc.getQuestion().getQuestionHash(), Storage.TABLE_QUESTIONS)) {
            List<Question> questions = getQuestion(0, tc.getQuestion().getQuestionHash());
            for (Question question : questions) {
                List<Answer> answers = getAnswers(question.getQuestionId(), question.getQuestionHash());

                List<String> answersInDb = new ArrayList<String>();
                String answersInDbCorect = "";
                for (Answer answer : answers) {
                    answersInDb.add(answer.getAnswerHash());
                    if (answer.isCorrect())
                        answersInDbCorect = answer.getAnswerHash();
                }

                List<String> answersInCurTestCase = new ArrayList<String>();
                String corectAnswersInCurTestCase = "";
                for (Answer answer : tc.getAnswers()) {
                    answersInCurTestCase.add(answer.getAnswerHash());
                    if (answer.isCorrect())
                        corectAnswersInCurTestCase = answer.getAnswerHash();
                }

                Boolean gotQuestionsAnswers = false;
                if ((answersInDb.containsAll(answersInCurTestCase)) &&
                        (answersInDbCorect.contentEquals(corectAnswersInCurTestCase))) {
                    gotQuestionsAnswers = true;
                } else {
                    continue;
                }

                Boolean gotQuestionsImages = false;
                if (tc.getPics() != null) {
                    Images imagesInDb = getImages(question.getQuestionId(), question.getQuestionHash());
                    if (imagesInDb == null) {
                        gotQuestionsImages = false;
                    } else {
                        if ((tc.getPics().getImageLargeHash().contentEquals(imagesInDb.getImageLargeHash())) &&
                                (tc.getPics().getImageSmallHash().contentEquals(imagesInDb.getImageSmallHash())))
                            gotQuestionsImages = true;
                    }
                }

                if ((gotQuestionsAnswers) && (tc.getPics() == null))
                    return true;
                else if ((gotQuestionsAnswers) && (tc.getPics() != null) && (gotQuestionsImages))
                    return true;
                else
                    return false;
            }
        }
        return false;

        /*




        boolean gotQuestion = chkRowExists(tc.getQuestion().getQuestionHash(), Storage.TABLE_QUESTIONS);
        boolean gotAnswer = false;

        for (Answer answer : tc.getAnswers()) {
            if (chkRowExists(answer.getAnswerHash(), Storage.TABLE_ANSWERS)) {
                gotAnswer = true;
                break;
            }
        }

        Boolean gotPicL = null;
        Boolean gotPicS = null;
        if (tc.getPics() != null) {
            gotPicL = false;
            gotPicS = false;
            if (chkRowExists(tc.getPics().getImageLargeHash(), Storage.TABLE_PICTURES_LARGE))
                gotPicL = true;

            if (chkRowExists(tc.getPics().getImageSmallHash(), Storage.TABLE_PICTURES_SMALL))
                gotPicS = true;
        }

        if ((gotAnswer) && (gotQuestion))
            if ((gotPicL != null) && (gotPicS != null)) {
                if ((gotPicL) && (gotPicS))
                    return true;

            } else
                return true;

        return false;
        */
    }

    private boolean chekForDubl2(TCase tc) throws MyCustException {
        if (chkRowExists(tc.getQuestion().getQuestionHash(), Storage.TABLE_QUESTIONS)) {
            List<TCase> tCases = loadTicketFromDb(tc.getQuestion().getQuestionId());
            for (TCase tCase : tCases) {
                if (tCase.equals(tc))
                    return true;
            }
        }
        return false;
    }


    public void storeAll(TCaseCont tcAll) throws MyCustException {
        // 1, 2, 3, ....
        for (Integer key : tcAll.get30TestCasesListKeys()) {
            TCase tc = tcAll.getTestCases(key);
            logger.info("Saving row : " + (key + 1) + " {" + tc.getQuestion().getQuestionId() + "}");
            saveTc(tc);
        }
    }

    public void saveTc(TCase tc) throws MyCustException {
        logger.debug(tc.toString());
        if ((chekForDubl(tc)) || (chekForDubl2(tc))) {
            logger.info("Already got question : " + tc.getQuestion().getQuestionId());
            return;
        } else
            logger.info("New question : " + tc.getQuestion().getQuestionId());

        // Save question
        addQuestion(tc.getQuestion().getQuestionHash(), tc.getQuestion().getQuestionText(), tc.getQuestion().getQuestionId());

        // Save answers
        for (Answer answer : tc.getAnswers()) {
            addAnswer(answer.getAnswerHash(), answer.getAnswerCsddId(), answer.getAnswerText(), answer.isCorrect());
            // Link Answer To Question
            addAnswerQuestion(answer.getAnswerHash(), answer.getAnswerCsddId(),
                    tc.getQuestion().getQuestionHash(), tc.getQuestion().getQuestionId());
        }

        // save pictures and links
        if (tc.getPics() != null) {
            final Integer imageCsddId = tc.getPics().getImageCsddId();
            if (imageCsddId > 1) {
                addPicture(tc.getPics().getImageLargeHash(), imageCsddId, tc.getPics().getImageLarge(), Storage.TABLE_PICTURES_LARGE);
                addPicture(tc.getPics().getImageSmallHash(), imageCsddId, tc.getPics().getImageSmall(), Storage.TABLE_PICTURES_SMALL);
                addQuestionPic(tc.getQuestion().getQuestionHash(), tc.getQuestion().getQuestionId(), tc.getPics().getImageCsddId(), tc.getPics().getImageLargeHash(), tc.getPics().getImageSmallHash());
            }
        }
    }


    /**
     * @param questionCsddId
     * @return TestCase
     */
    public List<TCase> loadTicketFromDb(Integer questionCsddId) throws MyCustException {

        List<TCase> tCases = new ArrayList<TCase>();

        List<Question> questions = getQuestion(questionCsddId, "");
        for (Question question : questions) {
            TCase tCase = new TCase();
            tCase.setQuestion(question);
            //all answers
            tCase.setAnswers(getAnswers(question.getQuestionId(), question.getQuestionHash()));
            Images pics = getImages(question.getQuestionId(), question.getQuestionHash());
            tCase.setPics(pics);
            tCases.add(tCase);
        }
        return tCases;
    }


    /**
     * @return true - exists
     */
    public boolean chkRowExists(String rowHash, String tableName) throws MyCustException {
        if ((rowHash == null) || (rowHash.length() != Hash.HASH_LENGHT))
            throw new MyCustException("Chek failture ! hash not set ");

        try {
            String sql = "Select 1 from " + Storage.schemName + "." + tableName +
                    " where hash = '" + rowHash + "'";
            ResultSet rs = apacheDerbyClient.execQuery(sql);

            if (rs.next()) {
                rs.getStatement().close();
                return true;
            } else {
                rs.getStatement().close();
                return false;
            }
        } catch (Exception e) {
            logger.error("", e);
        }
        return false;
    }


    public void addQuestion(String questionHash, String questionText, Integer qusetionCsddId) throws MyCustException {
        String sql = "insert into " + Storage.schemName + "." + Storage.TABLE_QUESTIONS +
                " (hash, CSDDid, questionText)" +
                " values ('" + questionHash + "' , " + qusetionCsddId + " , '" + questionText + "' ) ";
        apacheDerbyClient.execSQL(sql);
    }

    public void addAnswer(String answerHash, Integer qusetionCsddId, String answerText, Boolean corectAnsw) throws MyCustException {
        String sql = "insert into " + Storage.schemName + "." + Storage.TABLE_ANSWERS +
                " (hash, CSDDid, answtxt, answcor) values ('" + answerHash + "' , " +
                qusetionCsddId + " , '" +
                answerText + "' , '" +
                Boolean.toString(corectAnsw)
                + "' ) ";
        apacheDerbyClient.execSQL(sql);
    }


    public void addAnswerQuestion(String answerHash, Integer answerCsddId,
                                  String questionHash, Integer qusetionCsddId) throws MyCustException {
        String sql = "insert into " + Storage.schemName + "." + Storage.TABLE_QUESTION_ANSWERS_LINKER +
                " (answer, answerCSDDid, question, questionCSDDid)  values ('" + answerHash + "' ," +
                answerCsddId + " , " +
                " '" + questionHash + "' ," +
                qusetionCsddId +
                " ) ";
        apacheDerbyClient.execSQL(sql);
    }

    public void addQuestionPic(
            String questionHash, Integer questionCsddId, Integer picCsddId,
            String picLHash, String picSHash) throws MyCustException {

        String sql = "insert into " + Storage.schemName + "." + Storage.TABLE_QUESTION_PICTURES_LINKER +
                " (questionCSDDid, question, picCSDDid, picL, picS) values (" +
                questionCsddId + " , " +
                " '" + questionHash + "' , " +
                picCsddId + " , " +
                " '" + picLHash + "' , " +
                " '" + picSHash + "'  " +
                " ) ";
        apacheDerbyClient.execSQL(sql);
    }

    public void addPicture(String hash, Integer csddId, BufferedImage pic, String table) {
        try {
            Connection con = apacheDerbyClient.getSqlConn();

            PreparedStatement ps = con.prepareStatement(
                    "insert into " + Storage.schemName + "." + table + " " +
                            "(HASH, CSDDID, PIC ) " +
                            " values ( '" + hash + "' ," +
                            csddId + " , " +
                            " ?  " +
                            " )");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(pic, CommonS.PICEXT, baos);
            InputStream is = new ByteArrayInputStream(baos.toByteArray());
            ps.setBinaryStream(1, is, (int) baos.size());

            ps.execute();
        } catch (Exception ex) {
            logger.error("", ex);
        }
    }

    public Question getQuestion(Integer qusetionCsddId) throws MyCustException {
        return getQuestion(qusetionCsddId, "").get(0);
    }

    public Question getQuestion(String hash) throws MyCustException {
        return getQuestion(0, hash).get(0);
    }

    public List<Question> getQuestion(Integer qusetionCsddId, String hash) throws MyCustException {
        logger.debug("qusetionCsddId : " + qusetionCsddId + " hash " + hash);
        Connection sqlCon = apacheDerbyClient.getSqlConn();
        List<Question> questions = new ArrayList<Question>();
        try {
            String sql = "select * from " + Storage.schemName + "." + Storage.TABLE_QUESTIONS + " where 1 = 1";

            if (!hash.isEmpty())
                sql += " and HASH = '" + hash + "'";
            if ((qusetionCsddId != null) && (qusetionCsddId != 0))
                sql += " and csddId = " + qusetionCsddId;

            logger.debug(sql);
            PreparedStatement ps = sqlCon.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String questionHash = rs.getString("hash");
                Integer questionCsddIdfromDb = rs.getInt("CSDDid");
                String questionText = rs.getString("questionText");
                questions.add(new Question(questionText, questionCsddIdfromDb, questionHash));
            }
        } catch (Exception ex) {
            logger.error("", ex);
        }
        return questions;
    }

    public List<Integer> getQuestionList() throws MyCustException {
        List<Integer> questionList = new ArrayList<Integer>();
        Connection sqlCon = apacheDerbyClient.getSqlConn();
        try {
            String sql = "select * from " + Storage.schemName + "." + Storage.TABLE_QUESTIONS;
            PreparedStatement ps = sqlCon.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();
            while (rs.next())
                questionList.add(rs.getInt("CSDDid"));

        } catch (Exception ex) {
            logger.error("", ex);
        }
        return questionList;
    }


    public Images getImages(Integer qusetionCsddId, String qusetionHash) throws MyCustException {
        logger.debug("qusetionCsddId " + qusetionCsddId + "qusetionHash " + qusetionHash);
        Connection sqlCon = apacheDerbyClient.getSqlConn();
        Images img = null;
        try {
            String sql = "select * from " + Storage.schemName + "." + Storage.TABLE_QUESTION_PICTURES_LINKER + " " +
                    "where questionCSDDid = " + qusetionCsddId + " " +
                    " and question = '" + qusetionHash + "'";
            logger.debug(sql);
            PreparedStatement ps = sqlCon.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                img = new Images(rs.getInt("picCSDDid"));
                img.setImageLargeHash(rs.getString("picL"));
                img.setImageSmallHash(rs.getString("picS"));
            }
        } catch (Exception ex) {
            logger.error("", ex);
        }

        if ((img != null) && (img.getImageCsddId() > 1)) {
            try {
                Blob picL = apacheDerbyClient.getPics(img.getImageCsddId(), img.getImageLargeHash(), Storage.TABLE_PICTURES_LARGE);
                img.setImageLarge(ImageIO.read(picL.getBinaryStream()));
                Blob picS = apacheDerbyClient.getPics(img.getImageCsddId(), img.getImageSmallHash(), Storage.TABLE_PICTURES_SMALL);
                img.setImageSmall(ImageIO.read(picS.getBinaryStream()));
            } catch (Exception e) {
                logger.error("", e);
            }
        }
        return img;
    }


    /**
     * Get answers using index table :
     * <p/>
     * Question -> (Table) ANSWERQUESTIONS -> (Table) ANSWERS
     */
    public List<Answer> getAnswers(Integer qusetionCsddId, String qusetionHash) throws MyCustException {
        logger.debug("qusetionCsddId " + qusetionCsddId + "qusetionHash" + qusetionHash);
        Connection sqlCon = apacheDerbyClient.getSqlConn();
        List<Answer> answers = new ArrayList<Answer>();

        try {
            String sql2 = "select answer, answerCSDDid from " + Storage.schemName + "." + Storage.TABLE_QUESTION_ANSWERS_LINKER + " " +
                    "where 1 = 1 ";

            if ((qusetionCsddId != null) && (qusetionCsddId != 0))
                sql2 += " and questionCSDDid = " + qusetionCsddId;

            if (!qusetionHash.isEmpty())
                sql2 += " and question = '" + qusetionHash + "'";
            logger.debug(sql2);
            PreparedStatement ps = sqlCon.prepareStatement(sql2);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Answer answer = new Answer();
                answer.setAnswerHash(rs.getString("answer"));
                answer.setAnswerCsddId(rs.getInt("answerCsddId"));

                String sql = "select answtxt, CSDDid, answcor from " + Storage.schemName + "." + Storage.TABLE_ANSWERS + " " +
                        "where CSDDid = " + answer.getAnswerCsddId() + " and " +
                        "hash = '" + answer.getAnswerHash() + "'";
                logger.debug(sql2);
                PreparedStatement ps2 = sqlCon.prepareStatement(sql);

                ResultSet rs2 = ps2.executeQuery();
                while (rs2.next()) {
                    answer.setAnswerText(rs2.getString("answtxt"));
                    answer.setCorrect(rs2.getBoolean("answcor"));

                    if (!answers.contains(answer))
                        answers.add(answer);
                    else
                        logger.warn("records in Answers duplicating !!!:  " + answer.toString());
                }
            }
        } catch (Exception ex) {
            logger.error("", ex);
        }
        return answers;
    }

}
