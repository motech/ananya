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
        return "flw/register";
    };

    this.submitNameUrl = function() {
        return  "flw/record/name";
    };

    this.nextFlow = function(calledNumber) {
        if (metadata["certificate.application.number"] == calledNumber)
            return metadata["url.version"] + "/vxml/certificatecourse.vxml";
        else
            return metadata["url.version"] + "/vxml/jobaid.vxml";
    };

    this.getGrammar = function() {
        return metadata["grammar.url"]+"ANANYA_ALL.grxml";
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
