/**

UserInfoの削除に関するjsファイル

**/

$(document).on('click', '#deleteUserInfo', function(event){
	event.preventDefault();
	let userId = $(this).data('id');
	let ghostUser = $(this).data('ghostuser');
	let Url;
	if(ghostUser === 'GhostUser'){
	  Url = "/deleteGhostUser/" + userId;
	}else{
	  Url = "/deleteUserInfo/" + userId;
	}
	
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