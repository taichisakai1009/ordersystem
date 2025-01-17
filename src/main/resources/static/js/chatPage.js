/**
 * 
 */

let chatModeParameter = document.getElementById('chat-mode-parameter').value;
let inputParameter = document.getElementById('chat-mode-parameter');

function changeParameter() {
	const changeModeButton = document.getElementById('change-mode-button');
	if (chatModeParameter === "simple" || !chatModeParameter) {
		chatModeParameter = "special";
		changeModeButton.innerText = "店舗モード";
		console.log("パラメーター：" + chatModeParameter);
	} else {
		chatModeParameter = "simple";
		changeModeButton.innerText = "汎用モード";
		console.log("パラメーター：" + chatModeParameter);
	}
	inputParameter.value = chatModeParameter;
}