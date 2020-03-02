$(function(){
	$("#publishBtn").click(publish);
});

function publish() {
	$("#publishModal").modal("hide");

//    //发送AJAX请求之前，将CSRF令牌设置到请求的消息头中
//    var token = $("meta[name='_csrf']").attr("content");
//    var header = $("meta[name='_csrf_header']").attr("content");
//    $(document).ajaxSend(function(e, xhr, option){
//        xhr.setRequestHeader(header, token);
//    });

	//获取表单内容
	var title = $("#recipient-name").val();
	var content = $("#message-text").val();
	//发送异步请求
	$.post(
	    CONTEXT_PATH + "/discuss/add",
	    {"title":title,"content":content},
	    function(data){
	        data = $.parseJSON(data);
	        $("#hintBody").text(data.msg);
	        $("#gintModel").modal("show");
	        setTimeout(function(){
	            $("#hintModel").modal("hide");
	            if(data.code == 0){
	                window.location.reload();
	            }
	        }, 1000);
	    }
	);

}