var LandingController = function(metadata) {

    var metadata = metadata;

    this.jobAidEnterURL = function() {
        return metadata["url.version"] + "/vxml/jobaid/enter.vxml";
    };

    this.certificateEnterURL = function() {
        return metadata["url.version"] + "/vxml/certificatecourse/enter.vxml"
    };

}