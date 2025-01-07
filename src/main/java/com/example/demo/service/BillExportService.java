package com.example.demo.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.example.demo.Dto.OrderRecordDto;
import com.example.demo.Dto.OrderRecordDtoList;

@Service
public class BillExportService {

	// Excelファイルのヘッダー内容
	private static final String[] HEADER_CONTENT = { "商品名", "", "金額", "数量" };

	// 会計情報をExcel形式でファイルにエクスポート
	public void exportBillToExcel(OrderRecordDtoList orderDetails, String filePath) throws IOException {
		List<OrderRecordDto> orderList = orderDetails.getOrderRecordDtoList();
		int totalPrice = orderDetails.getTotalPrice();

		// Excelワークブックの作成
		try (Workbook workbook = new XSSFWorkbook()) {
			Sheet sheet = workbook.createSheet("Bill");

			// スタイル作成（ヘッダーと本文のスタイル）
			CellStyle headerStyle = createHeaderCellStyle(workbook);
			CellStyle cellStyle = createBodyCellStyle(workbook);

			// ヘッダー行作成
			createHeaderRow(sheet, headerStyle);

			// データ行作成
			populateOrderData(sheet, orderList, cellStyle);

			// 合計金額行作成
			createTotalRow(sheet, orderList.size() + 2, totalPrice);

			// 枠線で囲う
			addBorderToRange(sheet, 1, orderList.size() + 1, 1, 4);

			// ファイル出力
			writeFile(workbook, filePath);
		}
	}

	// ヘッダーのセルスタイルを作成
	private CellStyle createHeaderCellStyle(Workbook workbook) {
		CellStyle style = workbook.createCellStyle();
		style.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);

		// フォントを太字に設定
		Font font = workbook.createFont();
		font.setBold(true);
		style.setFont(font);

		return style;
	}

	// 本文のセルスタイルを作成
	private CellStyle createBodyCellStyle(Workbook workbook) {
		CellStyle style = workbook.createCellStyle();
		style.setAlignment(HorizontalAlignment.CENTER);
		return style;
	}

	// 選択範囲のセルを作成。
	public void createCellToRange(Sheet sheet, int startRow, int endRow, int startCol, int endCol,
			Consumer<Cell> function) {
		// 範囲内のすべてのセルを確認し、必要に応じて枠線を設定 
		for (int rowIndex = startRow; rowIndex <= endRow; rowIndex++) {
			Row row = sheet.getRow(rowIndex);
			if (row == null) {
				row = sheet.createRow(rowIndex); // 行が存在しない場合は作成
			}
			for (int colIndex = startCol; colIndex <= endCol; colIndex++) {
				Cell cell = row.getCell(colIndex);
				if (cell == null) {
					cell = row.createCell(colIndex); // セルが存在しない場合は作成
				}
				function.accept(cell); // セルに対してスタイルなどの処理を適用
			}
		}
	}

	// 選択範囲を枠線で囲む
	public void addBorderToRange(Sheet sheet, int startRow, int endRow, int startCol, int endCol) {
		createCellToRange(sheet, startRow, endRow, startCol, endCol, cell -> {
			CellStyle extractedStyle = cell.getCellStyle();
			Workbook workbook = sheet.getWorkbook();
			CellStyle newStyle = workbook.createCellStyle(); // 空のスタイルを用意
			newStyle.cloneStyleFrom(extractedStyle); // newStyleにコピー
			// 行番号を取得
			Row row = cell.getRow();
			int rowIndex = row.getRowNum(); // 行番号
			// 列番号を取得
			int colIndex = cell.getColumnIndex(); // 列番号

			// 境界線のスタイルを設定
			if (rowIndex == startRow) {
				newStyle.setBorderTop(BorderStyle.THIN); // 上枠線
			}
			if (rowIndex == endRow) {
				newStyle.setBorderBottom(BorderStyle.THIN); // 下枠線
			}
			if (colIndex == startCol) {
				newStyle.setBorderLeft(BorderStyle.THIN); // 左枠線
			}
			if (colIndex == endCol) {
				newStyle.setBorderRight(BorderStyle.THIN); // 右枠線
			}
			cell.setCellStyle(newStyle); // スタイルをセルに設定
		});
	}

	// ヘッダー行を作成
	private void createHeaderRow(Sheet sheet, CellStyle headerStyle) {
		Row headerRow = sheet.createRow(1);

		// ヘッダーの内容を各セルに設定
		for (int i = 0; i < HEADER_CONTENT.length; i++) {
			Cell cell = headerRow.createCell(i + 1);
			cell.setCellValue(HEADER_CONTENT[i]);
			cell.setCellStyle(headerStyle);
		}

		// B2からC2を結合
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 1, 2));
	}

	// 注文データをシートに追加
	private void populateOrderData(Sheet sheet, List<OrderRecordDto> orderList, CellStyle cellStyle) {
		int rowNum = 2; // データ開始行

		for (OrderRecordDto order : orderList) {
			Row row = sheet.createRow(rowNum);

			// 注文データの内容を設定
			String[] bodyContent = {
					order.getDishName(), "", order.getPrice() + "円", order.getQuantity() + "個"
			};

			// 各セルにデータをセット
			for (int i = 0; i < bodyContent.length; i++) {
				Cell cell = row.createCell(i + 1);
				cell.setCellValue(bodyContent[i]);
				cell.setCellStyle(cellStyle);
			}

			// 商品名のセルを結合
			sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 1, 2));
			rowNum++;
		}
	}

	// 合計金額の行を作成
	private void createTotalRow(Sheet sheet, int rowNum, int totalPrice) {
		Row totalRow = sheet.createRow(rowNum);
		totalRow.createCell(1).setCellValue("合計金額：" + totalPrice + "円");
		sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 1, 2));
	}

	// Excelファイルとして書き出し
	private void writeFile(Workbook workbook, String filePath) throws IOException {
		File file = new File(filePath);
		File parentDir = file.getParentFile();

		// 親ディレクトリが存在しない場合は作成
		if (!parentDir.exists()) {
			parentDir.mkdirs();
		}

		// ファイル出力ストリームで書き込む
		try (FileOutputStream fileOut = new FileOutputStream(file)) {
			workbook.write(fileOut);
			System.out.println("会計内容がExcelファイルとして出力されました。");
		} catch (IOException e) {
			System.out.println("ファイルの出力に失敗しました: " + e.getMessage());
			throw e;
		}
	}
}
