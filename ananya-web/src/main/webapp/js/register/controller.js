var RegisterController = function(metadata) {

    var metadata = metadata;
    var count = 0;
    var records = [];
    var fields = ["designation", "name", "district", "block", "village"];

    this.nextField = function() {
        return fields[count];
    };

    this.capture = function(record) {
        records[count] = record;
        count++;
    };

    this.allCaptured = function() {
        return count >= fields.length;
    };

    this.getGrammar = function(field, record) {
        return metadata["grammar.url"] + metadata["grammar.title." + field] + record + ".grxml";
    };

    this.getPrompt = function(field) {
        return metadata["audio.url"] + metadata["register." + field + ".say"];
    };

    this.getConfirmPrompt = function(field) {
        return metadata["audio.url"] + metadata["register." + field + ".confirm"];
    };

    this.getNoInputPrompt = function(field) {
        return metadata["audio.url"] + metadata["register." + field + ".noinput"];
    };

    this.getRerecordPrompt = function(field) {
        return metadata["audio.url"] + metadata["register." + field + ".rerecord"];
    };

    this.isVoiceRecognised = function(field) {
        return field != "name" && field != "designation";
    };

    this.submitUrl = function() {
        return metadata["web.url"] + "/flw/register";
    };

    this.nextFlow = function(calledNumber) {
        if (metadata["certificatecourse.application.number"] == calledNumber)
            return metadata["web.url"] + "/vxml/flwcc.vxml";
        else
            return metadata["web.url"] + "/vxml/jobaid.vxml";
    };

};

