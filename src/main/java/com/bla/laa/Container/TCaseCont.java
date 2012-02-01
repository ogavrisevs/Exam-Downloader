package com.bla.laa.Container;

import com.bla.laa.Common.MyCustException;

import java.util.*;

public class TCaseCont {

    public static final int QUESTION_COUNT = 30;

    /**
     * All Test cases (30 by def.), counter 1,2,3 ....
     */
    private final Map<Integer, TCase> testCases30 = new HashMap<Integer, TCase>();

    public void addTestCase(Integer id, TCase tc) {
        this.testCases30.put(id, tc);
    }


    /**
     * put TCase in Map, first index = 0
     */
    public void setTestCase(TCase tc) {
        this.testCases30.put(this.testCases30.size(), tc);
    }

    /**
     * action=eksamens&perform=loadQuestion&question_id=23201570&next=1&prev=1
     */
    public List<String> getAnswerPOSTStrList(Map<Integer, Integer> shortAnswerIdsList) {
        List<String> postList = new ArrayList<String>();
        StringBuffer sb = new StringBuffer();

        if (shortAnswerIdsList == null)
            if (shortAnswerIdsList.size() == 0)
                return null;

        for (int index = 1; index <= QUESTION_COUNT; index++) {
            sb.delete(0, sb.length());
            sb.append("action=eksamens&perform=loadQuestion&question_id=");
            sb.append(shortAnswerIdsList.get(index));
            sb.append("&next=1");
            if (index > 1)
                sb.append("&prev=1");

            postList.add(sb.toString());
        }
        return postList;
    }

    public void setCorrectAnswer(Integer testCaseId, Integer corectQuestionCsddId) throws MyCustException {
        if ((testCaseId < 0) || (testCaseId > TCaseCont.QUESTION_COUNT))
            throw new MyCustException("wrong test case nuber");

        if (!this.testCases30.containsKey(testCaseId))
            throw new MyCustException("key : " + testCaseId + " not found in testCases30");

        TCase tc = this.testCases30.get(testCaseId);
        for (Answer answer : tc.getAnswers())
            if (answer.getAnswerCsddId().compareTo(corectQuestionCsddId) == 0)
                answer.setCorrect(Boolean.TRUE);
    }

    public Map<Integer, TCase> getAllTestCases() {
        return this.testCases30;
    }

    public Set<Integer> get30TestCasesListKeys() {
        return this.testCases30.keySet();
    }

    public TCase getTestCases(Integer index) {
        return this.testCases30.get(index);
    }


    /*public boolean isAnswerCorect( Integer key,  Integer  answerCSSDId ){

        boolean foundKey = false;
        for (Integer sk : this.correctAnswerlist.keySet())
            if (sk.equals(key))
                foundKey = true;

        if (foundKey != true )
            CommonS.out("Problem here!");

        Integer answerCsddId = this.correctAnswerlist.get(key);
        if (answerCsddId.compareTo(answerCSSDId) == 0)
            return true;
        else
            return false;
    }*/

    public Integer getTestCaseCount() {
        return this.testCases30.size();
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("TCaseCont");
        sb.append("{testCases30=").append(testCases30);
        sb.append('}');
        return sb.toString();
    }

    /**
     * @return true - got in list !
     */
    public boolean dublTestCase(TCase tc) {
        for (Integer key : this.testCases30.keySet()) {
            TCase curTc = this.testCases30.get(key);
            if (curTc.equals(tc))
                return true;
        }
        return false;
    }
}

