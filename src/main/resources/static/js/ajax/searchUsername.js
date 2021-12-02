/**

ユーザーの検索に関するjsファイル

**/

$(document).on('submit', '#nav-item3', function(event){
	event.preventDefault();
	let Url = "/searchUsername";
	
	$.ajax({
		type: "POST",
		url: Url,
		data: {username: $('#searchForm').val(),
		       page: $('#searchUsername').data('currentpage'),
		       _csrf: $("*[name=_csrf]").val()},
		dataType: "html"
	}).done(function(data, status, xhr){
		$('#nav-item3').html(data);
		console.log('ajax')
	}).fail(function(XMLHttpRequest, status, errorThrown){
		console.log(XMLHttpRequest);
		console.log(status);
		console.log(errorThrown);
	});
});