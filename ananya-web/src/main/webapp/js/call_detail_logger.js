var CallDetailLogger = function(){
    this.init=function(){
    };

    this.start_call=function() {
        dataTransferList.add({ "event": "CALL_START" ,"time" :  new Date().getTime() });
    };

    this.end_call=function() {
        dataTransferList.add({ "event": "CALL_END" ,"time" :  new Date().getTime() });
    };

    this.registration_start=function() {
        dataTransferList.add({ "event": "REGISTRATION_START" ,"time" :  new Date().getTime() });
    };

    this.registration_end=function() {
        dataTransferList.add({ "event": "REGISTRATION_END" ,"time" :  new Date().getTime() });
    };

    this.init();
}
