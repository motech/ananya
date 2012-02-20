var CallDetailLogger = function(){
    this.init=function(){
        this.dataTransferList = new DataTransferList();
    };

    this.start_call=function() {
        this.dataTransferList.add({ "start" : new Date().toLocaleString() });
    };

    this.end_call=function() {
        this.dataTransferList.add({ "end" : new Date().toLocaleString() });
    };

    this.dataPostSuccessful=function(){
        this.dataTransferList.drain();
    };

    this.dataToPost=function(){
        return this.dataTransferList.asJson();
    };

    this.init();
}
