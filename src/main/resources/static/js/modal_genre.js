/**

モーダル(削除するGenreを確認)の表示に関するjsファイル

**/

$(function(){
  let genre_id;
  let title;
  $(document).on('click', '#modalButton', function(e){
    genre_id = $(e.currentTarget).data('genreid');
    title = $(e.currentTarget).data('title');
    
    $('#modalTitle').text(title);
    let url = "/genre/delete/" + genre_id;
    $('#modalDelete').attr('href', url);
  });
});
