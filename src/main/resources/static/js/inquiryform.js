/**

モーダル(お問い合わせの確認)の表示に関するjsファイル

**/

$(function(){
	$('#fadeModal').on('show.bs.modal', function(){
		let name = $('#formName').val()
		let message = $('#formMessage').val()
		let modal = $(this)
		
		modal.find('#modalName').text(name)
		modal.find('#modalMessage').text(message)
	})
})