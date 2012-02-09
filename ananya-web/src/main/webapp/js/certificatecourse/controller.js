var CertificateCourse = function () {
};

CertificateCourse.interactions = new Array();

var CertificateCourseController = function(course, metadata) {
    this.init = function(course, metadata) {
        this.promptContext = new PromptContext(metadata);
        this.courseState = new CourseState();
        this.initializeInteractionsArray(metadata, course, this.courseState);
        this.dataTransferList = new DataTransferList();
        this.setInteraction(CertificateCourse.interactions[WelcomeInteraction.KEY]);
    };

    //TODO: This should be pulled out.
    this.initializeInteractionsArray = function(metadata, course, courseState) {
        CertificateCourse.interactions[WelcomeInteraction.KEY] = new WelcomeInteraction(metadata, course);
        CertificateCourse.interactions[StartCourseOption.KEY] = new StartCourseOption(metadata, course);
        CertificateCourse.interactions[StartNextChapter.KEY] = new StartNextChapter(metadata, course, courseState)
        CertificateCourse.interactions[LessonInteraction.KEY] = new LessonInteraction(metadata, course, courseState);
        CertificateCourse.interactions[LessonEndMenuInteraction.KEY] = new LessonEndMenuInteraction(metadata, course, courseState);
        CertificateCourse.interactions[StartQuizInteraction.KEY] = new StartQuizInteraction(metadata, course, courseState);
        CertificateCourse.interactions[PoseQuestionInteraction.KEY] = new PoseQuestionInteraction(metadata, course, courseState);
        CertificateCourse.interactions[PlayAnswerExplanationInteraction.KEY] = new PlayAnswerExplanationInteraction(metadata, course, courseState);
        CertificateCourse.interactions[ReportChapterScoreInteraction.KEY] = new ReportChapterScoreInteraction(metadata, course, courseState);
        CertificateCourse.interactions[EndOfChapterMenuInteraction.KEY] = new EndOfChapterMenuInteraction(metadata, course, courseState);
        CertificateCourse.interactions["endOfCourse"] = {disconnect:function(){return true;}};
    };

    this.playAudio = function() {
        return this.interaction.playAudio();
    };

    this.nextAction = function() {
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

    this.dataToPost = function() {
        var stringifiedDataToPost = Utility.stringify(this.dataTransferList.transferList);
        return stringifiedDataToPost;
    };

    this.anyDataToPost = function() {
        return this.dataTransferList.size() > 0;
    }

    this.dataPostSuccessful = function() {
        this.dataTransferList.drain();
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
            this.dataTransferList.add(this.courseState.toJson());
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
    }

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
    }

    this.init(course, metadata);
};

