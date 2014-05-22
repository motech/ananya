package org.motechproject.ananya.domain;

import ch.lambdaj.group.Group;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import java.util.*;

import static ch.lambdaj.Lambda.filter;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.group.Groups.by;
import static ch.lambdaj.group.Groups.group;
import static org.apache.commons.lang3.ObjectUtils.compare;
import static org.apache.commons.lang3.ObjectUtils.max;

public class ReportCard implements Comparable<ReportCard>{
    @JsonProperty
    private List<Score> scores;
    @JsonIgnore
    private Score latestScore;

    public ReportCard() {
        scores = new ArrayList<Score>();
    }

    public void addScore(Score score) {
        Object matchedScore = CollectionUtils.find(scores, Score.findByChapterIdAndQuestionId(score.chapterIndex(), score.questionIndex()));
        scores.remove(matchedScore);
        scores.add(score);
    }

    public void clearAllScores() {
        scores.clear();
    }

    public void clearScoresForChapterIndex(String chapterIndex) {
        Collection scoresForChapterIndex = CollectionUtils.select(scores, Score.findByChapterId(chapterIndex));
        scores.removeAll(scoresForChapterIndex);
    }

    public List<Score> scores() {
        return Collections.unmodifiableList(scores);
    }
    
    public Score getlatestScore(){
    	List<Score> scores = scores();
    	if(scores==null || scores.isEmpty())
    		return new Score("0", "0", false);
    	latestScore = scores.get(0);
    	for(Score score: scores){
    			latestScore = max(score, latestScore);
    	}
    	return latestScore;
    }

    public Map<String, Integer> scoresByChapterIndex() {
        Map<String, Integer> scoresByChapterIndex = new HashMap();
        Group<Score> groupByChapter = group(scores(), by(on(Score.class).chapterIndex()));

        for (String chapter : groupByChapter.keySet()) {
            final List<Score> scoresForChapter = groupByChapter.find(chapter);
            List<Score> correctAnswers = filter(new CorrectAnswerMatcher(), scoresForChapter);
            scoresByChapterIndex.put(chapter, correctAnswers.size());
        }
        return scoresByChapterIndex;
    }
    
    public Collection<Score> getScoresForChapter(String chapterIndex) {
        return CollectionUtils.select(scores, Score.findByChapterId(chapterIndex));
    }

    public Integer totalScore() {
        Collection<Integer> scores = scoresByChapterIndex().values();
        int totalScore = 0;
        for (Integer score : scores)
            totalScore += score;
        return totalScore;
    }

    @Override
    public int compareTo(ReportCard other) {
        return compare(this.totalScore(), other.totalScore());
    }


    private class CorrectAnswerMatcher extends BaseMatcher<Score> {
        @Override
        public void describeTo(Description description) {
        }

        @Override
        public boolean matches(Object o) {
            Score scoreToMatch = (Score) o;
            return scoreToMatch.result();
        }
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}


