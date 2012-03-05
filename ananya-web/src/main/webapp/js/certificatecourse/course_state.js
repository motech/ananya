
var CourseState = function(callerData, courseData) {
    this.init = function(callerData, courseData){
        if(!callerData) {
            callerData = {"bookmark":{}, "scoresByChapter":{}};
        }
        this.defineStateVars();

        var bookmark = callerData.bookmark;

        this.courseData = courseData;
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
        this.interactionKey = StartCertificationCourse.KEY;
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

    this.contentFunctions = { "parent" : this };

    this.contentFunctions[CourseType.COURSE] = function() { return this["parent"].courseData };
    this.contentFunctions[CourseType.CHAPTER] = function()
        { return this["parent"].courseData.children[this["parent"].chapterIndex] };
    this.contentFunctions[CourseType.LESSON] = function()
        { return this["parent"].courseData.children[this["parent"].chapterIndex].children[this["parent"].lessonOrQuestionIndex] };
    this.contentFunctions[CourseType.QUIZ] = function()
        { return this["parent"].courseData.children[this["parent"].chapterIndex] };

    this.setCourseStateForServerCall = function(contentType, interactionKey, courseItemState, shouldLog) {
        this.interactionKey = interactionKey;
        this.contentType = contentType;
        this.courseItemState = courseItemState;

        if (shouldLog && this.contentFunctions[this.contentType]) {
            var content = this.contentFunctions[this.contentType]();
            this.contentId = content.id;
            this.contentName = content.name;
        } else {
            this.contentId = null;
            this.contentName = null;
        }
    }

    this.getStateData = function() {
        return (this.interactionKey == ReportChapterScoreInteraction.KEY)
            ? String(this.scoresByChapter[this.chapterIndex]) : null;
    }

    this.toJson = function() {

        return {
            "chapterIndex" : this.chapterIndex,
            "lessonOrQuestionIndex" : this.lessonOrQuestionIndex,
            "questionResponse" : this.currentQuestionResponse,
            "result" : this.isAnswerCorrect,
            "interactionKey": this.interactionKey,

            "contentId" : this.contentId,
            "contentName" : this.contentName,
            "contentType" : this.contentType,
            "courseItemState" : this.courseItemState,
            "contentData" : this.getStateData(),
            "time" : Utility.toISODateString(new Date()),
            "certificateCourseId": ""
        };
    };

    this.init(callerData, courseData);

};

CourseState.START = "start";
CourseState.END = "end";

var CourseType = function() {};

CourseType.COURSE = "course";
CourseType.CHAPTER = "chapter";
CourseType.LESSON = "lesson";
CourseType.QUIZ = "quiz";