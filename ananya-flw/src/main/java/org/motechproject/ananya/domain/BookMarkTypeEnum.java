package org.motechproject.ananya.domain;

public enum BookMarkTypeEnum {
	startCertificationCourse(0),
	startNextChapter(1),
	lesson(2),
	lessonEndMenu(3),
	startQuiz(4),
	poseQuestion(5),
	playAnswerExplanation(6),
	reportChapterScore(7),
	endOfChapterMenu(8),
	playThanks(9),
	playFinalScore(10),
	playCourseResult(11),
	endOfCourse(12),
	courseEndMarker(13);

    public final int index;

    BookMarkTypeEnum(int index) {
        this.index = index;
    }
    
    public int getIndex(){
		return index;
    }
}

