/**

NgWord一覧の表示に関するjsファイル

**/

$(document).on('click', '#nav-item2-tab', function(event){
	event.preventDefault();
	let Url = "/toNgWord";
	
	$.ajax({
		type: "GET",
		url: Url,
		dataType: "html"
	}).done(function(data, status, xhr){
		$('#nav-item2').html(data);
		console.log('ajax');
	}).fail(function(XMLHttpRequest, status, errorThrown){
		console.log(XMLHttpRequest);
		console.log(status);
		console.log(errorThrown);
	})
	
})