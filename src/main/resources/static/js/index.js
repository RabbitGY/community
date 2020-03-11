$(function(){
	$("#publishBtn").click(publish);
});

function publish() {
	$("#publishModal").modal("hide");

   //发送AJAX请求之前，将CSRF令牌设置到请求的消息头中
   // var token = $("meta[name='_csrf']").attr("content");
   // var header = $("meta[name='_csrf_header']").attr("content");
   // $(document).ajaxSend(function(e, xhr, option){
   //     xhr.setRequestHeader(header, token);
   // });

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
	        //显示提示框
	        $("#hintModel").modal("show");
	        //2s后，自动隐藏提示框
	        setTimeout(function(){
	            $("#hintModel").modal("hide");
	            //刷新页面
	            if(data.code == 0){
	                window.location.reload();
	            }
	        }, 2000);
	    }
	);

}