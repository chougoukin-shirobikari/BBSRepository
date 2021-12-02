/**

NgWordの登録に関するjsファイル

**/

$(document).on('submit', '#registerNgWord', function(event){
	event.preventDefault();
	
	$.ajax({
		type: "POST",
		url: $(this).attr('action'),
		data: {ngWord: $('#ngWord').val(),
		       _csrf: $("*[name=_csrf]").val()
		      },
		dataType: "html"
	}).done(function(data, status, xhr){
		$('#nav-item2').html(data);
		console.log('ajax');
	}).fail(function(XMLHttpRequest, status, errorThrown){
		console.log(XMLHttpRequest);
		console.log(status);
		console.log(errorThrown);
	})
	
})