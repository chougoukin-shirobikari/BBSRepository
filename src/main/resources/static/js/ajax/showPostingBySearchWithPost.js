/**

キーワードで検索された投稿(Posting)の表示に関するjsファイル

**/

$(function(){

$(document).on('submit','#showPostingBySearchWithPost', function(event){
	event.preventDefault();
	let threadId = $(this).data('id');
	let bySearch = $(this).data('bysearch');
	let Url = "/posting/showPostingBySearch/" + threadId;
	
	$.ajax({
		type: "POST",
		url : Url,
		data: {message: $('#searchText').val(),
		       page: $(this).data('currentpage'),
		       _csrf: $("*[name=_csrf]").val()
		       },
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
});


