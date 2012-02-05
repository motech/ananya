var EntryController = function(callerData) {

    var callerData = callerData;

    this.decideFlowForCertificateCourse = function() {
        if (callerData.isRegistered == "false")
            return "#unregistered";
        else if (callerData.isRegistered == "true" && callerData.bookmark.type)
            return "#registered_bookmark_present";
        else
            return "#registered_bookmark_absent";
    };

    this.decideFlowForJobAid = function() {
        if (callerData.isRegistered == "false")
            return "#unregistered";
        else
            return "#registered";
    };
};
