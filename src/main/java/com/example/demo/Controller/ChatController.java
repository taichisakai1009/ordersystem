package com.example.demo.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.Python.PythonExecutor;

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
	public String sendMessage(String inputText, Model model) {
		String responce = pythonExecutor.pythonExecution("MySQLConnector", true, false);
		
		inputText = responce += inputText;
		System.out.println("質問：" + responce);
		String[] commandArray = { inputText };
		String chatAnswer = pythonExecutor.pythonExecutionByParameter("chatBot", true, false, commandArray);
		model.addAttribute("chatAnswer", chatAnswer);
		return "chatPage/chatPage";
	}
}
