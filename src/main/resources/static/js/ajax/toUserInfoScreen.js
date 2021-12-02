/**

UserInfoの表示に関するjsファイル

**/

$(document).on('click', '#nav-item3-tab', function(event){
	event.preventDefault();
	let Url = "/toUserInfo";
	
	$.ajax({
		type: "GET",
		url: Url,
		data: {page: $(this).data('currentpage')},
		dataType: "html"
	}).done(function(data, status, xhr){
		$('#nav-item3').html(data);
		console.log('ajax');
	}).fail(function(XMLHttpRequest, status, errorThrown){
		console.log(XMLHttpRequest);
		console.log(status);
		console.log(errorThrown);
	})
	
})

$(document).on('click', '#toUserInfo', function(event){
	event.preventDefault();
	let Url = "/toUserInfo";
	
	$.ajax({
		type: "GET",
		url: Url,
		data: {page: $(this).data('currentpage')},
		dataType: "html"
	}).done(function(data, status, xhr){
		$('#nav-item3').html(data);
		console.log('ajax');
	}).fail(function(XMLHttpRequest, status, errorThrown){
		console.log(XMLHttpRequest);
		console.log(status);
		console.log(errorThrown);
	})
	
})