package com.example.demo.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.Python.PythonExecutor;

import jakarta.servlet.http.HttpSession;

@Controller
public class ChatController {

	private final PythonExecutor pythonExecutor;

	ChatController(PythonExecutor pythonExecutor) {
		this.pythonExecutor = pythonExecutor;
	}

	// チャットページを表示
	@RequestMapping(path = "/chat")
	public String showChatPage() {
		return "chatPage/chatPage";
	}

	// チャットに質問
	@RequestMapping(path = "/chat", params = "sendMessage")
	public String sendMessage(String inputText, String parameter, Model model, HttpSession session) {
		if (parameter.equals("special")) {
			String responce = pythonExecutor.pythonExecution("MySQLConnector", true, false);
			inputText = responce += inputText;
			String shopModeInfo = "感想データを読み込みました。";
			session.setAttribute("shopModeInfo", shopModeInfo);
		}
		String[] commandArray = { inputText };
		String chatAnswer = pythonExecutor.pythonExecutionByParameter("chatBot", true, false, commandArray);
		System.out.println("チャット内容：" + chatAnswer);
		if (chatAnswer.trim().equals("会話を終了し、履歴ファイルを消去しました。")) {
			System.out.println("セッション削除");
			session.removeAttribute("shopModeInfo");
		}
		;
		model.addAttribute("chatAnswer", chatAnswer);
		String shopModeInfo = (String) session.getAttribute("shopModeInfo");
		model.addAttribute("shopModeInfo", shopModeInfo);
		return "chatPage/chatPage";
	}
}
