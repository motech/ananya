$(document).ready(function(){
    $('form').submit(function(event){
        var form = $(event.target);
        var result = true;

        var trimSpaceInputs = form.find('.trim-space').each(function(index, element){
            element = $(element);
            element.val($.trim(element.val()));
        });

        var requiredInputs = form.find('.required').each(function(index, element){
            element = $(element);
            if(element.val() == "") {
                removeErrorMsg(element);
                element.parents('.controls').append('<span class="help-inline error-help">This is required.</span>');
                element.parents('.control-group').addClass('error');
                removeErrorMsgOnChange(element);
                result = false;
            }
        });


        var dateInputs = form.find('.date').each(function(index, element){
            element = $(element);
            var value = element.val();
            var enteredDate = new Date(value);
            var maxDate = parseDate(element.attr('max-date'));
            var minDate = parseDate(element.attr('min-date'));


            if(value!="" && (enteredDate < minDate || enteredDate > maxDate)) {
                removeErrorMsg(element);
                element.parents('.controls').append('<span class="help-inline error-help">Please select date only within last 90 days.</span>');
                element.parents('.control-group').addClass('error');
                removeErrorMsgOnChange(element);
                result = false;
            }
        });

        var dateRangeInputs = form.find('.date-range-start').each(function(index, element){
            var dateStartEle = $(element);
            var dateEndEle = $(form.find('.date-range-end')[index]);
            var dateRangeStart = new Date(dateStartEle.val());
            var dateRangeEnd = new Date(dateEndEle.val());
            if(dateRangeStart > dateRangeEnd){
                removeErrorMsg(dateStartEle);
                removeErrorMsg(dateEndEle);
                dateEndEle.parents('.controls').append('<span class="help-inline error-help">"To Date" cannot be a date before "From Date".</span>');
                dateEndEle.parents('.control-group').addClass('error');
                removeErrorMsgOnChange(dateStartEle, dateEndEle);
                result = false;
            }
        });

        var passwordElements = form.find('.check-password');
        if(passwordElements.length == 2 && $(passwordElements[0]).val() != $(passwordElements[1]).val()){
            passwordElements[0].value = passwordElements[1].value = '';
            passwordElements.each(function(index, element){
                element = $(element);
                removeErrorMsg(element);
                element.parents('.controls').append('<span class="help-inline error-help">Passwords do not match.</span>');
                element.parents('.control-group').addClass('error');
                removeErrorMsgOnChange(element);

            });
            result = false;
        }

        return result;
    });
    
     parseDate = function(date){
        var result;
        if(/^[-+]\d+[d]$/.test(date)){
            var nDays = parseInt(/([-+]\d+)([d])/g.exec(date)[1]);
            result = new Date();
            result.setDate(result.getDate() + nDays);
            result.setHours(0,0,0,0);
        }
        else if(date != null && date != undefined){
            result = new Date(date);
        }
        return result;
    }

    removeErrorMsgOnChange = function(){
        var elements = arguments;
        $(elements).each(function(i, targetEle){
            $(targetEle).change(function(){
                $(elements).each(function(j, ele){
                    removeErrorMsg($(ele));
                 });
            });
        });
    }

    removeErrorMsg = function(element){
        element = $(element);
        element.parents('.control-group').removeClass('error');
        element.parents('.controls').children('.error-help').remove();
    }

})