describe("Landing Controller", function () {

    var controller;
    var metadata;

    it("should return jobaid enter URL", function() {
         metadata = {"url.version" : "v1.1"};
         controller = new LandingController(metadata);

         expect(controller.jobAidEnterURL()).toEqual("../v1.1/vxml/jobaid_enter.vxml")
    });

    it("should return certificate enter URL", function() {
         metadata = {"url.version" : "v1.2"};
         controller = new LandingController(metadata);

         expect(controller.certificateEnterURL()).toEqual("../v1.2/vxml/certificatecourse_enter.vxml")
    });

});