var EntryController = function(callerData) {

    var callerData = callerData;

    this.decideFlowForCertificateCourse = function() {
        if (!this.isCallerRegistered())
            return "#unregistered";
        else if (this.isCallerRegistered() && callerData.bookmark.type)
            return "#registered_bookmark_present";
        else
            return "#registered_bookmark_absent";
    };

    this.decideFlowForJobAid = function() {
        if (this.isCallerRegistered())
            return "#registered";
        else
            return "#unregistered";
    };

    this.isCallerRegistered = function() {
        return callerData.isRegistered == "true";
    };
};
