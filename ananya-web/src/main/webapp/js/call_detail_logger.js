var CallDetailLogger = function(){
    this.init=function(){
        this.dataTransferList = new DataTransferList();
    };

    this.start_call=function() {
        this.dataTransferList.add({ "event": "CALL_START" ,"time" :  new Date().getTime() });
    };

    this.end_call=function() {
        this.dataTransferList.add({ "event": "CALL_END" ,"time" :  new Date().getTime() });
    };

    this.registration_start=function() {
        this.dataTransferList.add({ "event": "REGISTRATION_START" ,"time" :  new Date().getTime() });
    };

    this.registration_end=function() {
        this.dataTransferList.add({ "event": "REGISTRATION_END" ,"time" :  new Date().getTime() });
    };

    this.dataPostSuccessful=function(){
        this.dataTransferList.drain();
    };

    this.dataToPost=function(){
        return this.dataTransferList.asJson();
    };

    this.init();
}
