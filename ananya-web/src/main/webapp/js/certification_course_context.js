var CertificationCourseContext = function(course, metadata) {
    this.init = function(course, metadata) {
        Course.buildLinks(course);
        this.course = course;
        this.currentInteraction = course;
        this.metadata = metadata;
        this.bookmark = null;

        this.hasFinishedLastQuizOfChapter = false;
        this.hasFinishedLastLessonOfChapter = false;
    };

    this.isAtCourseRoot = function() {
        return this.currentInteraction == course;
    };

    this.navigateToBookmark = function(bookmark) {
        if(bookmark.type == "lesson"){
            this.currentInteraction = this.course.children[bookmark.chapterIndex].children[bookmark.lessonIndex];
        }
    };

    this.isAtQuizHeader = function() {
        return this.hasFinishedLastLessonOfChapter;
    };

    this.quizHeaderFinished = function() {
        this.hasFinishedLastLessonOfChapter = false;
    };

    this.scoreReportFinished = function() {
        this.hasFinishedLastLessonOfChapter = false;
        this.hasFinishedLastQuizOfChapter = false;
        this.currentInteraction = this.currentInteraction.siblingOnRight.children[0];
    };

    this.restartChapter = function() {
        this.hasFinishedLastLessonOfChapter = false;
        this.hasFinishedLastQuizOfChapter = false;
        this.currentInteraction = this.currentInteraction.children[0];
    };

    this.welcomeFinished = function() {
        this.currentInteraction  = this.course.children[0].children[0];
    };

    this.chapterFinished = function() {
        return this.hasFinishedLastQuizOfChapter;
    };

    this.isAtLesson = function() {
        return this.currentInteraction.data.type == "lesson";
    };

    this.isAtQuizQuestion = function() {
        return this.currentInteraction.data.type == "quiz";
    };

    this.courseWelcomeMessage = function() {
        return this.findAudio(this.currentInteraction, "introduction");
    };

    this.currentInteractionLesson = function() {
        return this.findAudio(this.currentInteraction, "lesson");
    };

    this.currentInteractionQuizHeader = function() {
        return this.findAudio(this.currentInteraction.parent, "quizHeader");
    };

    this.currentInteractionQuizQuestion = function() {
        return this.findAudio(this.currentInteraction, "question");
    };

    this.currentInteractionMenu = function() {
        return this.findAudio(this.currentInteraction, "menu");
    };

    this.lessonOrQuizFinished = function() {
        var isAtLastLessonOfChapter = this.isAtLesson() && this.currentInteraction.siblingOnRight.data.type == "quiz";
        if(isAtLastLessonOfChapter){
            this.hasFinishedLastLessonOfChapter = true;
        }
        var lastChildOfMyParent =  this.currentInteraction.parent.children[this.currentInteraction.parent.children.length-1];
        var isAtLastQuizOfChapter = lastChildOfMyParent == this.currentInteraction;
        if (isAtLastQuizOfChapter) {
            this.hasFinishedLastQuizOfChapter = true;
            this.currentInteraction = this.currentInteraction.parent;
        }
        else {
            this.currentInteraction = this.currentInteraction.siblingOnRight;
        }
    };

    this.addAfterLessonBookmark = function() {
        if (this.hasFinishedLastLessonOfChapter) {
            this.bookmark = {
                "type" : "quizHeader",
                "chapterIndex" : "" + this.currentInteraction.parent.positionIndex
            };
        }
        else {
            this.bookmark = this.lessonBookmark();
        }
    }

    this.addAfterWelcomeMessageBookmark = function(){
        this.bookmark = this.lessonBookmark();
    }

    this.lessonBookmark = function() {
        return {
            "type" : "lesson",
            "chapterIndex" : "" + this.currentInteraction.parent.positionIndex ,
            "lessonIndex" : "" + this.currentInteraction.positionIndex
        };
    }

    this.shouldSaveBookmark = function() {
        return this.bookmark != null && this.metadata.shouldSaveBookmark == "true";
    };

    this.evaluateAndReturnAnswerExplanation = function(input) {
        if(this.currentInteraction.data.correctAnswer == input) {
            return this.findAudio(this.currentInteraction, "correct");
        }
        return this.findAudio(this.currentInteraction, "incorrect");
    };

    this.findContentByName = function(interactionToUse, contentName) {
        var contents = interactionToUse.contents;
        var contentLength = contents.length
        for(i = 0; i< contentLength; i++){
            if(contents[i].name == contentName)
                return contents[i];
        }
        return undefined;
    };

    this.findAudio = function(interactionToUse, contentName) {
        return this.audioFileBase() + this.findContentByName(interactionToUse, contentName).value;
    };

    this.audioFileBase = function() {
        return this.metadata.audioFileBase;
    };

    this.init(course, metadata);
};