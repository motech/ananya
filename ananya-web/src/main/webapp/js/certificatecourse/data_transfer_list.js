var DataTransferList = function() {
    this.init = function() {
        this.counter = 0;
        this.drain();
    };

    this.add = function(data) {
        var dataPacket = {};
        dataPacket["token"] = this.counter++;
        dataPacket["data"] = data;
        this.transferList[this.transferList.length] = dataPacket;
    };

    this.drain = function() {
        this.transferList = new Array();
    };

    this.init();
};