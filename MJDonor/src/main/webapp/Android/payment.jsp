<!DOCTYPE html>
<html lang="en">
<head>
    <%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
    <link rel="icon" type="image/svg+xml" href="/vite.svg" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>결제위젯 샘플</title>
    <script src="https://js.tosspayments.com/v1/payment-widget"></script>
</head>
<body>
<div id="payment-method"></div>
<button id="payment-request-button">결제하기</button>
<script>
    const paymentWidget = PaymentWidget(
        "test_ck_D4yKeq5bgrpdjYLeeo4rGX0lzW6Y",
        // 비회원 customerKey
        PaymentWidget.ANONYMOUS
    );

    /**
     * 결제창을 렌더링합니다.
     * @docs https://docs.tosspayments.com/reference/widget-sdk#renderpaymentmethods%EC%84%A0%ED%83%9D%EC%9E%90-%EA%B2%B0%EC%A0%9C-%EA%B8%88%EC%95%A1
     */
    paymentWidget.renderPaymentMethods("#payment-method", { value: 100 });

    const paymentRequestButton = document.getElementById(
        "payment-request-button"
    );
    paymentRequestButton.addEventListener("click", () => {
        /** 결제 요청
         * @docs https://docs.tosspayments.com/reference/widget-sdk#requestpayment%EA%B2%B0%EC%A0%9C-%EC%A0%95%EB%B3%B4
         */
        paymentWidget.requestPayment({
            orderId: generateRandomString(),
            orderName: '프로젝트', //프로젝트 명 받아오
            customerName: "이상훈",
            dueDate: '2023-08-22',
            successUrl: window.location.origin + "/MJDonor/Android/success.jsp",
            failUrl: window.location.origin + "/MJDonor/Android/fail.jsp",
        });
    });

    function generateRandomString() {
        return window.btoa(Math.random()).slice(0, 20);
    }
</script>
</body>
</html>
