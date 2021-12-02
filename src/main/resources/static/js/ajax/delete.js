/**

Genre, threadの削除に関するjsファイル

**/

$(document).on('click','a#modalDelete', function(event){
	event.preventDefault();
	let Url = $(event.currentTarget).attr('href');
	
	$.ajax({
		type: "GET",
		url : Url,
		data: {page : $('#modalDelete').data('currentpage')},
		dataType: "html"
	}).done(function(data, status, xhr){
			$('.container').html(data);
			$("#fadeModal").modal('hide');
			$("body").removeClass("modal-open");
			$("body").removeAttr("style");
			$('.modal-backdrop').remove();
			console.log('ajax');
	}).fail(function(XMLHttpRequest, status, errorThrown){
			console.log(XMLHttpRequest);
			console.log(status);
			console.log(errorThrown);
	});
		
});