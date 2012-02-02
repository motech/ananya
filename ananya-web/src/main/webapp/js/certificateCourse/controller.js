var CertificateCourse = function () {
};

CertificateCourse.interactions = new Array();

var CertificateCourseController = function(course, metadata) {
    this.init = function(course, metadata) {
        this.promptContext = new PromptContext(metadata);
        this.courseState = new CourseState();
        this.initializeInteractionsArray(metadata, course, this.courseState);
        this.setInteraction(CertificateCourse.interactions["welcome"]);
    };

    this.initializeInteractionsArray = function(metadata, course, courseState) {
        CertificateCourse.interactions["welcome"] = new WelcomeInteraction(metadata, course);
        CertificateCourse.interactions["startCourseOption"] = new StartCourseOption(metadata, course);
        CertificateCourse.interactions["startNextChapter"] = new StartNextChapter(metadata, course, courseState)
        CertificateCourse.interactions["lesson"] = new LessonInteraction(metadata, course);
        CertificateCourse.interactions["endOfCourse"] = {};
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

    this.playingDone = function() {
        this.setInteraction(this.interaction.nextInteraction());
    };

    this.setInteraction = function(interaction) {
        this.interaction = interaction;
        this.quietlyProcessTillAPhoneInteractionIsNeeded();
    };

    this.quietlyProcessTillAPhoneInteractionIsNeeded = function() {
        while(this.interaction && this.interaction.processSilentlyAndReturnNextState) {
            this.interaction = this.interaction.processSilentlyAndReturnNextState();
        }
    }

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

