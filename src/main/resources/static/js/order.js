// CSRFトークンを取得
const csrfToken = document.querySelector('meta[name="_csrf"]').content;
const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

// Enterキーによるフォーム送信を制御
document.addEventListener("keydown", function(event) {
	console.log("キー入力");
	const orderCount = document.getElementById("orderCount");
	const orderCountDisplay = window.getComputedStyle(orderCount).display; // 「カートに入れる」ボタンがあるか否か
	const addToCartButton = document.getElementById("addToCart"); // 「カートに入れる」ボタンを取得

	if (event.key === "Enter" && orderCountDisplay === "none") {
		event.preventDefault(); // デフォルトのフォーム送信を防止
		console.log("Enterキーによるフォーム送信を阻止");
	} else if (event.key === "Enter" && orderCountDisplay === "block") {
		event.preventDefault(); // デフォルトのフォーム送信を防止
		console.log("Enterキーが押され、カートに入れるボタンをクリックします。");
		// 「カートに入れる」ボタンをプログラムからクリック
		addToCartButton.click();
	}
});

// 4つの数字入力フォームの動き
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
	// 入力のたびに商品名を入力
	fetchDishName();
}

// Backspaceキーで一つ前の入力フォームにフォーカス
document.addEventListener('keydown', (event) => {
	const currentId = document.activeElement.id;
	if (event.key === 'Backspace' && document.activeElement.value === '') {
		const prevId = `digit${parseInt(currentId.replace('digit', '')) - 1}`;
		const prevInput = document.getElementById(prevId);
		if (prevInput) prevInput.focus();
	}
});

const OrderNumberInput = document.getElementById('orderNumber');

// async：非同期関数にする。
// 内部処理にawaitをつけることで、その処理が終わるまで次の処理に進まない。
async function fetchDishName() {
	const orderNumber = [
		document.getElementById('digit0').value,
		document.getElementById('digit1').value,
		document.getElementById('digit2').value,
		document.getElementById('digit3').value
	].join('');

	OrderNumberInput.value = orderNumber;
	if (orderNumber) {
		// fetch メソッドはすぐにネットワーク要求を開始しますが、その結果は非同期的に処理され、すぐには取得できません。
		// なのでawaitをつけて取得できるまで待つ。
		try {
			const response = await fetch(`/order/choice?search&orderNumber=${orderNumber}`);
			if (response.ok) {
				const data = await response.json();
				const dishesEntity = data.dishesEntity; // dishesEntityを取り出す
				const dishName = dishesEntity.dishName; // 料理名を取り出す
				const price = dishesEntity.price; // 料金を取り出す
				const onSaleFlg = dishesEntity.onSaleFlg; // 販売中フラグを取り出す

				const dishNameElement = document.getElementById("dishName"); // 商品名
				const notForSaleElement = document.getElementById("notForSale"); // 取り扱い停止中メッセージ
				const orderCountElement = document.getElementById("orderCount"); // 数量調整メーター

				dishNameElement.innerText = dishName + " " + price + "円";
				dishNameElement.style.display = "none";
				notForSaleElement.textContent = dishName + 'は、現在取り扱っておりません。';
				notForSaleElement.style.display = "none";
				orderCountElement.style.display = "none";
				// 商品があるときだけ数量入力フォームを活性化。
				if (dishName) {
					if (onSaleFlg) {
						dishNameElement.style.display = "block";
						dishNameElement.style.color = "darkBlue";
						orderCountElement.style.display = "block";
					} else if (!onSaleFlg) {
						notForSaleElement.style.display = "block";
						notForSaleElement.style.color = "red";
					}
				}
			}
		} catch (error) {
			console.error('Fetch error:', error);
			dishNameElement.innerText = "エラーが発生しました";
		}
	}
}

// モーダル内HTMLの生成
const calculateTotal = () => {
	// 合計金額を計算する
	const rows = document.querySelectorAll(".modal-order tbody tr");
	let total = 0;
	rows.forEach(row => {
		const price = parseFloat(row.querySelector(".price-cell").textContent) || 0;
		const quantity = parseInt(row.querySelector(".number-valueA").textContent) || 0;
		total += price * quantity;
	});
	// 合計を表示
	document.querySelector(".total-amount strong").textContent = `追加料金: ¥${total}`;
};

// 一つに統一できるかもしれないのであとで考える
const valueElement = document.querySelector('.number-value'); // 表示用の数値
const quantityInput = document.getElementById('quantity'); // メソッドに送る数値

quantityInput.value = 1;
let quantity = 1;

// ◀ ボタンで1個削除
function decrement() {
	quantity = Math.max(1, quantity - 1);
	valueElement.textContent = quantity;
	quantityInput.value = quantity;
}

// ▶ ボタンで1個追加
function increment() {
	quantity += 1;
	valueElement.textContent = quantity;
	quantityInput.value = quantity;
}

// previousElementSibling は、指定した要素の前にある兄弟要素を取得します。
// つまりクラス名やIDは関係ない。
function amendmentDec(button) {
	const quantitySpan = button.nextElementSibling;
	let decQuantity = parseInt(quantitySpan.textContent) || 0;
	if (decQuantity > 1) { // 数量が1より大きい場合のみ減少
		quantitySpan.textContent = --decQuantity; // 表示用
		quantityInput.value = decQuantity;
		calculateTotal(); // 合計を再計算
	}
}

function amendmentInc(button) {
	console.log("注文番号：" + orderNumber + "、数量：" + quantity);
	const quantitySpan = button.previousElementSibling;
	let incQuantity = parseInt(quantitySpan.textContent) || 0;
	quantitySpan.textContent = ++incQuantity;
	quantityInput.value = incQuantity;
	calculateTotal(); // 合計を再計算
}

// 注文かごから指定した商品を除外
function removeOrder(button, dishName) {
	console.log("注文取り消し：" + dishName);
	// dishNameをURLエンコードする
	const encodedDishName = encodeURIComponent(dishName);
	// JSONデータを作成
	const jsonData = JSON.stringify({ dishName: dishName });

	// fetchリクエストを送信
	fetch(`/order/choice?remove=true&dishName=${encodedDishName}`, {
		method: 'GET'
	})
		.then(response => {
			if (!response.ok) {
				throw new Error('注文の取り消しに失敗しました');
			}
			return response.text();
		})
		.then(data => {
			console.log("取り消しが完了しました:", data);

			// 取り消す商品の行を削除
			const row = button.closest("tr");
			if (row) {
				row.remove(); // 行を削除
			}

			calculateTotal(); // 合計を再計算

			// 商品がすべて削除された場合にモーダルを閉じる
			const remainingRows = document.querySelectorAll(".modal-order tbody tr");
			if (remainingRows.length === 0) {
				closeModal(); // モーダルを閉じる
			}
		})
		.catch(error => {
			console.error('エラー:', error);
			alert('注文の取り消し中にエラーが発生しました');
		});
}

const modal = document.querySelector('.order-modal');
const modalOverLay = document.querySelector('.modal-overlay');
const modalContainer = document.getElementById('modalContainer');
const viewCartButton = document.querySelector('.js-modal-button'); // 「注文に進む」ボタン
const viewRecordButton = document.querySelector('.view-record-button'); // 注文履歴表示ボタン
const restaurantBillButton = document.querySelector('.restaurant-bill-button'); // お会計ボタン


restaurantBillButton.addEventListener('click', () => {

	fetch('/order/choice?viewRecord=true', {
		method: 'GET',
		headers: {
			'Accept': 'application/json',
			[csrfHeader]: csrfToken // CSRFトークンをヘッダーに追加
		}
	})
		.then(response => {
			if (!response.ok) {
				throw new Error('注文履歴の取得に失敗しました');
			}
			return response.json();
		})
		.then(data => {
			// データ構造を確認
			console.log("受け取ったデータ:", data);

			// orderRecordから直接データを取得
			const orderRecord = data.orderRecordList || [];

			// データが空である場合の処理
			if (!data || !orderRecord || orderRecord.length === 0) {
				console.log("注文履歴が空です");
				// 必要に応じて、ここでユーザーに通知する処理を追加
				alert("注文内容を送信してください");
				return;
			}

			// モーダル内HTMLの生成
			const modalContent = `
<div class="modal-content">
	<div class="modal-order">
		<button type="button" class="close-modal" onclick="closeModal()">×</button>
		<div id="orderFrom">
			<h2 class="margin">お会計</h2>
				<div class="table-container">
			<table border="1">
				<thead>
					<tr>
						<th>商品名</th>
						<th>値段</th>
						<th>数量</th>
					</tr>
				</thead>
				<tbody>
					${orderRecord.orderRecordDtoList.map(Record => `
					<tr>
						<td>${Record.dishName}</td>
						<td style="text-align: center;">${Record.price}</td>
						<td style="text-align: center;">${Record.quantity}</td>
					</tr>
					`).join('')}
				</tbody>
			</table>
				</div>
			<div class="total-price" style="margin-top: 15px;">
				<strong>合計金額: ¥ ${orderRecord.totalPrice}</strong>
			</div>
			<button type="button" onclick="closeModal()">閉じる</button>
			<button type="button" onclick="comfirmAndCheckout()">お会計</button>
		</div>
	</div>
</div>
		`;

			modal.innerHTML = modalContent;
			modal.style.display = 'block';
			modalOverLay.style.display = 'block'

		})
		.catch(error => {
			console.error('Error:', error);
			alert('注文履歴の表示中にエラーが発生しました');
		});
});

// お会計処理
function checkout() {
	const passengerId = document.getElementById('passengerId').value;
	// URL に passengerId をクエリパラメータとして追加
	const url = `/order/choice?title=true&passengerId=${encodeURIComponent(passengerId)}`;

	// fetch API を使用してリクエスト送信
	fetch(url, {
		method: 'GET', // HTTPメソッドを指定
		headers: {
			'Accept': 'text/html', // HTMLレスポンスを受け取るため
		}
	})
		.then(response => {
			if (response.ok) {
				window.location.href = "/order/comment"; // リダイレクト先を指定
				alert('お会計が完了しました。');
				// 成功した場合、HTMLを受け取る
				return response.text();
			} else {
				// エラー処理
				throw new Error(`HTTPエラー: ${response.status}`);
			}
		})
		.then(html => {
			console.log("受信したHTML:", html);
			// 必要に応じて HTML を DOM に挿入
			//        document.querySelector('#contentArea').innerHTML = html;
		})
		.catch(error => {
			console.error("エラーが発生しました:", error);
		});
}

function comfirmAndCheckout() {
	if (confirm("お会計に進みます。よろしいですか？")) {
		checkout();
	}
}


viewRecordButton.addEventListener('click', () => {

	fetch('/order/choice?viewRecord=true', {
		method: 'GET',
		headers: {
			'Accept': 'application/json',
			[csrfHeader]: csrfToken // CSRFトークンをヘッダーに追加
		}
	})
		.then(response => {
			if (!response.ok) {
				throw new Error('注文履歴の取得に失敗しました');
			}
			return response.json();
		})
		.then(data => {
			// データ構造を確認
			console.log("受け取ったデータ:", data);

			// orderRecordから直接データを取得
			const orderRecord = data.orderRecordList || [];

			// データが空である場合の処理
			if (!data || !orderRecord || orderRecord.length === 0) {
				console.log("注文履歴が空です");
				// 必要に応じて、ここでユーザーに通知する処理を追加
				alert("注文内容を送信してください");
				return;
			}

			// モーダル内HTMLの生成
			const modalContent = `
<div class="modal-content">
	<div class="modal-order">
		<button type="button" class="close-modal" onclick="closeModal()">×</button>
		<div id="orderFrom">
			<h2 class="margin">注文履歴</h2>
				<div class="table-container">
			<table border="1">
				<thead>
					<tr>
						<th>商品名</th>
						<th>数量</th>
						<th>注文時間</th>
						<th>お届け状況</th>
					</tr>
				</thead>
				<tbody>
					${orderRecord.orderRecordDtoList.map(Record => `
					<tr>
						<td>${Record.dishName}</td>
						<td style="text-align: center;">${Record.quantity}</td>
						<td style="text-align: center;">${Record.orderTime}</td>
						<td style="text-align: center;">
						 ${Record.undeliveredFlg ? '未配達' : '配達済み'}
						 </td>
					</tr>
					`).join('')}
				</tbody>
			</table>
				</div>
			<div class="total-price" style="margin-top: 15px;">
				<strong>合計金額: ¥ ${orderRecord.totalPrice}</strong>
			</div>
			<button type="button" onclick="closeModal()">閉じる</button>
		</div>
	</div>
</div>
		`;

			modal.innerHTML = modalContent;
			modal.style.display = 'block';
			modalOverLay.style.display = 'block'

		})
		.catch(error => {
			console.error('Error:', error);
			alert('注文履歴の表示中にエラーが発生しました');
		});

});
viewCartButton.addEventListener('click', () => {

	fetch('/order/choice?viewCart=true', {
		method: 'GET',
		headers: {
			'Accept': 'application/json',
			[csrfHeader]: csrfToken // CSRFトークンをヘッダーに追加
		},
	})
		.then(response => {
			if (!response.ok) {
				throw new Error('注文内容の取得に失敗しました');
			}
			return response.json();
		})
		.then(data => {
			// データ構造を確認
			console.log("受け取ったデータ:", data);

			// orderDetailListから直接データを取得
			const orderList = data.orderDetailList || [];

			// データが空である場合の処理
			if (!data || !orderList || orderList.length === 0) {
				console.log("注文内容が空です");
				// 必要に応じて、ここでユーザーに通知する処理を追加
				alert("注文内容をカートに入れてください");
				return;
			}

			// モーダル内HTMLの生成
			const modalContent = `
		<div class="modal-content" >
			<div class="modal-order">
				<button type="button" class="close-modal" onclick="closeModal()">×</button>
				<div id="orderFrom">
				<div class="table-container">
					<table border="1">
						<thead>
							<tr>
								<th>商品名</th>
								<th>注文番号</th>
								<th>数量</th>
								<th>値段</th>
								<th>注文の取り消し</th>
							</tr>
						</thead>
						<tbody>
							${orderList.map(order => `
                                    <tr>
                                        <td>${order.dishName || ''}</td>
                                        <td style="text-align: center;">${order.orderNumber || ''}</td>
										<td style="text-align: center;"><button type="button" name="insert" onclick="amendmentDec(this)" aria-label="Decrement">◀</button>
                                    		<span class="number-valueA">${order.quantity || 1}</span>
                                    		<button type="button" name="insert" onclick="amendmentInc(this)" aria-label="Increment">▶</button>
										</td>
										<td class="price-cell" style="text-align: center;">${order.price || ''}</td>
                                        <td style="text-align: center;">
                                            <button type="button" onclick="removeOrder(this, '${order.dishName || ''}')">取り消し</button>
                                        </td>
                                    </tr>
                                `).join('')}
						</tbody>
					</table>
					</div>
					<div class="total-amount" style="margin-top: 15px;">
						<strong>追加料金: ¥0</strong>
					</div>
					<button type="button" name="order" onclick="confirmAndSubmit()">注文を送信する</button>
					<button type="button" onclick="closeModal()">注文を続ける</button>
				</div>
			</div>
            </div>
		`;

			modal.innerHTML = modalContent;
			modal.style.display = 'block';
			modalOverLay.style.display = 'block'

			calculateTotal(); // 合計を再計算
		})
		.catch(error => {
			console.error('Error:', error);
			alert('カートの表示中にエラーが発生しました');
		});

	console.log("モーダル出す");
});

// モーダルを閉じる
function closeModal() {
	modal.style.display = 'none';
	modalOverLay.style.display = 'none'
}

// カートに商品を追加したことを通知
function showNotification() {
	if (Notification.permission === 'granted') {
		const notification = new Notification('カートに追加しました。', {
			body: '商品がカートに追加されました。',
			icon: 'path/to/icon.png' // アイコン画像のパス
		});

		// 一定時間後に通知を自動的に閉じる
		setTimeout(() => {
			notification.close();
		}, 3000);
	}
}

// プラウザが通知を許可しているか確認
function askNotificationPermission() {
	if ('Notification' in window) {
		Notification.requestPermission().then(permission => {
			if (permission === 'granted') {
				showNotification();
			}
		});
	} else {
		alert('このブラウザは通知をサポートしていません。');
	}
}

// 注文内容を送信する
function submitOrders() {
	// テーブルから全ての注文データを収集
	const orders = [];
	const rows = document.querySelectorAll('#orderFrom tbody tr');
	const seatNumber = document.getElementById('seatNumber').value;
	console.log("seatNumber：" + seatNumber);

	rows.forEach(row => {
		const dishName = row.querySelector('td:nth-child(1)').textContent;
		const orderNumber = row.querySelector('td:nth-child(2)').textContent;
		const quantity = row.querySelector('.number-valueA').textContent;
		const price = row.querySelector('.price-cell').textContent;

		orders.push({
			dishName: dishName,
			orderNumber: orderNumber,
			quantity: parseInt(quantity),
			price: price
		});
	});

	const requestBody = {
		orderDetailsDtoList: orders,
		seatNumber: parseInt(seatNumber)
	};

	const JSONrequestBody = JSON.stringify(requestBody);

	console.log("JSONrequestBody：" + JSONrequestBody);
	// 収集したデータをサーバーに送信
	fetch('/order/choice?order=true', {
		method: 'POST',
		headers: {
			'Content-Type': 'application/json',
			[csrfHeader]: csrfToken // CSRFトークンをヘッダーに追加
		},
		body: JSONrequestBody
	})
		.then(response => {
			if (!response.ok) {
				throw new Error('Network response was not ok');
			}
			return response.json();
		})
		.then(data => {
			window.location.href = "/order/choice?model=true"; // モデル追加を行う
			// 成功時の処理（例：モーダルを閉じる、成功メッセージを表示する等）
			closeModal();
			alert('注文が完了しました。');
		})
		.catch(error => {
			console.error('Error:', error);
			alert('注文の送信に失敗しました。');
		});
}

// 注文確認ダイアログ
function confirmAndSubmit() {
	if (confirm("注文を送信します。よろしいですか？")) {
		submitOrders();
	}
}