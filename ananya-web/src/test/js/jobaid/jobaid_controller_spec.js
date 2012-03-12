describe("Controller for jobaid ", function () {
    var controller;

    it("should return 'unregistered' form for unregistered caller in jobaid", function() {
        var callerData = {
            "isRegistered" : "false"
        };
        var metadata = {'usage.general' : 50};
        controller = new JobAidController(callerData, metadata);
        expect(controller.decideFlowForJobAid()).toEqual("#partially_registered");
    });

    it("should return 'registered' form for registered caller in jobaid", function() {
        var callerData = {
            "isRegistered" : "true"
        };
        controller = new JobAidController(callerData);
        expect(controller.decideFlowForJobAid()).toEqual("#registered");
    });

    it("should return 'max_usage' form for registered caller in jobaid with max usage reached", function() {
        var callerData = {
            "isRegistered" : "true",
            "hasReachedMaxUsageForMonth" : true
        };
        controller = new JobAidController(callerData);
        expect(controller.decideFlowForJobAid()).toEqual("#max_usage");
    });

    it("should tell if the caller is registered", function () {
        var registeredCaller = {
            "isRegistered" : "true",
        };
        controller = new JobAidController(registeredCaller);
        expect(controller.isCallerRegistered()).toEqual(true);

        var unRegisteredCaller = {
            "isRegistered" : "false"
        };
        controller = new JobAidController(unRegisteredCaller);
        expect(controller.isCallerRegistered()).toEqual(false);
    });

    it("should return jobaid welcome prompt for registered users", function() {
         var registeredCaller = {};
         var metaData = {
            "audio.url" : "audio/",
            "jobaid.audio.url" : "jobaid/",
            "jobaid.welcome.registered" : "0001_welcome_job_aid_reg.wav"
         };
         controller = new JobAidController(registeredCaller , metaData);
         expect(controller.jobAidWelcomePromptRegistered()).toEqual("audio/jobaid/0001_welcome_job_aid_reg.wav")
    });

    it("should return jobaid welcome prompt for partially registered users", function() {
         var registeredCaller = {};
         var metaData = {
            "audio.url" : "audio/",
            "jobaid.audio.url" : "jobaid/",
            "jobaid.welcome.registered" : "0001_welcome_job_aid_not_reg.wav"
         };
         controller = new JobAidController(registeredCaller , metaData);
         expect(controller.jobAidWelcomePromptRegistered()).toEqual("audio/jobaid/0001_welcome_job_aid_not_reg.wav")
    });
});
