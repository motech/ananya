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

    it("should return 'unregistered' form for unregistered caller in jobaid entry", function() {
        var callerData = {
            "isRegistered" : "false"
        };
        var metadata = {'usage.general' : 50};
        controller = new EntryController(callerData, metadata);
        expect(controller.decideFlowForJobAid()).toEqual("#partially_registered");
    });

    it("should return 'registered' form for registered caller in jobaid entry", function() {
        var callerData = {
            "isRegistered" : "true",
            "bookmark" : "{}"
        };
        controller = new EntryController(callerData);
        expect(controller.decideFlowForJobAid()).toEqual("#registered");
    });

    it("should tell if the caller is registered", function () {
        var registeredCaller = {
            "isRegistered" : "true",
            "bookmark" : "{}"
        };
        controller = new EntryController(registeredCaller);
        expect(controller.isCallerRegistered()).toEqual(true);

        var unRegisteredCaller = {
            "isRegistered" : "false",
            "bookmark" : "{}"
        };
        controller = new EntryController(unRegisteredCaller);
        expect(controller.isCallerRegistered()).toEqual(false);

    });

    it("should return jobaid welcome prompt for registered users", function() {
         var registeredCaller = {};
         var metaData = {
            "audio.url" : "audio/",
            "jobaid.audio.url" : "jobaid/",
            "jobaid.welcome.registered" : "0001_welcome_job_aid_reg.wav"
         };
         controller = new EntryController(registeredCaller , metaData);
         expect(controller.jobAidWelcomePromptRegistered()).toEqual("audio/jobaid/0001_welcome_job_aid_reg.wav")
    });

    it("should return jobaid welcome prompt for partially registered users", function() {
         var registeredCaller = {};
         var metaData = {
            "audio.url" : "audio/",
            "jobaid.audio.url" : "jobaid/",
            "jobaid.welcome.registered" : "0001_welcome_job_aid_not_reg.wav"
         };
         controller = new EntryController(registeredCaller , metaData);
         expect(controller.jobAidWelcomePromptRegistered()).toEqual("audio/jobaid/0001_welcome_job_aid_not_reg.wav")
    });
});
