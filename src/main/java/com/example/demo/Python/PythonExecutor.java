package com.example.demo.Python;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class PythonExecutor {

	// python実行ファイルのパス（環境に合わせて設定）
	//  環境変数は設定されてるけど"python"では呼び出されなかった C:\Users\Anali\AppData\Local\Programs\Python\Python313\Lib
	private static final String PYTHON_EXECUTABLE = "C:/Users/Anali/AppData/Local/Programs/Python/Python313/python.exe";
	private static final String PYTHON_SCRIPT_BASEPATH = "C:/workspace2/MyPythonProject/";

	public String pythonPrint(Process process) throws IOException {
		// プロセスの標準出力を読み取る
		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String line;
		StringBuilder outputBuilder = new StringBuilder();
		while ((line = reader.readLine()) != null) {
			outputBuilder.append(line).append("<br>"); // 1行ずつ取得して改行を追加
		}
		String output = outputBuilder.toString();
		System.out.println(output);
		return output;
	}

	public String pythonExecution(String pythonScriptName, boolean printOutput, boolean waitForCompletion) {

		String output = "";

		ProcessBuilder processBuilder = new ProcessBuilder(PYTHON_EXECUTABLE,
				PYTHON_SCRIPT_BASEPATH + pythonScriptName + ".py");

		try {
			System.out.println("Pythonコード実行中...");
			// プロセスを開始
			Process process = processBuilder.start();

			if (printOutput) {
				output = pythonPrint(process);
			}
			; // プロセスの標準出力を読み取る

			if (waitForCompletion) {
				process.waitFor();
			}
			; // プロセスが終了するまで待機
			System.out.println("Pythonコードの実行が終了しました。");
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		return output;
	}

	public String pythonExecutionByParameter(String pythonScriptName, boolean printOutput, boolean waitForCompletion,
			String[] parameter) {
		String output = "";

		List<String> commandList = new ArrayList<>();
		commandList.add(PYTHON_EXECUTABLE);
		commandList.add(PYTHON_SCRIPT_BASEPATH + pythonScriptName + ".py");

		Collections.addAll(commandList, parameter);
		String[] commandArray = commandList.toArray(new String[0]);
		ProcessBuilder processBuilder = new ProcessBuilder(commandArray);

		try {
			System.out.println("Pythonコード実行中...");
			// プロセスを開始
			Process process = processBuilder.start();

			if (printOutput) {
				output = pythonPrint(process);
			}
			; // プロセスの標準出力を読み取る

			if (waitForCompletion) {
				process.waitFor();
			}
			; // プロセスが終了するまで待機
			System.out.println("Pythonコードの実行が終了しました。");
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		return output;
	}
}
