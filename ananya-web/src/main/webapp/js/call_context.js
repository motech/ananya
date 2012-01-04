var CallContext = function(course, metadata) {
    this.init = function(course, metadata) {
        //TODO: should we be validating course and metadata somehow?
        this.currentInteraction = course;
        Course.buildLinks(course);
        this.metadata = metadata;
    };

    this.goToChild = function(childNumber) {
        this.currentInteraction = this.currentInteraction.children[childNumber - 1];
        return this;
    };

    this.isValidChild = function(childNumber) {
        return 0 < childNumber && childNumber <= this.currentInteraction.children.length;
    };

    this.lessonFinished = function() {
        this.currentInteraction = this.currentInteraction.siblingOnRight.parent;
        return this;
    };

    this.isAtALesson = function() {
        return this.currentInteraction.data.lesson?true:false;
    };

    this.currentInteractionIntroduction = function() {
        return this.audioFileBase() + this.currentInteraction.data.introduction;
    };

    this.currentInteractionMenu = function() {
        return this.audioFileBase() + this.currentInteraction.data.menu;
    };

    this.currentInteractionLesson = function() {
        return this.audioFileBase() + this.currentInteraction.data.lesson;
    };

    this.audioFileBase = function() {
        return this.metadata.audioFileBase;
    };

    this.incrementNoInputCount = function() {
        this.noInputCount++;
    }

    this.incrementInvalidInputCount = function() {
        this.invalidInputCount++;
    }

    this.hasExceededMaxNoInputs = function() {
        //TODO: get the count from metadata
        return this.noInputCount > 3;
    }

    this.hasExceededMaxInvalidInputs = function() {
        //TODO: get the count from metadata
        return this.invalidInputCount > 3;
    }

    this.resetPromptCounts = function() {
        this.noInputCount = 0;
        this.invalidInputCount = 0;
        this.tempToControlValidation = 0;
    }

    this.isValidInput = function(input) {
 s       //TODO: decide based on children
        return input < 4;
    }

    this.init(course, metadata);
}