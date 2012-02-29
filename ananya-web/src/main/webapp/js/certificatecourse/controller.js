var CertificateCourse = function () {
};

CertificateCourse.interactions = new Array();

var CertificateCourseController = function(course, metadata, courseState, dataTransferList, pathToRoot) {
    this.init = function(course, metadata, dataTransferList, pathToRoot) {
        this.promptContext = new PromptContext(metadata);
        this.courseState = courseState;
        this.dataTransferList = dataTransferList;
        this.initializeInteractionsArray(metadata, course, this.courseState);
        this.setInteraction(CertificateCourse.interactions[this.courseState.interactionKey].resumeCall());
        this.pathToRoot = pathToRoot;
    };

    //TODO: This should be pulled out.
    this.initializeInteractionsArray = function(metadata, course, courseState) {
        CertificateCourse.interactions[StartNextChapter.KEY] = new StartNextChapter(metadata, course, courseState);
        CertificateCourse.interactions[LessonInteraction.KEY] = new LessonInteraction(metadata, course, courseState);
        CertificateCourse.interactions[LessonEndMenuInteraction.KEY] = new LessonEndMenuInteraction(metadata, course, courseState);
        CertificateCourse.interactions[StartQuizInteraction.KEY] = new StartQuizInteraction(metadata, course, courseState);
        CertificateCourse.interactions[PoseQuestionInteraction.KEY] = new PoseQuestionInteraction(metadata, course, courseState);
        CertificateCourse.interactions[PlayAnswerExplanationInteraction.KEY] = new PlayAnswerExplanationInteraction(metadata, course, courseState);
        CertificateCourse.interactions[ReportChapterScoreInteraction.KEY] = new ReportChapterScoreInteraction(metadata, course, courseState);
        CertificateCourse.interactions[EndOfChapterMenuInteraction.KEY] = new EndOfChapterMenuInteraction(metadata, course, courseState);
        CertificateCourse.interactions[PlayThanksInteraction.KEY] = new PlayThanksInteraction(metadata, course, courseState);
        CertificateCourse.interactions[PlayFinalScoreInteraction.KEY] = new PlayFinalScoreInteraction(metadata, course, courseState);
        CertificateCourse.interactions[PlayCourseResultInteraction.KEY] = new PlayCourseResultInteraction(metadata, course, courseState);
        CertificateCourse.interactions[CourseEndMarkerInteraction.KEY] = new CourseEndMarkerInteraction(metadata, course, courseState);
        CertificateCourse.interactions["endOfCourse"] = new EndOfCourseInteraction();
    };

    this.playAudio = function() {
        return Utility.resourceUrl(pathToRoot,this.interaction.playAudio());
    };

    this.nextAction = function() {
        if(this.interaction.exit && this.interaction.exit()) {
            return "#exit";
        }
        if(this.interaction.disconnect && this.interaction.disconnect()) {
            return "#disconnect";
        }
        if(this.interaction.doesTakeInput()) {
            return "#collectInput";
        }
        else {
            return "#playAudio";
        }
    };

    this.playingDone = function() {
        this.setInteraction(this.interaction.nextInteraction());
    };

    this.setInteraction = function(interaction) {
        this.interaction = interaction;
        this.quietlyProcessTillAPhoneInteractionIsNeeded();
    };

    this.quietlyProcessTillAPhoneInteractionIsNeeded = function() {
        while(this.currentInteractionHasToBeProcessedSilently()) {
            this.interaction = this.interaction.processSilentlyAndReturnNextState();
        }
        if(this.currentInteractionIsBookMarkable()) {
            this.courseState.setInteractionKey(this.interaction.getInteractionKey());
            this.dataTransferList.add(this.courseState.toJson(), DataTransferList.TYPE_CC_STATE);
        }
    };

    this.currentInteractionIsBookMarkable = function() {
        return this.interaction && this.interaction.getInteractionKey;
    };

    this.currentInteractionHasToBeProcessedSilently = function() {
        return this.interaction && this.interaction.processSilentlyAndReturnNextState;
    };

    this.gotNoInput = function() {
        this.promptContext.gotNoInput();
        if(this.promptContext.hasExceededMaxNoInputs())
        {
            this.promptContext.resetCounts();
            this.setInteraction(this.interaction.continueWithoutInput());
        }
    };

    this.processInput = function(input) {
        var nextInteraction;
        if(this.interaction.validateInput(input)) {
            this.promptContext.resetCounts();
            nextInteraction = this.interaction.processInputAndReturnNextInteraction(input);
        }
        else {
            this.promptContext.gotInvalidInput();
            if(this.promptContext.hasExceededMaxInvalidInputs()) {
                nextInteraction = {
                                    disconnect : function() {
                                                    return true;
                                                  }
                                  };
            }
            else {
                nextInteraction = new InvalidInputInteraction(this.interaction, metadata);
            }
        }
        this.setInteraction(nextInteraction);
    };

    this.callDisconnected = function() {
        var exitInteraction = {exit:function(){return true;}}
        this.setInteraction(exitInteraction);
    }

    this.init(course, metadata, dataTransferList, pathToRoot);
};

