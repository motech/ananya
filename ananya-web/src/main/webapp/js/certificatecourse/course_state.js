var CourseState = function() {
    this.setState = function(chapterIndex, lessonOrQuestionIndex, currentQuestionResponse, isAnswerCorrect, interaction) {
        this.chapterIndex = chapterIndex;
        this.lessonOrQuestionIndex = lessonOrQuestionIndex;
        this.currentQuestionResponse = currentQuestionResponse;
        this.isAnswerCorrect = isAnswerCorrect;
        this.interaction = interaction;
    };

    this.setChapterIndex = function(chapterIndex) {
        this.chapterIndex = chapterIndex;
    };

    this.setLessonOrQuestionIndex = function(lessonOrQuestionIndex) {
        this.lessonOrQuestionIndex = lessonOrQuestionIndex;
    };

    this.setCurrentQuestionResponse = function(currentQuestionResponse) {
        this.currentQuestionResponse = currentQuestionResponse;
    }

    this.setAnswerCorrect = function(isAnswerCorrect) {
        this.isAnswerCorrect = isAnswerCorrect;
    }
    this.setState(null, null, null, null, null);
};

