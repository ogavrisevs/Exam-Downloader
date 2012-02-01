package com.bla.laa.Container;

import com.bla.laa.Hash;

public class Question {
    private String questionText = "";
    private Integer questionCsddId = 0; // Id from CSSD system
    private String questionHash = "";

    public boolean isQuestionOk() {
        if (questionText == null)
            return false;
        if (questionText.length() == 0)
            return false;

        if (questionCsddId == null)
            return false;
        if (questionCsddId == 0)
            return false;

        return true;
    }

    public Question(String questionText, Integer questionCsddId) {
        this.questionText = questionText;
        this.questionCsddId = questionCsddId;
        if (this.questionHash.length() == 0)
            this.questionHash = Hash.getHash(questionText);

    }

    public Question(String questionText, Integer questionCsddId, String questionHash) {
        this.questionText = questionText;
        this.questionCsddId = questionCsddId;
        this.questionHash = questionHash;
    }

    public Question() {
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
        if (this.questionHash.length() == 0)
            this.questionHash = Hash.getHash(this.questionText);

    }

    public Integer getQuestionId() {
        return questionCsddId;
    }

    public String getQuestionHash() {
        return questionHash;
    }

    public void setQuestionHash(String questionHash) {
        this.questionHash = questionHash;
    }

    public void setQuestionCsddId(Integer questionCsddId) {
        this.questionCsddId = questionCsddId;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("Question");
        sb.append("{questionText='").append(questionText).append('\'');
        sb.append(", questionCsddId=").append(questionCsddId);
        sb.append(", questionHash='").append(questionHash).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Question question = (Question) o;

        //if (questionCsddId != null ? !questionCsddId.equals(question.questionCsddId) : question.questionCsddId != null)
        //    return false;
        if (questionHash != null ? !questionHash.equals(question.questionHash) : question.questionHash != null)
            return false;
        if (questionText != null ? !questionText.equals(question.questionText) : question.questionText != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = questionText != null ? questionText.hashCode() : 0;
        result = 31 * result + (questionCsddId != null ? questionCsddId.hashCode() : 0);
        result = 31 * result + (questionHash != null ? questionHash.hashCode() : 0);
        return result;
    }
}

