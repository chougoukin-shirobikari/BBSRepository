/**

お問い合わせ一覧の表示に関するjsファイル

**/

$(document).on('click', '#nav-item4-tab', function(event){
	event.preventDefault();
	let Url = "/toInquiry";
	
	$.ajax({
		type: "GET",
		url: Url,
		data: {page: $(this).data('currentpage')},
		dataType: "html"
	}).done(function(data, status, xhr){
		$('#nav-item4').html(data);
		console.log('ajax');
	}).fail(function(XMLHttpRequest, status, errorThrown){
		console.log(XMLHttpRequest);
		console.log(status);
		console.log(errorThrown);
	})
	
})