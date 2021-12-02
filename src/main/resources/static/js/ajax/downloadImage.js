/**

画像のダウンロードに関するjsファイル

**/

$(function(){
$(document).on('click','#imageModalButton', function(event){
	event.preventDefault();
	let postingId = $(this).data('id');
	 console.log(postingId);
	let Url = "/download/" + postingId;
	
	$.ajax({
		type: "GET",
		url : Url,
		dataType: "text",
	}).done(function(data, status, xhr){
			$('#image').attr("src", data);
			console.log('ajax');
	}).fail(function(XMLHttpRequest, status, errorThrown){
			console.log(XMLHttpRequest);
			console.log(status);
			console.log(errorThrown);
	});
		
});
});