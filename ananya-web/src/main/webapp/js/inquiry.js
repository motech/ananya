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
    resetForm();
    initDataGrids("0");
    fetchAndDisplay();
});

var fetchAndDisplay = function() {
    $("#msisdn_form").submit(function(event) {
        event.preventDefault();
        var msisdn = $("#msisdn").val();
        if (validateMsisdn(msisdn)) {
            initDataGrids(msisdn);
        }
    });
}

var showPostgresDetails = function(data) {
    $("#postgres_details").show();

    academyCallsDataGrid.initWithData(data);
    kunjiCallsDataGrid.initWithData(data);
    callDetailsDataGrid.initWithData(data);

    $("#academy_calls_table").show();
    $("#kunji_calls_table").show();
    $("#call_details_table").show();

    if (data.callerDetail) {
        $("#caller_name").text(data.callerDetail.name);
        $("#caller_msisdn").text(data.callerDetail.msisdn);
    }
    $("#postgres_error").hide();
}

var showCouchDbDetails = function(data) {
    $("#couchdb_details").show();

    if (data.callerDataJs) {
        $("#caller_data_to_ivr").text(data.callerDataJs);
        prettyPrint();
    }
    $("#couchdb_error").hide();

}

var showAllDetails = function(data) {
    if (data.postgresError) {
        $("#postgres_details").hide();
        $("#postgres_error").html(data.postgresError);
        $("#postgres_error").show();
    } else {
        showPostgresDetails(data);
    }
    if (data.couchdbError) {
        $("#couchdb_details").hide();
        $("#couchdb_error").html(data.couchdbError);
        $("#couchdb_error").show();
    } else {
        showCouchDbDetails(data);
    }
}

var initDataGrids = function(msisdn) {
    if (msisdn.length <= 10) {
        msisdn = "91" + msisdn;
    }
    resetForm();
    $.ajax({
        url: "admin/inquiry/data",
        data: 'msisdn=' + msisdn,
        dataType: 'json'

    }).done(showAllDetails);
}

var resetForm = function() {
    $("#postgres_error").hide();
    $("#postgres_details").show();
    $("#couchdb_error").hide();
    $("#couchdb_details").show();
}

var validateMsisdn = function (msisdn) {
//    var isValid = msisdn.match(/^(91)?[1-9]\d{9}$/) != null;
//    var infoLabel = $("#msisdn_info");
//
//    if (!isValid) {
//        infoLabel.text("Enter a valid msisdn. eg 9988776655 or 91998776655")
//        infoLabel.addClass("label-warning");
//    } else if (infoLabel.hasClass("label-warning")) {
//        infoLabel.text("Enter MSISDN")
//        infoLabel.removeClass("label-warning");
//    }
//    return isValid;
    return true;
}