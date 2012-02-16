var CourseState = function(callerData) {
    this.init = function(callerData){
        if(!callerData) {
            callerData = {"bookmark":{}, "scoresByChapter":{}};
        }
        this.defineStateVars();

        var bookmark = callerData.bookmark;

        this.scoresByChapter = callerData.scoresByChapter;

        if(bookmark && bookmark.type) {
            if(bookmark.chapterIndex!=null) {
                this.chapterIndex = bookmark.chapterIndex;
            }
            if(bookmark.lessonIndex!=null) {
                this.lessonOrQuestionIndex = bookmark.lessonIndex;
            }
            this.interactionKey = bookmark.type;
        }
    }

    this.defineStateVars = function() {
        this.chapterIndex = null;
        this.lessonOrQuestionIndex = null;
        this.currentQuestionResponse = null;
        this.isAnswerCorrect = null;
        this.interactionKey = StartNextChapter.KEY;
        this.scoresByChapter = null;
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

    this.init(callerData);
};

