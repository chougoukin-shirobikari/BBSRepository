/**

Threadの表示(投稿件数の多い順)に関するjsファイル

**/

$(document).on('click', 'a#showThreadOrderByNumberOfPosting', function(event){
	event.preventDefault();
	let genreId = $(this).data('id');
	let bySearch = $(this).data('bysearch');
	let Url;
	//let Url = "/thread/showThreadOrderByNumberOfPosting/" + genreId;
	if(bySearch === 'yes'){
	  let threadTitle = $('#threadTitle').val();
	  Url = "/thread/showThreadBySearchOrderByNumberOfPosting/" + genreId + "?threadTitle=" + threadTitle;
	}else{
	  Url = "/thread/showThreadOrderByNumberOfPosting/" + genreId;
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
	})
	
});

