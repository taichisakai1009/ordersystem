package com.example.demo.TwoFactorAuth;

import java.io.ByteArrayOutputStream; // バイト配列への出力をサポートするストリーム

import com.google.zxing.BarcodeFormat; // バーコードの形式を指定するためのクラス
import com.google.zxing.client.j2se.MatrixToImageWriter; // BitMatrixを画像に変換するためのクラス
import com.google.zxing.common.BitMatrix; // QRコードのビットデータを保持するクラス
import com.google.zxing.qrcode.QRCodeWriter; // QRコードのエンコードを行うクラス

/**
 * QRコード生成を担当するユーティリティクラス。
 * 
 * 外部ライブラリ ZXing (Zebra Crossing) を使用してQRコードを生成し、
 * 画像としてバイト配列の形式で出力する。
 */
public class QRCodeGenerator {

    /**
     * QRコード画像を生成するメソッド
     *
     * @param qrCodeUrl QRコードに埋め込むURLや文字列
     * @param width     QRコードの幅 (ピクセル単位)
     * @param height    QRコードの高さ (ピクセル単位)
     * @return 生成されたQRコード画像をPNG形式のバイト配列として返す
     */
    public static byte[] generateQRCodeImage(String qrCodeUrl, int width, int height) {
        try {
            // QRCodeWriterインスタンスを作成
            QRCodeWriter qrCodeWriter = new QRCodeWriter();

            // QRコードの内容をBitMatrix形式にエンコード
            BitMatrix bitMatrix = qrCodeWriter.encode(
                qrCodeUrl,            // QRコードに埋め込むURLやデータ
                BarcodeFormat.QR_CODE, // QRコード形式として指定
                width,                // QRコードの幅
                height                // QRコードの高さ
            );

            // BitMatrixを画像に変換し、バイト配列に書き込むための出力ストリーム
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            // BitMatrixをPNG形式の画像としてストリームに書き込む
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);

            // 出力ストリームの内容をバイト配列として返す
            return outputStream.toByteArray();
        } catch (Exception e) {
            // 例外が発生した場合はRuntimeExceptionとしてスロー
            throw new RuntimeException("Failed to generate QR code", e);
        }
    }
}
