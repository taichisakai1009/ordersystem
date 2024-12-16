// 指定された文字数のパスワードを生成
function generatePassword(length) {
	const charset = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*()_+";
	let password = "";
	for (let i = 0; i < length; i++) {
		const randomIndex = Math.floor(Math.random() * charset.length);
		password += charset[randomIndex];
	}
	return password;
}

// ^= 演算子は、「始まりが指定した文字列と一致する」という意味を持ちます。この演算子を使用することで、指定した文字列で始まる属性値を持つ要素を選択できます。
const registNum = document.querySelectorAll('input[id^="regist-digit"]'); // 数字の入力欄を取得

// 6つの数字入力フォームの動き
async function moveFocus(current) {

	const currentInput = document.getElementById(`regist-digit${current}`);
	const nextInput = document.getElementById(`regist-digit${current + 1}`);

	// e, -, ., + を受け付けない
	currentInput.value = currentInput.value.replace(/[^0-9]/g, "");

	// 入力が完了し次のフィールドにフォーカス
	if (currentInput.value.length === 1 && nextInput) {
		nextInput.focus();
	}
	// 入力値の長さをチェックし、2文字以上なら最後の文字だけを保持
	if (currentInput.value.length > 1) {
		currentInput.value = currentInput.value.slice(-1);
	}

	// 6桁の数字を結合 
	const clerkNumber = Array.from(registNum) // 配列に変換
		.map(input => input.value) // .mapメソッドでvalue属性の値を配列の要素一つづつに対して取得
		.join(''); // 配列を一つに結合

	const submitButton = document.getElementById('clerk-submit-button'); // 新規登録ボタン
	const duplicationMessage = document.getElementById('clerk-number-duplication'); // 重複メッセージ
	submitButton.style.display = "none";
	duplicationMessage.style.display = "none";

	// 数字が6つそろったときのみ登録の準備をする
	const allInputsFilled = Array.from(registNum).every(input => input.value.length === 1);
	if (allInputsFilled) {

		const duplicationFlg = await duplicationCheck(clerkNumber); // 店員番号の重複フラグを取得
		if (duplicationFlg === false) {
			submitButton.style.display = "block";
			duplicationMessage.style.display = "none";
		} else {
			submitButton.style.display = "none";
			duplicationMessage.style.display = "block";
		}
		document.getElementById('regist-date').value = getTodayStr(); // 今日の日付を文字列で埋め込む(yyyy-mm-dd)
		document.getElementById('regist-num').value = clerkNumber; // hidden の値を埋め込む
		const password = generatePassword(12);
		document.getElementById('regist-password').value = password; // 12文字の初期パスワードを埋め込む
		console.log("パスワード：" + password);
	}
}

// 現在日時をyyyy-mm-dd型の文字列で取得
function getTodayStr() {
	const today = new Date();
	const year = today.getFullYear();
	const month = String(today.getMonth() + 1).padStart(2, '0'); // 月は0始まりなので+1
	const day = String(today.getDate()).padStart(2, '0');
	const localDate = `${year}-${month}-${day}`;
	return localDate;
}

// 店員番号の重複チェック
async function duplicationCheck(clerkNumber) {
	const response = await fetch(`/admin/clerkRegist?duplication&clerkNumber=${clerkNumber}`);
	const exists = await response.json();
	return exists;
}

// フォーム送信前の確認処理
function confirmAndSubmit(event) {
	event.preventDefault(); // フォーム送信を一旦停止

	// 確認ダイアログを表示
	const isConfirmed = confirm("新規登録を行います。よろしいですか？");
	if (isConfirmed) {

		// フォーム要素を取得
		const form = event.target.closest('form');

		// 新しいinput要素を作成してパラメータを追加
		const input = document.createElement('input');
		input.type = 'hidden';
		input.name = 'regist';

		// フォームに新しいinput要素を追加
		form.appendChild(input);

		// フォームを送信
		form.submit();

		// 確認がOKの場合、完了ダイアログを表示
		alert("新規登録を行いました。");
	}
}

// Backspaceキーで一つ前の入力フォームにフォーカス
document.addEventListener('keydown', (event) => {
	// テキスト入力欄にカーソルがある場合
	const currentId = document.activeElement.id; // その入力欄のID
	// digit0からdigit5のIDのみを許可
	const validIds = ['regist-digit0', 'regist-digit1', 'regist-digit2', 'regist-digit3', 'regist-digit4', 'regist-digit5'];

	if (!validIds.includes(currentId)) return;
	if (event.key === 'Backspace' && document.activeElement.value === '') {
		// currentId から'digit'を排除、数字に変換して-1
		const prevId = `regist-digit${parseInt(currentId.replace('regist-digit', '')) - 1}`;
		const prevInput = document.getElementById(prevId);
		if (prevInput) prevInput.focus();
	}
});
