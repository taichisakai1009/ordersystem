package com.example.demo.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.Python.PythonExecutor;

@Controller
public class ChatController {

	@Autowired
	PythonExecutor pythonExecutor;

	@RequestMapping(path = "/chat")
	public String showChatPage() {
		return "chatPage/chatPage";
	}

	@RequestMapping(path = "/chat", params = "sendMessage")
	public String sendMessage(String inputText, Model model) {
		String[] commandArray = { inputText };
		String chatAnswer = pythonExecutor.pythonExecutionByParameter("chatBot", true, false, commandArray);
		model.addAttribute("chatAnswer", chatAnswer);
		return "chatPage/chatPage";
	}
}
