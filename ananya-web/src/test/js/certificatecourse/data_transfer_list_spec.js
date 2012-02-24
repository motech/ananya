describe("Data transfer list", function() {
    var dataTransferList;

    beforeEach(function() {
        metadata = {
            "url.version" : "v1",
            "context.path" : "/ananya",
            "certificate.add.bookmark.url" : "coursestate/add"
        };
        dataTransferList = new DataTransferList();
    });

    it("should initialize the counter keeping track of packets to zero at the start", function () {
        expect(dataTransferList.transferList.length).toEqual(0);
        expect(dataTransferList.counter).toEqual(0);
    });

    it("should add a payload, with token set to next increment of counter and data set to payload", function () {
        var payLoad1 = {"foo" : "bar1"};
        var payLoad2 = {"foo" : "bar2"};
        var type1 = "t1";
        var type2 = "t2";

        dataTransferList.init();
        
        dataTransferList.add(payLoad1,type1);
        dataTransferList.add(payLoad2,type2);

        expect(dataTransferList.transferList.length).toEqual(2);

        expect(dataTransferList.transferList[0].token).toEqual(0);
        expect(dataTransferList.transferList[0].type).toEqual("t1");
        expect(dataTransferList.transferList[0].data).toEqual(payLoad1);

        expect(dataTransferList.transferList[1].token).toEqual(1);
        expect(dataTransferList.transferList[1].type).toEqual("t2");
        expect(dataTransferList.transferList[1].data).toEqual(payLoad2);
    });


    it("should drain all the data upon call of drain(), but retain the current value of counter", function () {
        var payLoad1 = {"foo" : "bar1"};
        var payLoad2 = {"foo" : "bar2"};

        dataTransferList.init();

        dataTransferList.add(payLoad1);
        dataTransferList.add(payLoad2);

        expect(dataTransferList.transferList[0].token).toEqual(0);
        expect(dataTransferList.transferList[0].data).toEqual(payLoad1);

        expect(dataTransferList.transferList[1].token).toEqual(1);
        expect(dataTransferList.transferList[1].data).toEqual(payLoad2);

        dataTransferList.drain();
        
        var payLoad3 = {"foo" : "bar3"};
        var payLoad4 = {"foo" : "bar4"};

        dataTransferList.add(payLoad3);
        dataTransferList.add(payLoad4);

        expect(dataTransferList.transferList[0].token).toEqual(2);
        expect(dataTransferList.transferList[0].data).toEqual(payLoad3);

        expect(dataTransferList.transferList[1].token).toEqual(3);
        expect(dataTransferList.transferList[1].data).toEqual(payLoad4);
    });

     it("should say if there is any data to post", function () {
         dataTransferList.add(1);
         expect(dataTransferList.anyData()).toEqual(true);
     });

     it("should say if there is no data to post", function () {
         expect(dataTransferList.anyData()).toEqual(false);
     });

      it("should give the url at which the coursestate is to be posted", function () {
          expect(dataTransferList.dataPostUrl()).toEqual("/ananya/v1/coursestate/add");
      });
});