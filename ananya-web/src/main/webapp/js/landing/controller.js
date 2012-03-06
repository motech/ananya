var LandingController = function(metadata) {

    var metadata = metadata;

    this.jobAidEnterURL = function() {
        return metadata["url.version"] + "/vxml/jobaid_enter.vxml";
    };

    this.certificateEnterURL = function() {
        return metadata["url.version"] + "/vxml/certificatecourse_enter.vxml"
    };

}