$(document).on('click','a#showThread', function(event){
	event.preventDefault();
	let genreId = $(this).data('id');
	let currentpage = $(this).data('currentpage');
	let Url = "/thread/showThread/" + genreId + "/" + currentpage;
	
	$.ajax({
		type: "GET",
		url : Url,
		dataType: "html"
	}).done(function(data, status, xhr){
			$('.container').html(data);
			console.log('ajax');
	}).fail(function(XMLHttpRequest, status, errorThrown){
			console.log(XMLHttpRequest);
			console.log(status);
			console.log(errorThrown);
	}).always(() => {
	        $('a#showThread').removeAttr('disabled');
	});
		
});