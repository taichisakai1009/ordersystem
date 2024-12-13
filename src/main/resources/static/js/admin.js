// 検索ワードを店員番号か店員名か選択
function choiceSubmitForm() {
	var selectedOption = document.getElementById("selection").value;
	var byNumberForm = document.querySelector(".seach-clerks-number");
	var byNameForm = document.querySelector(".seach-clerks-name");
	if (selectedOption === "number") {
		byNumberForm.style.display = "block";
		byNameForm.style.display = "none";
	} else if (selectedOption === "name") {
		byNumberForm.style.display = "none";
		byNameForm.style.display = "block";
	}
}

// 6つの数字入力フォームの動き
function moveFocus(current) {
	const currentInput = document.getElementById(`digit${current}`);
	const nextInput = document.getElementById(`digit${current + 1}`);

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
	// 数字が6つそろったときのみ検索を実施
	const allInputsFilled = Array.from(digitInputs).every(input => input.value.length === 1);
	if (allInputsFilled) {
		fetchClerksByNumber();
	}
}

// Backspaceキーで一つ前の入力フォームにフォーカス
document.addEventListener('keydown', (event) => {
	// テキスト入力欄にカーソルがある場合
	const currentId = document.activeElement.id; // その入力欄のID
	// digit0からdigit5のIDのみを許可
	const validIds = ['digit0', 'digit1', 'digit2', 'digit3', 'digit4', 'digit5'];

	if (!validIds.includes(currentId)) return;
	if (event.key === 'Backspace' && document.activeElement.value === '') {
		// currentId から'digit'を排除、数字に変換して-1
		const prevId = `digit${parseInt(currentId.replace('digit', '')) - 1}`;
		const prevInput = document.getElementById(prevId);
		if (prevInput) prevInput.focus();
	}
});

const tableBody = document.getElementById('clerksTableBody');

// テーブルの行にクリックイベントを追加
tableBody.addEventListener('click', (event) => {
	// クリックした要素が<tr>かチェック。 event.target はクリックされた具体的なHTML要素（セルや子要素）を指します
	const row = event.target.closest('tr');
	if (!row) return;

	// 行のデータを取得　row.childrenはtr内すべてのtd
	const clerkData = Array.from(row.children).map(cell => cell.textContent.trim());

	// データを確認 (デバッグ用)
	console.log('選択されたデータ:', clerkData);

	// フォームデータを設定
	const form = document.createElement('form');
	form.method = 'POST'; // 必要に応じて 'GET' に変更可能
	form.action = '/admin/clerkManagement?show';

	// 各キー名は、フォーム送信時にサーバー側で受け取るパラメータのキー名になります。
	['clerkId', 'name', 'clerkNumber', 'mailAddress', 'tel', 'startDate', 'roleName'].forEach((key, index) => {
		const input = document.createElement('input');
		input.type = 'hidden';
		input.name = key;
		input.value = clerkData[index] || '未設定';
		form.appendChild(input);
	});

	// CSRFトークンを取得してフォームに追加
	const csrfToken = document.querySelector('meta[name="_csrf"]').content;
	const csrfInput = document.createElement('input');
	csrfInput.type = 'hidden';
	csrfInput.name = "_csrf";
	csrfInput.value = csrfToken;
	form.appendChild(csrfInput);

	// フォームを送信
	document.body.appendChild(form); // 一時的にDOMに追加
	form.submit();
});

// テキストボックスの要素を取得
const searchInput = document.getElementById('seach-clerks-byname');
//let searchName = searchInput.value.trim(); // こうするとsearchNameの値が固定される

function fetchClerksByName() {
	// 名前が空白でない場合にのみfetchを実行
	if (searchInput.value.trim() !== '') {
		console.log("名前が空白でない：" + searchInput.value.trim());
		try {
			// エラーハンドリングとローディング状態の追加
			tableBody.innerHTML = `
        <tr>
            <td colspan="6" class="text-center">検索中...</td>
        </tr>
    `;

			fetch(`/admin/clerkSelect?nameSearch&name=${encodeURIComponent(searchInput.value.trim())}`, {
				method: 'GET',
				headers: {
					'Content-Type': 'application/json',
					//					 CSRFトークン
					'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').getAttribute('content')
				}
			})
				.then(response => {
					if (!response.ok) {
						// HTTPエラーステータスを含めたエラーメッセージ
						throw new Error(`検索に失敗しました (Status: ${response.status})`);
					}
					return response.json();
				})
				.then(clerks => {
					// テーブルボディをクリア
					tableBody.innerHTML = '';

					// 検索結果が空の場合
					if (!clerks || clerks.length === 0) {
						const noResultRow = `
                <tr>
                    <td colspan="6" class="text-center">該当する従業員が見つかりませんでした</td>
                </tr>
            `;
						tableBody.innerHTML = noResultRow;
						return;
					}

					// 検索結果を表示
					clerks.forEach(clerk => {
						// nullチェックと安全な文字列変換
						const row = `
                <tr>
                	<td style="display: none;">${clerk.clerkId || '未設定'}</td>
                    <td>${clerk.name || '未設定'}</td>
                    <td>${clerk.clerkNumber || '未設定'}</td>
                    <td>${clerk.mailAddress || '未設定'}</td>
                    <td>${clerk.tel || '未設定'}</td>
                    <td>${clerk.startDate || '未設定'}</td>
                    <td>${clerk.role.name || '未設定'}</td>
                </tr>
            `;
						tableBody.innerHTML += row;
					});
				})
				.catch(error => {
					console.error('検索エラー:', error);
					tableBody.innerHTML = `
            <tr>
                <td colspan="6" class="text-center">検索中にエラーが発生しました: ${error.message}</td>
            </tr>
        `;
				});

		} catch (error) {
			console.error('エラーが発生しました:', error);
			// エラーハンドリング（必要に応じてユーザーに通知）
		}
	}
	else {
		// 名前が空白の場合は、リストをクリアするなどの処理
		console.log("名前が空白");
		tableBody.innerHTML = ``;
	}

}




const digitInputs = document.querySelectorAll('input[id^="digit"]');
function fetchClerksByNumber() {

	// 6桁の数字を結合
	const clerkNumber = Array.from(digitInputs)
		.map(input => input.value)
		.join('');

	// エラーハンドリングとローディング状態の追加
	tableBody.innerHTML = `
        <tr>
            <td colspan="6" class="text-center">検索中...</td>
        </tr>
    `;

	fetch(`/admin/clerkSelect?numberSearch&clerkNumber=${clerkNumber}`, {
		method: 'GET',
		headers: {
			'Content-Type': 'application/json',
			// CSRFトークンが必要な場合は追加
			'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').getAttribute('content')
		}
	})
		.then(response => {
			if (!response.ok) {
				// HTTPエラーステータスを含めたエラーメッセージ
				throw new Error(`検索に失敗しました (Status: ${response.status})`);
			}
			return response.json();
		})
		.then(clerks => {
			// テーブルボディをクリア
			tableBody.innerHTML = '';

			// 検索結果が空の場合
			if (!clerks || clerks.length === 0) {
				const noResultRow = `
                <tr>
                    <td colspan="6" class="text-center">該当する従業員が見つかりませんでした</td>
                </tr>
            `;
				tableBody.innerHTML = noResultRow;
				return;
			}

			// 検索結果を表示
			clerks.forEach(clerk => {
				// nullチェックと安全な文字列変換
				const row = `
                <tr>
                    <td style="display: none;">${clerk.clerkId || '未設定'}</td>
                    <td>${clerk.name || '未設定'}</td>
                    <td>${clerk.clerkNumber || '未設定'}</td>
                    <td>${clerk.mailAddress || '未設定'}</td>
                    <td>${clerk.tel || '未設定'}</td>
                    <td>${clerk.startDate || '未設定'}</td>
                    <td>${clerk.role.name || '未設定'}</td>
                </tr>
            `;
				tableBody.innerHTML += row;
			});
		})
		.catch(error => {
			console.error('検索エラー:', error);
			tableBody.innerHTML = `
            <tr>
                <td colspan="6" class="text-center">検索中にエラーが発生しました: ${error.message}</td>
            </tr>
        `;
		});
}