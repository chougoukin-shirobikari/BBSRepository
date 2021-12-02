/**

Inquiryの削除に関するjsファイル

**/

$(document).on('click', '#deleteInquiry', function(event){
	event.preventDefault();
	let inquiryId = $(this).data('id');
	let Url = "/inquiry/deleteInquiry/" + inquiryId;
	
	$.ajax({
		type: "GET",
		url: Url,
		data: {page: $(this).data('currentpage')},
		dataType: "html"
	}).done(function(data, status, xhr){
		$('#nav-item4').html(data);
		console.log('ajax');
	}).fail(function(XMLHttpRequest, status, errorThrown){
		console.log(XMLHttpRequest);
		console.log(status);
		console.log(errorThrown);
	})
	
})