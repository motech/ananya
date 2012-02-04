var Controller = function(callerData) {

    var callerData = callerData;

    this.decideFlow = function() {
        if (callerData.isRegistered == "false")
            return "#unregistered";
        else if (callerData.isRegistered == "true" && callerData.bookmark.type)
            return "#registered_bookmark_present";
        else
            return "#registered_bookmark_absent";
    };
};
