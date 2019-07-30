/**
 * Created by ylj on 2017/11/13.
 */

/**
 * 表单一行的数据，（已排序）
 * @param array
 * @returns {string}
 */
var createFormLine = function(array) {
    var contentString = "";

    contentString += '<div class="row">';

    for(var index in array) {

        contentString += ' <div class="col-xs-' + array[index].displayLength + '">';

        contentString += '<div class="form-group" >';

        contentString += '<label class="col-xs-' + array[index].labelWidth +
                                    ' control-label" style="text-align: right;">';
        contentString +=  array[index].title + '</label>';

        contentString += '<div class="col-xs-' + (12 - array[index].labelWidth) + '" style="text-align: left">';

        contentString += createFormElement(array[index]);

        contentString += '</div>';

        contentString += '</div>';

        contentString += '</div>';

    }

    contentString += '</div>';

    return contentString;
}

/**
 * 拼接控件
 * @param data
 * @returns {string}
 */
function createFormElement(data){
    var elementString = "";
    var display = data.display;
    var defaultValue = data.defaultValue || '';
    var extraAttribute = JSON.parse(data.extraAttribute);
    var datePickerFrom =  translateDate(extraAttribute.datePickerFrom);
    var datePickerTo = translateDate(extraAttribute.datePickerTo);

    if ("multiSelect" == display) {
        var dataSource = getDataSource(data.sourceCode, data.sourceType);
        saveDataToSource(dataSource, data.tableFieldName);
        elementString += '<select style="width:100%" ' + info(data);
        elementString += ' ></select>';
        elementString += '<script>' +
            'var multi = $("#' + data.tableFieldName + '").kendoMultiSelect({' +
            '   dataSource: '+ JSON.stringify(dataSource) + ',' +
            '   dataTextField: "textField",' +
            '   dataValueField: "valueField",' +
            '   value: [' + defaultValue + ']' +
            '}).data("kendoMultiSelect");';
        if('Y' == data.required) {
            elementString += 'multi.wrapper.find(".k-multiselect-wrap").css("background-color","rgb(255, 248, 197)");'
        }
        elementString +='</script>';

    }else if ("comboBox" == display) {
        var dataSource = getDataSource(data.sourceCode, data.sourceType);
        saveDataToSource(dataSource, data.tableFieldName);
        elementString += '<input style="width: 100%;" ' + info(data);
        elementString += ' ></select>';
        var cascadeFrom  = "";
        var cascadeFromField = "";
        if(null != data.extraAttribute && undefined != data.extraAttribute) {
            var extraAttribute = eval('(' + data.extraAttribute + ')');
            cascadeFrom  = extraAttribute.cascadeFrom;
            cascadeFromField = extraAttribute.cascadeFromField;
        }
        var autoBind = !isNotEmpty(cascadeFrom) && isNotEmpty(defaultValue)?true:false;
        elementString += '<script>' +
            '$("#' + data.tableFieldName + '").kendoComboBox({' +
            ' autoBind: ' + autoBind + ',' +
            ' cascadeFrom: "' + cascadeFrom + '",' +
            ' cascadeFromField: "' + cascadeFromField + '",' +
            'valuePrimitive: true,' +
            '   dataSource: ' + JSON.stringify(dataSource) + ',' +
            '   dataTextField: "textField",' +
            '   dataValueField: "valueField",' +
            '   value: "' + defaultValue + '"' +
            '});' +
            '</script>';
    } else if ("LOV" == display) {
        var dataSource = getDataSource(data.sourceCode, data.sourceType);
        saveDataToSource(dataSource, data.tableFieldName);
        var defaultText = data.defaultText || '';
        elementString += '<input style="width: 100%; "' + info(data);
        elementString += ' />';
        elementString += '<script>' +
            ' $("#' + data.tableFieldName + '").kendoLov({' +
            '   code: "' + data.sourceCode + '",' +
            '   contextPath: "' + _basePath + '",' +
            '   locale: "' + _locale + '"' +
            '}).data("kendoLov");' +
            ' $("#' + data.tableFieldName + '").data("kendoLov").value("' + defaultValue + '");' +
            ' $("#' + data.tableFieldName + '").data("kendoLov").text("' + defaultText + '");' +
            '</script>';

    } else if ("textBox" == display) {
        elementString += '<input ' + info(data);
        if('Y' == data.readOnly) {
            elementString += ' style="width:100%; background-color: #ededed !important;" ';
        } else {
            elementString += ' style="width:100%" ';
        }
        elementString += ' class="k-textbox" ';
        if(null != data.dataLength) {
            elementString += 'dataLength="' + data.dataLength + '" ';
        }
        elementString += ' data-role="combobox" data-value-primitive="true" data-text-field="meaning" data-value-field="value" ';
        elementString += ' value="' + defaultValue + '" ';
        elementString += ' />';
    } else if("datePicker" == display) {
        var str = isNotEmpty(defaultValue) ? 'value="' + defaultValue + '"' : '';
        elementString += '<input style="width: 100%;" class="k-datepicker" ' + str + ' ' + info(data);
        elementString += '/>';
        var opts = 'format: "{0:yyyy-MM-dd}",';
        if(datePickerFrom){
            opts += 'min: "'+ datePickerFrom +'",';
        }
        if(datePickerTo){
            opts += 'max: "'+ datePickerTo +'",';
        }
        elementString += '<script>' +
            '$("#' + data.tableFieldName + '").kendoDatePicker({' + opts + '});'+
            '</script>';
    }
    return elementString;
}

/**
 * 拼接 id name
 * 判定 是否必输 是否只读
 * @param data
 * @returns {string}
 */
function info(data) {
    var elementString = "";
    elementString += ' id="' + data.tableFieldName + '" name="' + data.title + '"';
    if('Y' == data.readOnly) {
        elementString += ' disabled="disabled" ';
    }
    if('Y' == data.required) {
        elementString += ' required ';
    }
    return elementString;
}

/**
 * 获取DataSource
 * @param sourceCode
 */
function getDataSource(sourceCode, sourceType){
    var data ;
    if(isNotEmpty(sourceCode)) {
        $.ajax({
            url: _basePath + "/sys/parameter/config/" + getDynamicDataSourceUrl(sourceType) + "?sourceCode=" + sourceCode,
            type: 'POST',
            dataType: "json",
            async: false,
            contentType: "application/json",
            success: function (args) {
                 data = args.rows;
            }
        });
    }
    return data || '';
}

function getDynamicDataSourceUrl(sourceType){
    if(sourceType == 'LOV'){
        return "getLov";
    }else if(sourceType == 'CODE'){
        return "getCode";
    }
}

/**
 * 非空判定
 * @param obj
 * @returns {boolean}
 */
function isNotEmpty(obj) {
    return obj!=undefined && obj!=null && obj!="";
}

function translateDate(date){
    if(isNotEmpty(date)){
        var  temp =  date.split(" ");
        return temp[0];
    }else{
        return "";
    }
}

/**
 * 将数据源存入变量，用于保存时获取填写数据.
 *      LOV数据源，下拉框数据源, 多选框数据源
 * @param dataSource
 */
function saveDataToSource(dataSource, tableFieldName) {
    if (dataSource.length == 0) {
        return;
    }
    var data = {};
    dataSource.forEach(function (d) {
        data[d.valueField] = d
    });
    dataSources[tableFieldName] = data;
}


