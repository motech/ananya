var academyCallsDataGrid = new DataGrid({
            "tableId": "academy_calls_table",
            "root" : "academyCalls",
            "rows": 100
        });

var kunjiCallsDataGrid = new DataGrid({
            "tableId": "kunji_calls_table",
            "root" : "kunjiCalls",
            "rows": 100
        });

var callDetailsDataGrid = new DataGrid({
            "tableId": "call_details_table",
            "root" : "callDetails",
            "rows": 100
        });

$(document).ready(function() {
    initDataGrids("");
    initSearchForm();
});

var initDataGrids = function(msisdn) {
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
        return false;
    });

    $("#msisdn").keyup(function(event) {
        event.preventDefault();
        if (event.keyCode == 13) {
            initDataGrids($("#msisdn").val());
        }
    });
}