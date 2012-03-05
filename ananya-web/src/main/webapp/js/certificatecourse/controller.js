var CertificateCourse = function () {
};

CertificateCourse.interactions = new Array();

var CertificateCourseController = function(course, metaData, courseState, dataTransferList) {
    this.init = function(course, metaData, dataTransferList) {
        this.promptContext = new PromptContext(metaData);
        this.courseState = courseState;
        this.dataTransferList = dataTransferList;
        this.initializeInteractionsArray(metaData, course, this.courseState);
        this.setInteraction(CertificateCourse.interactions[this.courseState.interactionKey].resumeCall());
    };

    //TODO: This should be pulled out.
    this.initializeInteractionsArray = function(metaData, course, courseState) {
        CertificateCourse.interactions[StartCertificationCourse.KEY] = new StartCertificationCourse(metaData, course, courseState);
        CertificateCourse.interactions[StartNextChapter.KEY] = new StartNextChapter(metaData, course, courseState);
        CertificateCourse.interactions[LessonInteraction.KEY] = new LessonInteraction(metaData, course, courseState);
        CertificateCourse.interactions[LessonEndMenuInteraction.KEY] = new LessonEndMenuInteraction(metaData, course, courseState);
        CertificateCourse.interactions[StartQuizInteraction.KEY] = new StartQuizInteraction(metaData, course, courseState);
        CertificateCourse.interactions[PoseQuestionInteraction.KEY] = new PoseQuestionInteraction(metaData, course, courseState);
        CertificateCourse.interactions[PlayAnswerExplanationInteraction.KEY] = new PlayAnswerExplanationInteraction(metaData, course, courseState);
        CertificateCourse.interactions[ReportChapterScoreInteraction.KEY] = new ReportChapterScoreInteraction(metaData, course, courseState);
        CertificateCourse.interactions[EndOfChapterMenuInteraction.KEY] = new EndOfChapterMenuInteraction(metaData, course, courseState);
        CertificateCourse.interactions[PlayThanksInteraction.KEY] = new PlayThanksInteraction(metaData, course, courseState);
        CertificateCourse.interactions[PlayFinalScoreInteraction.KEY] = new PlayFinalScoreInteraction(metaData, course, courseState);
        CertificateCourse.interactions[PlayCourseResultInteraction.KEY] = new PlayCourseResultInteraction(metaData, course, courseState);
        CertificateCourse.interactions[CourseEndMarkerInteraction.KEY] = new CourseEndMarkerInteraction(metaData, course, courseState);
        CertificateCourse.interactions[EndOfCourseInteraction.KEY] = new EndOfCourseInteraction();
    };

    this.playAudio = function() {
        return this.interaction.playAudio();
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
            this.processCurrentInteractionIntoPostQ();
            this.interaction = this.interaction.processSilentlyAndReturnNextState();
        }
        this.processCurrentInteractionIntoPostQ();
    };

    this.processCurrentInteractionIntoPostQ = function() {
        if(this.currentInteractionIsBookMarkable()) {
            this.courseState.setCourseStateForServerCall(this.interaction.getCourseType(),
                this.interaction.getInteractionKey(), this.interaction.getCourseItemState(), this.interaction.shouldLog());
            this.dataTransferList.add(this.courseState.toJson(), DataTransferList.TYPE_CC_STATE);
        }
    }

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
                nextInteraction = new InvalidInputInteraction(this.interaction, metaData);
            }
        }
        this.setInteraction(nextInteraction);
    };

    this.callDisconnected = function() {
        var exitInteraction = {exit:function(){return true;}}
        this.setInteraction(exitInteraction);
    }

    this.init(course, metaData, dataTransferList);
};

