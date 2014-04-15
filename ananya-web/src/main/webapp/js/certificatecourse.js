var locationList;

matchWithAll = function(str1, str2) {
   if (str2 == 'all') return true;

   if (str1 == str2) return true;

   return false;
}

add_location_element = function(location_level, location_name) {
    var location_display = location_name == 'C00' ? 'Unknown' : location_name;
    $('#location_' + location_level).append('<option value="' + location_name + '">' + location_display + '</option>')
}

$('#location_district').change(function() {
    $('#location_block > option').slice(1).remove();
    $('#location_panchayat > option').slice(1).remove();

    var selectedDistrict = $('#location_district').val();

    for (var i in locationList) {
        var location = locationList[i];
        if (matchWithAll(location.district, selectedDistrict) && location.panchayat == '') {
            add_location_element('block', location.block);
        }
    }
    $('#location_block').change();
});

$('#location_block').change(function() {
    $('#location_panchayat > option').slice(1).remove();

    var selectedDistrict = $('#location_district').val();
    var selectedBlock = $('#location_block').val();

    for (var i in locationList) {
        var location = locationList[i];
        if (matchWithAll(location.district, selectedDistrict) &&
            matchWithAll(location.block, selectedBlock) &&
            location.panchayat != '') {
            add_location_element('panchayat', location.panchayat);
        }
    }
});

$(document).ready(function(){
   $("#filter_criteria").submit(function(event){
        event.preventDefault();
        if($(event.target).find('.error').length == 0){
            new DataGrid({
                "tableId": "certificate_usage_report_table",
                "dataUrl": "report/certificatecourse/data",
                "rows": 10,
                "baseSortBy" : "district"
            });

            $("#certificate_usage_report_table").show();
        }
   });



   $("#startDate").datepicker({
       startDate: '-90d',
       endDate: '+0d',
       "autoclose": true
   });

   $("#endDate").datepicker({
        startDate: '-90d',
        "autoclose": true
   });

    $.ajax({
        url:"report/certificatecourse/locations",
        // TODO: figure out correct place for error
        error:function () {
            dataGrid.handleError("An error has occurred, please try again.")
        }
    }).done(function (data) {
            locationList = data;
            var uniqueDistricts = [];
            for (var i in data) {
                var location = data[i];
                if($.inArray(location.district, uniqueDistricts) == -1){
                    add_location_element('district', location.district);
                    uniqueDistricts.push(location.district)
                }
            }
            $('#location_district').change();
        }
    );
});