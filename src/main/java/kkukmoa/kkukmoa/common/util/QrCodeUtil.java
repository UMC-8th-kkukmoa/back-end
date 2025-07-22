package kkukmoa.kkukmoa.common.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QrCodeUtil {

  // QR 종류 : 금액권, 쿠폰, 스탬프
  final static int QR_WIDTH = 200;
  final static int QR_HEIGHT = 200;
  final static String QR_FILETYPE = "png";

  /**
   *
   * QR 코드 생성하는 메서드
   * @param qrCodeInfo 은 변환할 QR코드의 정보
   * @return 은 QR 코드에 대한 byte[] 형태로 반환합니다.
   *  byte[] 형태의 데이터 -> DB 저장하여 관리하고,
   *  다른 계층에서 byte[] -> Base64 전환 후 사용자에게 반환합니다.
   *
   */
  // 문자열 -> byte[]
  public static byte[] makeQrCode(String qrCodeInfo) {

    try {
      // QR코드 생성 옵션 설정
      Map<EncodeHintType, Object> hintMap = new HashMap<>();
      hintMap.put(EncodeHintType.MARGIN, 0);
      hintMap.put(EncodeHintType.CHARACTER_SET,"UTF-8");

      // QR 코드 생성
      QRCodeWriter qrCodeWriter = new QRCodeWriter();
      BitMatrix bitMatrix = qrCodeWriter.encode(qrCodeInfo, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hintMap);

      // QR 코드 이미지 생성
      BufferedImage qrCodeImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

      // QR 코드 이미지를 바이트 배열로 변환, byteArrayOutputStream 에 저장
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      ImageIO.write(qrCodeImage,QR_FILETYPE, byteArrayOutputStream);
      byteArrayOutputStream.flush();

      byte[] qrCodeBytes = byteArrayOutputStream.toByteArray();
      byteArrayOutputStream.close();

      return qrCodeBytes;

    } catch (Exception e) {
      log.warn("QR 코드 생성 실패: " + e.getMessage());
    }

    return null;
  }

  // byte[] -> String ( Base64 인코딩 )
  public static String qrCodeToBase64(byte[] qrCodeBytes) {
    return Base64.getEncoder().encodeToString(qrCodeBytes);
  }



}
