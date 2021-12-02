/**

NgWordの削除に関するjsファイル

**/

$(document).on('click', '#deleteNgWord', function(event){
	event.preventDefault();
	let ngWordId = $(this).data('id');
	let Url = "/deleteNgWord/" + ngWordId;
	
	$.ajax({
		type: "GET",
		url: Url,
		dataType: "html"
	}).done(function(data, status, xhr){
		$('#nav-item2').html(data);
		console.log('ajax');
	}).fail(function(XMLHttpRequest, status, errorThrown){
		console.log(XMLHttpRequest);
		console.log(status);
		console.log(errorThrown);
	}).always(() => {
		$('#nav-item2-tab').removeAttr('disabled');
	})
})