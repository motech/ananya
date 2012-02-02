var CourseState = function() {
    this.setState = function(chapterIndex, lessonOrQuestionIndex, type) {
        this.chapterIndex = chapterIndex;
        this.lessonOrQuestionIndex = lessonOrQuestionIndex;
        this.type = type;
    };

    this.setChapterIndex = function(chapterIndex) {
        this.chapterIndex = chapterIndex;
    };

    this.setLessonOrQuestionIndex = function(lessonOrQuestionIndex) {
        this.lessonOrQuestionIndex = lessonOrQuestionIndex;
    };

    this.setState(null, null, null);
};

