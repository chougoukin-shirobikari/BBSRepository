/**

モーダルの表示に関するjsファイル

**/

$(function(){
	$('#fadeModal').on('show.bs.modal', function(){
		let title = $('#formTitle').val()
		let modal = $(this)
		
		modal.find('#modalTitle').text(title)
	})
})