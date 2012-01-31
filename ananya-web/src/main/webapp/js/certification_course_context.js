var CertificationCourseContext = function(course, metadata) {
    this.init = function(course, metadata) {
        Course.buildLinks(course);
        this.course = course;
        this.currentInteraction = course;
        this.metadata = metadata;
        this.bookmark = null;
        this.quizResponses = new Array();
        this.hasFinishedLastQuizOfChapter = false;
        this.hasFinishedLastLessonOfChapter = false;
    };

    this.setScoresByChapter = function(scoresByChapter){
        this.scoresByChapter = scoresByChapter;
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
        this.scoresByChapter[this.parentPositionIndex()] = 0;
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
                "chapterIndex" : "" + this.parentPositionIndex()
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
            "chapterIndex" : "" + this.parentPositionIndex() ,
            "lessonIndex" : "" + this.currentInteraction.positionIndex
        };
    }

    this.shouldSaveBookmark = function() {
        return this.bookmark != null && this.metadata.shouldSaveBookmark == "true";
    };

    this.evaluateAndReturnAnswerExplanation = function(input) {
        if(this.currentInteraction.data.correctAnswer == input) {
            this.quizResponses[this.quizResponses.length] = this.scoreReport(input, true);
            this.scoresByChapter[this.parentPositionIndex()]++;
            return this.findAudio(this.currentInteraction, "correct");
        }
        this.quizResponses[this.quizResponses.length] = this.scoreReport(input,false);
        return this.findAudio(this.currentInteraction, "incorrect");
    };

    this.currentChapterScore = function() {
    var chapterScore = 0;
       for(i = 0; i< this.quizResponses.length; i++){
             if(this.quizResponses[i].result == true)
                 chapterScore += 1;
         }
     return chapterScore;
    };

    this.scoreReport = function(response,result){
        return {
            "chapterIndex" : "" + this.parentPositionIndex(),
            "questionIndex" : "" + this.currentInteraction.positionIndex,
            "response" : response,
            "result" : result
        };
    }

    this.findContentByName = function(interactionToUse, contentName) {
        var contents = interactionToUse.contents;
        var contentLength = contents.length
        for(i = 0; i< contentLength; i++){
            if(contents[i].name == contentName)
                return contents[i];
        }
        return undefined;
    };

    this.currentChapterScoreAudio = function(){
        return this.audioFileBase() + this.scoresByChapter[this.currentInteraction.positionIndex] + "_out_of_"+ this.noOfquestionsInCurrentChapter() +".wav";
    };

    this.noOfquestionsInCurrentChapter = function(){
        var childrenOfCurrentChapter = this.currentInteraction.children;
        var noOfChildren = childrenOfCurrentChapter.length;

        var noOfQuestions = 0;
        for(var i = 0; i < noOfChildren; i++) {
            var child = childrenOfCurrentChapter[i];
            if(child.data.type == "quiz") {
                noOfQuestions++;
            }
        }
        
        return noOfQuestions;
    };

    this.parentPositionIndex = function(){
        return this.currentInteraction.parent.positionIndex;
    };

    this.findAudio = function(interactionToUse, contentName) {
        return this.audioFileBase() + this.findContentByName(interactionToUse, contentName).value;
    };

    this.audioFileBase = function() {
        return this.metadata.audioFileBase;
    };

    this.init(course, metadata);
};