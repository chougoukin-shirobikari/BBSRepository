/**

これ以上投稿できないThreadの表示に関するjsファイル

**/


$(document).on('click','#showUnwritableThread', function(event){
	event.preventDefault();
	let genreId = $(this).data('id');
	let Url = "/thread/showUnwritableThread/" + genreId;
	
	$.ajax({
		type: "GET",
		url : Url,
		data: {page: $(this).data('currentpage')},
		dataType: "html"
	}).done(function(data, status, xhr){
			$('.container').html(data);
			console.log('ajax');
	}).fail(function(XMLHttpRequest, status, errorThrown){
			console.log(XMLHttpRequest);
			console.log(status);
			console.log(errorThrown);
	})
		
});