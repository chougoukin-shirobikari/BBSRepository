/**

モーダル(削除するPostingを確認)の表示に関するjsファイル

**/

$(function(){
  let postingId;
  let threadId;
  let no;
  let name;
  let time;
  let message;
  let bySearch;
  let Url;
  $(document).on('click', '#modalButton', function(e){
    postingId = $(e.currentTarget).data('postingid');
    threadId = $(e.currentTarget).data('threadid');
    no = $(e.currentTarget).data('no');
    name = $(e.currentTarget).data('name');
    time = $(e.currentTarget).data('time');
    message = $(e.currentTarget).data('message');
    bySearch = $(e.currentTarget).data('bysearch');
    
    $('#modalNo').text(no);
    $('#modalName').text(name);
    $('#modalTime').text(time);
    $('#modalMessage').text(message);
    
    if(bySearch === 'yes'){
       let messages = $('#searchText').val();
	   Url = "/posting/deleteSearchedPosting/" + threadId + "/" + postingId + "?message=" + messages;
	 }else{
	   Url = "/posting/deletePosting/" + threadId + "/" + postingId;
	 }
    $('#modalDelete').attr('href', Url);
  })
})


