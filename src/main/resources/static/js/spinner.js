/**

スピナーの表示に関するjsファイル

**/

$("#button").on("click", function(){
	$("form").submit();
	$("#fadeModal").modal("hide");
	$("#overlay").fadeIn(500);
	setTimeout(function(){
		$("#overlay").fadeOut(500);
	}, 3000);
});