var CourseState = function() {
    this.setState = function(chapterIndex, lessonOrQuestionIndex, currentQuestionResponse, interaction) {
        this.chapterIndex = chapterIndex;
        this.lessonOrQuestionIndex = lessonOrQuestionIndex;
        this.currentQuestionResponse = currentQuestionResponse;
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
    this.setState(null, null, null, null);
};

