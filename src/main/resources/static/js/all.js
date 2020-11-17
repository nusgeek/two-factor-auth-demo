$(document).ready(function () {

    $('#sidebarCollapse').on('click', function () {
        $('#sidebar').toggleClass('active');
        $('#content').toggleClass('active');
    });
});

function toCreatTopicPage() {
    $.get(
        "/toCreateTopicPage", //url地址
        {},  //带给服务器的参数
        function(data){		//成功后的回调函数
            //data 是服务器返回的参数
            $('#id-main-content').html(data);
        }
    );
}

function toCreateSubPage() {
    $.get(
        "/toCreateSubPage", //url地址
        {},  //带给服务器的参数
        function(data){		//成功后的回调函数
            //data 是服务器返回的参数
            $('#id-main-content').html(data);
        }
    );
}

function selectCurrentRow(id) {
    $(".table-header-buttons .disabled").removeClass("disabled");
    /* remove all checked item */
    let selected = $(".table-row").hasClass("row-selected");
    if (selected) {
        $(".row-selected input").prop("checked", false);
        $(".row-selected").removeClass("row-selected");
    }
    /* add the clicked one */
    $("#"+id+" input").prop("checked", true);
    $("#"+id).addClass("row-selected");


}

function addFunToTopicBtn(id) {
    selectCurrentRow(id);
    // add data to modal
    $(".modal-delete-tip").html("<p class='modal-delete-tip'>You really want to delete topic: " +
        $("#" + id + " .topic-name-cell").text() + "?</p>");

    // transmit id to deleteOneTopic function
    $(".topic-delete-modal .modal-footer button[onclick]").attr("onclick", "window.location.href='" + "/deleteOneTopic?id=" + id + "'");

    // transmit id to editOneTopic function
    $("#topic-edit-button").attr("onclick", "toEditTopicPage(" + id + ")");
}

function toEditTopicPage(id) {
        let oneTr = $("#" + id);
        let name = $(oneTr).find('td:eq(2)').text().trim(),
            type = $(oneTr).find('td:eq(3)').text(),
            arn = $(oneTr).find('td:eq(4)').text();
        $.ajax({
            url: "/toEditTopicPage",
            async: true,
            type: "POST",
            data: {"topicName": name, "topicType": type, "topicArn": arn, "id": id},
            success: function (data) {
                $('#id-main-content').html(data);
            }
        })
    }

function addFunToSubsBtn(id) {
    selectCurrentRow(id);

    $(".topic-delete-modal .modal-footer button[onclick]").attr("onclick", "window.location.href='" + "/deleteOneSub?id=" + id + "'");

    $("#subs-edit-button ").attr("onclick", "toEditSubPage(" + id + ")");
}

function toEditSubPage(id) {
    let oneTr = $("#" + id);
    let name = $(oneTr).find('td:eq(2)').text(),
        type = $(oneTr).find('td:eq(3)').text(),
        arn = $(oneTr).find('td:eq(4)').text();
    $.ajax({
        url: "/toEditTopicPage",
        async: true,
        type: "POST",
        data: {"topicName": name, "topicType": type, "topicArn": arn, "id": id},
        success: function (data) {
            $('#id-main-content').html(data);
        }
    })
}