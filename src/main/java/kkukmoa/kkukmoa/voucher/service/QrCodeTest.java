package kkukmoa.kkukmoa.voucher.service;

import kkukmoa.kkukmoa.common.util.QrCodeUtil;

import java.io.FileWriter;
import java.io.IOException;

public class QrCodeTest {
    public static void main(String[] args) throws IOException {
        // 테스트할 문자열 (예: 금액권 UUID 등)
        String qrInfo = "voucher_test_123456";

        // Base64 QR 코드 생성
        String base64 = QrCodeUtil.qrCodeToBase64(qrInfo);

        // HTML 파일로 저장 (브라우저에서 열어보기)
        String html = "<html><body>"
                + "<h1>QR 코드 미리보기</h1>"
                + "<img src=\"data:image/png;base64," + base64 + "\" />"
                + "</body></html>";

        FileWriter writer = new FileWriter("qr_test.html");
        writer.write(html);
        writer.close();

        System.out.println("✅ qr_test.html 파일이 생성되었습니다. 브라우저에서 열어보세요!");
    }
}

