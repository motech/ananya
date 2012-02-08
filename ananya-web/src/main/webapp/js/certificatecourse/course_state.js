var CourseState = function() {
    this.setState = function(chapterIndex, lessonOrQuestionIndex, currentQuestionResponse, isAnswerCorrect, interactionKey) {
        this.chapterIndex = chapterIndex;
        this.lessonOrQuestionIndex = lessonOrQuestionIndex;
        this.currentQuestionResponse = currentQuestionResponse;
        this.isAnswerCorrect = isAnswerCorrect;
        this.interactionKey = interactionKey;
        this.scoresByChapter = {};
    };

    this.setChapterIndex = function(chapterIndex) {
        this.chapterIndex = chapterIndex;
    };

    this.setLessonOrQuestionIndex = function(lessonOrQuestionIndex) {
        this.lessonOrQuestionIndex = lessonOrQuestionIndex;
    };

    this.setCurrentQuestionResponse = function(currentQuestionResponse) {
        this.currentQuestionResponse = currentQuestionResponse;
    };

    this.setAnswerCorrect = function(isAnswerCorrect) {
        this.isAnswerCorrect = isAnswerCorrect;
    };

    this.setScoresByChapter = function(scoresByChapter) {
        this.scoresByChapter = scoresByChapter;
    };

    this.setInteractionKey = function(interactionKey) {
        this.interactionKey = interactionKey;
    };

    this.toJson = function() {
        return {
            "chapterIndex" : this.chapterIndex,
            "lessonOrQuestionIndex" : this.lessonOrQuestionIndex,
            "questionResponse" : this.currentQuestionResponse,
            "result" : this.isAnswerCorrect,
            "interactionKey": this.interactionKey
        };
    };

    this.setState(null, null, null, null, null);
};

