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
    }


}


