/**

投稿(Posting)の表示に関するjsファイル

**/

$(function(){
$(document).on('click','#showPosting', function(event){
	event.preventDefault();
	let threadId = $(this).data('id');
	let bySearch = $(this).data('bysearch');
	let order = $(this).data('order');
	let Url;
	
	if(bySearch === 'yes'){
	  let message = $('#searchText').val();
	  if(order === 'orderByCreatedTime'){
	    Url = "/posting/showPostingBySearchOrderByCreatedTime/" + threadId + "?message=" + message;
	  }else{
	    Url = "/posting/showPostingBySearch/" + threadId + "?message=" + message;
	  }
	}else{
	  if(order === 'orderByCreatedTime'){
	    Url = "/posting/showPostingOrderByCreatedTime/" + threadId;
	  }else{
	    Url = "/posting/showPostingByAjax/" + threadId;
	  }
	}
	
	$.ajax({
		type: "GET",
		url : Url,
		data: {page: $(this).data('currentpage')},
		dataType: "html",
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
});