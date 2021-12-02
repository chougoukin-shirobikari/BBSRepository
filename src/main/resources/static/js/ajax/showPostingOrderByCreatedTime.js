/**

投稿(Posting)の表示(新着順)に関するjsファイル

**/

$(document).on('click','#showPostingOrderByCreatedTime', function(event){
	event.preventDefault();
	let threadId = $(this).data('id');
	let bySearch = $(this).data('bysearch');
	let Url;
	
	if(bySearch === 'yes'){
	  let message = $('#searchText').val();
	  Url = "/posting/showPostingBySearchOrderByCreatedTime/" + threadId + "?message=" + message;
	}else{
	  Url = "/posting/showPostingOrderByCreatedTime/" + threadId;
	}
	//let Url = "/posting/showPostingOrderByCreatedTime/" + threadId;
	
	$.ajax({
		type: "GET",
		url : Url,
		data: {page: $(this).data('currentpage')},
		dataType: "html"
	}).done(function(data, status, xhr){
			$('.container').html(data);
			
			if(bySearch === 'yes'){
			  highlight();
			}
			console.log('ajax');
	}).fail(function(XMLHttpRequest, status, errorThrown){
			console.log(XMLHttpRequest);
			console.log(status);
			console.log(errorThrown);
	});
		
});