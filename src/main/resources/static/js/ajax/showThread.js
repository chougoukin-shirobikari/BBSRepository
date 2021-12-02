/**

Threadの表示に関するjsファイル

**/


$(document).on('click','a#showThread', function(event){
	event.preventDefault();
	let genreId = $(this).data('id');
	let bySearch = $(this).data('bysearch');
	let order = $(this).data('order');
	let Url;
	
	if(bySearch === 'yes'){
	  let threadTitle = $('#threadTitle').val();
	  if(order === 'orderByCreatedTime'){
	    Url = "/thread/showThreadBySearchOrderByCreatedTime/" + genreId + "?threadTitle=" + threadTitle;
	  }else if(order === 'orderByNumberOfPosting'){
	    Url = "/thread/showThreadBySearchOrderByNumberOfPosting/" + genreId + "?threadTitle=" + threadTitle;
	  }else{
	    Url = "/thread/showThreadBySearch/" + genreId + "?threadTitle=" + threadTitle;
	  }
	}else{
	  if(order === 'orderByCreatedTime'){
	    Url = "/thread/showThreadOrderByCreatedTime/" + genreId;
	  }else if(order === 'orderByNumberOfPosting'){
	    Url = "/thread/showThreadOrderByNumberOfPosting/" + genreId;
	  }else{
	    Url = "/thread/showThreadByAjax/" + genreId;
	  }
	
	}
	
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