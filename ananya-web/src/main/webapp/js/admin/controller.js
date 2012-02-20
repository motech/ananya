var AdminController = function() {

    var removeSelectedFlw = function() {
        var flwToDelete = $(this).attr("docid");
        $.ajax({
            url:'flw/delete',
            dataType:'html',
            type: 'POST',
            data:{"id":flwToDelete},
            success:refreshScreen
        });
        return false;
    };

    var refreshScreen = function() {
        location.reload();
    };

    var bootstrap = function() {
        $('input[class="flw_delete"]').click(removeSelectedFlw);
    };
    $(bootstrap);
};


$(document).ready(function() {
    new AdminController();
});
