var LandingController = function(metadata) {

    var metadata = metadata;

    this.jobAidURL = function() {
        return metadata["url.version"] + "/vxml/jobaid.vxml";
    };

    this.certificateEnterURL = function() {
        return metadata["url.version"] + "/vxml/certificatecourse_enter.vxml"
    };

}