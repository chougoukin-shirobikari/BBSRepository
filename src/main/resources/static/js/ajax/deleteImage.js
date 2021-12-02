/**

Imageの削除に関するjsファイル

**/

$(document).on('click', '#deleteImage', function(event){
	event.preventDefault();
	let postingId = $('#imageModalButton').data('id');
	let bySearch = $('#deleteImage').data('bysearch');
	let Url;
	if(bySearch === 'yes'){
	  let message = $('#searchText').val();
	  Url = "/posting/deleteSearchedPostingImage/" + postingId + "?message=" + message;
	}else{
	  Url = "/posting/deletePostingImage/" + postingId;
	}
	
	$.ajax({
		type: "GET",
		url: Url,
		data: {page: $('#deleteImage').data('currentpage'),
		       bySearch : $('#deleteImage').data('bysearch'),
		       orderBy : $('#deleteImage').data('order')
		       },
		dataType: "html"
	}).done(function(data, status, xhr){
		$('.container').html(data);
		$("#imageModal").modal('hide');
	    $("body").removeClass("modal-open");
		$("body").removeAttr("style");
		$('.modal-backdrop').remove();
		
		if(bySearch === 'yes'){
			  highlight();
		}
		console.log('ajax');
	}).fail(function(XMLHttpRequest, status, errorThrown){
		console.log(XMLHttpRequest);
		console.log(status);
		console.log(errorThrown);
	})
	
})