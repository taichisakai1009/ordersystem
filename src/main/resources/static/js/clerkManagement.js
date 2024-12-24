// 管理画面の「編集」「保存」
function toggleEdit(current) {
	const inputField = document.getElementById(`editableText${current}`);
	const editButton = document.getElementById(`editButton${current}`);

	if (inputField.readOnly) {
		inputField.readOnly = false;
		inputField.style.backgroundColor = 'white';
		editButton.textContent = '保存する';
	} else {
		inputField.readOnly = true;
		inputField.style.backgroundColor = 'lightgray';
		editButton.textContent = '編集する';

	}

	const fields = [
		document.getElementById('editableText1'),
		document.getElementById('editableText2'),
		document.getElementById('editableText3')
	];

	const allReadonly = fields.every(field => field.hasAttribute('readonly')); // 全てreadonlyならTrue

	const submitButton = document.getElementById('changeClerkDetails');
	submitButton.disabled = !allReadonly; // 全てreadonlyなら活性化

	if (!allReadonly) {
		submitButton.classList.add('disabled-button');
	} else {
		submitButton.classList.remove('disabled-button');
	}
}

// 店員情報の更新
function changeClerkDetails() {

	const confirmUpdate = confirm("編集内容に更新しますか？"); // この1行で確認ダイアログが出る。

	if (confirmUpdate) {
		const clerkId = document.getElementById('clerkIdHidden').value;
		const clerkName = document.getElementById('editableText1').value;
		const clerkMailAddress = document.getElementById('editableText2').value;
		const clerkTel = document.getElementById('editableText3').value;

		// リクエストパラメータを準備
		const requestData = {
			clerkId: clerkId,
			name: clerkName,
			mailAddress: clerkMailAddress,
			tel: clerkTel
		};

		// サーバーにリクエストを送信
		fetch('/admin/clerkSelect?update', {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json',
				'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').getAttribute('content')
			},
			body: JSON.stringify(requestData)
		})
			.then(response => {
				if (response.ok) {
					return response.json(); // JSON形式のレスポンスをjsで使えるようにする
				} else {
					throw new Error('Failed to update clerk details');
				}
			})
			.then(data => {
				alert(data.message); // メッセージを表示
			})
			.catch(error => {
				console.error('Error:', error);
			});
	}
}

function confirmDelete(event) {
	// 確認ダイアログを表示
	const userConfirmed = confirm("本当にこのユーザーを削除しますか？");

	// ユーザーが「OK」をクリックした場合のみフォームを送信
	if (!userConfirmed) {
		event.preventDefault(); // フォーム送信をキャンセル
	}
}