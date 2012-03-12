describe("Controller for certificate/jobaid course entry", function () {
    var controller;

    it("should return 'registered_bookmark_present' form for registered caller with bookmark", function() {
        var callerData = {
            "isRegistered" : "true",
            "bookmark" : {"type":"AA"}
        };
        controller = new EntryController(callerData);
        expect(controller.decideFlowForCertificateCourse()).toEqual("#registered_bookmark_present");
    });

    it("should return 'registered_bookmark_absent' form for registered caller without bookmark", function() {
        var callerData = {
            "isRegistered" : "true",
            "bookmark" : "{}"
        };
        controller = new EntryController(callerData);
        expect(controller.decideFlowForCertificateCourse()).toEqual("#registered_bookmark_absent");
    });

    it("should return 'unregistered' form for unregistered caller", function() {
        var callerData = {
            "isRegistered" : "false"
        };

        var metadata = {'usage.general' : 50};
        controller = new EntryController(callerData, metadata);
        expect(controller.decideFlowForCertificateCourse()).toEqual("#unregistered");
    });

    it("should tell if the caller is registered", function () {
        var registeredCaller = {
            "isRegistered" : "true",
            "bookmark" : "{}"
        };
        controller = new JobAidController(registeredCaller);
        expect(controller.isCallerRegistered()).toEqual(true);

        var unRegisteredCaller = {
            "isRegistered" : "false",
            "bookmark" : "{}"
        };
        controller = new JobAidController(unRegisteredCaller);
        expect(controller.isCallerRegistered()).toEqual(false);
    });
});
