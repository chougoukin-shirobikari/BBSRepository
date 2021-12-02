/**

キーワードで検索されたThreadの表示に関するjsファイル

**/


$(function(){
$(document).on('submit','#showThreadBySearchWithPost', function(event){
	event.preventDefault();
	let genreId = $(this).data('id');
	let bySearch = $(this).data('bysearch');
	let Url = "/thread/showThreadBySearch/" + genreId;
	
	$.ajax({
		type: "POST",
		url : Url,
		data: {threadTitle: $('#threadTitle').val(),
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