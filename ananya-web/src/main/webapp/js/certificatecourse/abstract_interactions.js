var AbstractCourseInteraction = function(metadata, interactionKey) {
    this.init = function(metadata, interactionKey) {
        this.metadata = metadata;
        this.interactionDone=false;
        this.interactionKey=interactionKey;
    };

    this.findAudio = function(interactionToUse, contentName) {
        return this.audioFileBase() + this.findContentByName(interactionToUse, contentName).value;
    };

    this.audioFileBase = function() {
       return this.metadata['audio.url']+this.metadata['certificate.audio.url'];
    };

    this.findContentByName = function(interactionToUse, contentName) {
        var contents = interactionToUse.contents;
        var contentLength = contents.length;
        for(i = 0; i< contentLength; i++){
            if(contents[i].name == contentName)
                return contents[i];
        }
        return undefined;
    };

    this.init(metadata, interactionKey);
};

//TODO:Rename this to processAndPlayAudio or something of that sort
AbstractCourseInteraction.prototype.playAudio = function() {
        throw "Please extend playAudio method in your class before starting to call it";
};

AbstractCourseInteraction.prototype.interactionDone = function(){
        this.interactionDone = true;
}
AbstractCourseInteraction.prototype.doesTakeInput = function() {
        throw "Please extend doesTakeInput in your class before starting to call it";
};

AbstractCourseInteraction.prototype.processInput = function(input) {
        throw "Please extend processInput in your class before starting to use it";
};

AbstractCourseInteraction.prototype.validateInput = function(input) {
        throw "Please extend validateInput in your class before starting to use it";
};

AbstractCourseInteraction.prototype.nextInteraction = function() {
        throw "Please extend nextInteraction in your class";
};

AbstractCourseInteraction.prototype.processInputAndReturnNextInteraction = function() {
        throw "Please extend processInputAndReturnNextInteraction in your class";
};


AbstractCourseInteraction.prototype.continueWithoutInput = function() {
        throw "Please extend continueWithoutInput in your class";
};

AbstractCourseInteraction.prototype.resumeCall = function() {
        throw "Please extend resumeCall in your class";
};

AbstractCourseInteraction.prototype.bookMark = function() {

};

AbstractCourseInteraction.prototype.getInteractionKey = function() {
  return this.interactionKey;
};
