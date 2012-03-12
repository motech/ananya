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
});
