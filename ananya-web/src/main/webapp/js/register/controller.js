var RegisterController = function(metadata) {
    var metadata = metadata;
    var fieldCounter = 0;
    var records = [];
    var fields = ["designation", "name", "district", "block", "panchayat"];

    this.nextField = function() {
        return fields[fieldCounter];
    };

    this.capture = function(record) {
        if (record.resultKey)
            records[fieldCounter] = record.resultKey;
        else
            records[fieldCounter] = record;
        fieldCounter++;
    };

    this.allCaptured = function() {
        return fieldCounter >= fields.length;
    };

    this.playPrompt = function(field) {
        return metadata["audio.url"] + metadata['register.audio.url'] + metadata["register." + field + ".say"];
    };

    this.playBeep = function(field) {
        return metadata["audio.url"] + metadata['register.audio.url'] + metadata['registration.beep.audio'];
    };

    this.playConfirmPrompt = function(field) {
        return metadata["audio.url"] + metadata['register.audio.url'] + metadata["register." + field + ".confirm"];
    };

    this.playNoInputPrompt = function(field) {
        return metadata["audio.url"] + metadata['register.audio.url'] + metadata["register." + field + ".noinput"];
    };

    this.playRerecordPrompt = function(field) {
        return metadata["audio.url"] + metadata['register.audio.url'] + metadata["register." + field + ".rerecord"];
    };

    this.playRegistrationDone = function() {
        return metadata["audio.url"] + metadata['register.audio.url'] + metadata['register.complete'];
    };

    this.isVoiceRecognised = function(field) {
        return field != "name" && field != "designation";
    };

    this.submitUrl = function() {
        return this.absoluteUrl(metadata["register.submit.url"]);
    };

    this.submitNameUrl = function() {
        return  this.absoluteUrl(metadata["register.name.submit.url"]);
    };

    this.absoluteUrl = function(url) {
        var urlStartsWithHttp = (url.indexOf("http:") == 0);
        if (urlStartsWithHttp) {
            return url;
        }
        return metadata["context.path"] + "/" + metadata["url.version"] + "/" + url;
    };

    this.nextFlow = function(calledNumber) {
        if (metadata["certificate.application.number"] == calledNumber)
            return this.absoluteUrl("vxml/certificatecourse.vxml");
        else
            return this.absoluteUrl("vxml/jobaid.vxml");
    };

    this.getDistGrammar = function() {
        var relativeUrl = metadata["grammar.url"] + "/" + "ANANYA_S001_DISTRICTS.grxml";
        return this.absoluteUrl(relativeUrl);
    };

    this.getBlockGrammar = function() {
        var relativeUrl = metadata["grammar.url"] + "/" + "ANANYA_BLOCKS_"+records[fieldCounter]+".grxml";
        return this.absoluteUrl(relativeUrl);
    };

    this.getPanchayatGrammar = function() {
        var relativeUrl = metadata["grammar.url"] + "/" + "ANANYA_VILLAGES_"+records[fieldCounter]+".grxml";
        return this.absoluteUrl(relativeUrl);
    };

    this.playBack = function(record) {
        return metadata["audio.url"] + metadata['location.audio.url'] + record.resultKey + ".wav";
    };

    this.playBackPrompt = function(field, record) {
        if (this.isVoiceRecognised(field))
            return this.playBack(record);
        else
            return record;
    };

    this.designation = function() {
        return records[0];
    };

    this.name = function() {
        return records[1];
    };

    this.district = function() {
        return records[2];
    };

    this.block = function() {
        return records[3];
    };

    this.panchayat = function() {
        return records[4];
    };
};
