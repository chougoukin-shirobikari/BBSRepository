/**

モーダル(削除するThreadを確認)の表示に関するjsファイル

**/

$(function(){
  let genreId;
  let threadId;
  let title;
  let bySearch;
  let Url;
  $(document).on('click', '#modalButton', function(e){
    genreId = $(e.currentTarget).data('genreid');
    threadId = $(e.currentTarget).data('threadid');
    title = $(e.currentTarget).data('title');
    bySearch = $(e.currentTarget).data('bysearch');
    
    $('#modalTitle').text(title);
    
    if(bySearch === 'yes'){
       let threadTitle = $('#threadTitle').val();
	   Url = "/thread/deleteSearchedThread/" + genreId + "/" + threadId + "?threadTitle=" + threadTitle;
	 }else{
	   Url = "/thread/deleteThread/" + genreId + "/" + threadId;
	 }
    $('#modalDelete').attr('href', Url);
  });
});
