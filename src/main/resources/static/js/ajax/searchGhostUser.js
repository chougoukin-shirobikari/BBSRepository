/**

三カ月以上書き込みのないユーザーの検索に関するjsファイル

**/


$(document).on('click', '#searchGhostUser', function(event){
	event.preventDefault();
	let Url = "/searchGhostUser";
	
	$.ajax({
		type: "GET",
		url: Url,
		data: {page: $(this).data('currentpage')},
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