/**

モーダル(削除するReplyを確認)の表示に関するjsファイル

**/

$(function(){
  let posting_id;
  let reply_id;
  let no;
  let name;
  let time;
  let message;
  $(document).on('click', '#modalButton', function(e){
    posting_id = $(e.currentTarget).data('postingid');
    reply_id = $(e.currentTarget).data('replyid');
    no = $(e.currentTarget).data('no');
    name = $(e.currentTarget).data('name');
    time = $(e.currentTarget).data('time');
    message = $(e.currentTarget).data('message');
    
    $('#modalNo').text(no);
    $('#modalName').text(name);
    $('#modalTime').text(time);
    $('#modalMessage').text(message);
    let url = "/reply/update/" + posting_id + "/" + reply_id;
    $('#modalDelete').attr('href', url);
  });
});

