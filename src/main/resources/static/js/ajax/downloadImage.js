/**

画像のダウンロードに関するjsファイル

**/

$(function(){
$(document).on('click','#imageModalButton', function(event){
	event.preventDefault();
	
	let postingId = $(this).data('id');
	let bySearch = $('#deleteImage').data('bysearch');
	let Url = "/download/" + postingId;
	let deleteUrl;
	
	if(bySearch === 'yes'){
	  let message = $('#searchText').val();
	  deleteUrl = "/posting/deleteSearchedPostingImage/" + postingId + "?message=" + message;
	}else{
	  deleteUrl = "/posting/deletePostingImage/" + postingId;
	}
	
    $('#deleteImage').attr('href', deleteUrl);
	
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