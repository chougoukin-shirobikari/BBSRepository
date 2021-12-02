/**

Threadの表示(新着順)に関するjsファイル

**/

$(document).on('click', '#showThreadOrderByCreatedTime', function(event){
	event.preventDefault();
	let genreId = $(this).data('id');
	let bySearch = $(this).data('bysearch');
	let Url;
	
	if(bySearch === 'yes'){
	  let threadTitle = $('#threadTitle').val();
	  Url = "/thread/showThreadBySearchOrderByCreatedTime/" + genreId + "?threadTitle=" + threadTitle;
	}else{
	  Url = "/thread/showThreadOrderByCreatedTime/" + genreId;
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