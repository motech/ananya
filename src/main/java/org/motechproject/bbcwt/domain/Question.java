package org.motechproject.bbcwt.domain;

import org.ektorp.support.TypeDiscriminator;
import org.motechproject.bbcwt.util.UUIDUtil;

@TypeDiscriminator("doc.documentType == 'Question'")
public class Question extends BaseCouchEntity {
    private int number;

    private String questionLocation;
    private int correctOption;
    private String correctAnswerExplanationLocation;
    private String incorrectAnswerExplanationLocation;

    public Question() {
    }

    public Question(int number, String questionLocation, int correctOption, String correctAnswerExplanationLocation, String incorrectAnswerExplanationLocation) {
        this.number = number;
        this.questionLocation = questionLocation;
        this.correctOption = correctOption;
        this.correctAnswerExplanationLocation = correctAnswerExplanationLocation;
        this.incorrectAnswerExplanationLocation = incorrectAnswerExplanationLocation;
        this.setId(UUIDUtil.newUUID());
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getQuestionLocation() {
        return questionLocation;
    }

    public void setQuestionLocation(String questionLocation) {
        this.questionLocation = questionLocation;
    }

    public int getCorrectOption() {
        return correctOption;
    }

    public void setCorrectOption(int correctOption) {
        this.correctOption = correctOption;
    }

    public String getCorrectAnswerExplanationLocation() {
        return correctAnswerExplanationLocation;
    }

    public void setCorrectAnswerExplanationLocation(String correctAnswerExplanationLocation) {
        this.correctAnswerExplanationLocation = correctAnswerExplanationLocation;
    }

    public String getIncorrectAnswerExplanationLocation() {
        return incorrectAnswerExplanationLocation;
    }

    public void setIncorrectAnswerExplanationLocation(String incorrectAnswerExplanationLocation) {
        this.incorrectAnswerExplanationLocation = incorrectAnswerExplanationLocation;
    }
}