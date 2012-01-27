package org.motechproject.ananya.domain;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReportCard {
    @JsonProperty
    private List<Score> scores;

    public ReportCard() {
        scores = new ArrayList<Score>();
    }
    public void addScore(Score score) {
        Object matchedScore = CollectionUtils.find(scores, Score.findByChapterIdAndQuestionId(score.chapterIndex(), score.questionIndex()));
        scores.remove(matchedScore);
        scores.add(score);
    }

    public List<Score> scores() {
        return Collections.unmodifiableList(scores);
    }

    public static class Score {
        @JsonProperty
        private String chapterIndex;
        @JsonProperty
        private String questionIndex;
        @JsonProperty
        private boolean result;

        public Score() {

        }

        public Score(String chapterIndex, String questionIndex, Boolean result) {
            this.chapterIndex = chapterIndex;
            this.questionIndex = questionIndex;
            this.result = result;
        }

        public String questionIndex() {
            return questionIndex;
        }

        public boolean result() {
            return result;
        }

        public String chapterIndex() {
            return chapterIndex;
        }

        public static Predicate findByChapterIdAndQuestionId(final String chapterIndex, final String questionIndex) {
            return new Predicate() {
                @Override
                public boolean evaluate(Object o) {
                    Score score = (Score) o;
                    return score.chapterIndex().equals(chapterIndex) && score.questionIndex().equals(questionIndex);
                }
            };
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Score score = (Score) o;

            if (result != score.result) return false;
            if (chapterIndex != null ? !chapterIndex.equals(score.chapterIndex) : score.chapterIndex != null)
                return false;
            if (questionIndex != null ? !questionIndex.equals(score.questionIndex) : score.questionIndex != null)
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result1 = chapterIndex != null ? chapterIndex.hashCode() : 0;
            result1 = 31 * result1 + (questionIndex != null ? questionIndex.hashCode() : 0);
            result1 = 31 * result1 + (result ? 1 : 0);
            return result1;
        }
    }


}

