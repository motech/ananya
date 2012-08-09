var academyCallsDataGrid = new DataGrid({
            "tableId": "academy_calls_table",
            "root" : "academyCalls",
            "rows": 10
        });

var kunjiCallsDataGrid = new DataGrid({
            "tableId": "kunji_calls_table",
            "root" : "kunjiCalls",
            "rows": 10
        });

var callDetailsDataGrid = new DataGrid({
            "tableId": "call_details_table",
            "root" : "callDetails",
            "rows": 10
        });

$(document).ready(function() {
    initDataGrids("0");
    initSearchForm();
});

var initDataGrids = function(msisdn) {
    if (msisdn.length <= 10) {
        msisdn = "91" + msisdn;
    }

    $.ajax({
                url: "admin/inquiry/data",
                data: 'msisdn=' + msisdn,
                dataType: 'json',
                error: function() {
                    academyCallsDataGrid.handleError("An error has occurred, please try again.")
                }
            }).done(function(data) {
                academyCallsDataGrid.initWithData(data);
                kunjiCallsDataGrid.initWithData(data);
                callDetailsDataGrid.initWithData(data);
                $("#academy_calls_table").show();
                $("#kunji_calls_table").show();
                $("#call_details_table").show();

                if (data.callerDataJs) {
                    $("#caller_data_to_ivr").text(data.callerDataJs);
                    prettyPrint();
                }
            });
}

var initSearchForm = function() {
    $("#msisdn_form").submit(function(event) {
        event.preventDefault();
        var msisdn = $("#msisdn").val();
        if (validateMsisdn(msisdn)) {
            initDataGrids(msisdn);
        }
    });
}

var validateMsisdn = function (msisdn) {
    var isValid = msisdn.match(/^(91)?[1-9]\d{9}$/) != null;
    var infoLabel = $("#msisdn_info");

    if (!isValid) {
        infoLabel.text("Enter a valid msisdn. eg 9988776655 or 91998776655")
        infoLabel.addClass("label-warning");
    } else if (infoLabel.hasClass("label-warning")) {
        infoLabel.text("Enter MSISDN")
        infoLabel.removeClass("label-warning");
    }

    return isValid;
}