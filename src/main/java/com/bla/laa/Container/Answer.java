package com.bla.laa.Container;

import com.bla.laa.Hash;

public class Answer {
    private String answerText = "";
    private Integer answerCsddId = 0;
    private String answerHash = "";
    private Boolean isCorrect = false;

    public Answer() {
    }

    public Answer(Integer answerCsddId, String answerText) {
        this.answerText = answerText;
        if (this.answerHash.length() == 0)
            this.answerHash = Hash.getHash(this.answerText);
        this.answerCsddId = answerCsddId;
    }


    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    public Integer getAnswerCsddId() {
        return answerCsddId;
    }

    public void setAnswerCsddId(Integer answerCsddId) {
        this.answerCsddId = answerCsddId;
    }

    public String getAnswerHash() {
        return answerHash;
    }

    public void setAnswerHash(String answerHash) {
        this.answerHash = answerHash;
    }

    public Boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(Boolean correct) {
        isCorrect = correct;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("Answer");
        sb.append("{answerText='").append(answerText).append('\'');
        sb.append(", answerCsddId='").append(answerCsddId).append('\'');
        sb.append(", answerHash='").append(answerHash).append('\'');
        sb.append(", isCorrect=").append(isCorrect);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Answer answer = (Answer) o;

        if (answerCsddId != null ? !answerCsddId.equals(answer.answerCsddId) : answer.answerCsddId != null)
            return false;
        if (answerHash != null ? !answerHash.equals(answer.answerHash) : answer.answerHash != null) return false;
        if (answerText != null ? !answerText.equals(answer.answerText) : answer.answerText != null) return false;
        if (isCorrect != null ? !isCorrect.equals(answer.isCorrect) : answer.isCorrect != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = answerText != null ? answerText.hashCode() : 0;
        result = 31 * result + (answerCsddId != null ? answerCsddId.hashCode() : 0);
        result = 31 * result + (answerHash != null ? answerHash.hashCode() : 0);
        result = 31 * result + (isCorrect != null ? isCorrect.hashCode() : 0);
        return result;
    }
}

