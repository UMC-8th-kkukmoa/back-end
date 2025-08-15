package kkukmoa.kkukmoa.payment.controller;

import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "토스 결제 위젯 API", description = "토스 결제 위젯 API 입니다.")
@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/v1/payments/toss")
public class PaymentViewController {
    @Value("${toss.client-key}")
    private String tossClientKey;

    @GetMapping("/view")
    public String tossPage(
            @RequestHeader(name = "Authorization") String authHeader,
            @RequestParam("unitPrice") int unitPrice,
            @RequestParam("quantity") int quantity,
            Model model
    ) {
        model.addAttribute("token", authHeader);
        model.addAttribute("clientKey", tossClientKey);
        model.addAttribute("unitPrice", unitPrice);
        model.addAttribute("quantity", quantity);
        return "TossPayment";
    }


}
