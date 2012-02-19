var AdminController = function() {

    var removeSelectedFlw = function() {
        var flwToDelete = $(this).attr("docid");
        var url = 'flw/delete?id='+flwToDelete;
        $.ajax({
            url:url,
            dataType:'html',
            success:refreshScreen
        });
        return false;
    };

    var refreshScreen = function(){
    };

    var bootstrap = function() {
        $('input[class="flw_delete"]').click(removeSelectedFlw);
    };

    $(bootstrap);
};


$(document).ready(function() {
    new AdminController();
});
