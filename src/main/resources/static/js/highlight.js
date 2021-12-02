/**

ハイライト表示をする関数を定義したjsファイル

**/

function highlight(){
	
	var searchWords_json = $('#searchWords').val();
	let searchWords = JSON.parse(searchWords_json);
	let listSize = $('#card').data('size');
			
	for(i = 0; i < listSize; i++){
		let id = '#searchedData' + i;
		let str;
			  
		$.each(searchWords, function(index){
			let regexp1 = new RegExp('(?<=>)[^<>]*?(' + searchWords[index] +')[^<>]*?(?=<)','gi');
			let regexp2 = new RegExp(searchWords[index], "gi");
			      
			str = $(id).html().replace(regexp1, function(){
			  return arguments[0].replace(regexp2, function(matchWord){
			  return '<span class="highlight">'+ matchWord +'</span>';
			  })
			})
		     $(id).html(str);
		});
	}
	
}