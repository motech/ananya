var CourseState = function() {
    this.setState = function(chapterIndex, lessonIndex, questionIndex) {
        this.chapterIndex = chapterIndex;
        this.lessonIndex = lessonIndex;
        this.questionIndex = questionIndex;
    };

    this.setChapterIndex = function(chapterIndex) {
        this.chapterIndex = chapterIndex;
    };

    this.setLessonIndex = function(lessonIndex) {
        this.lessonIndex = lessonIndex;
    };

    this.setQuestionIndex = function(questionIndex) {
        this.questionIndex = questionIndex;
    };
}

