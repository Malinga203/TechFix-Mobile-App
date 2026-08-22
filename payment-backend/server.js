const express = require("express");
const cors = require("cors");
const crypto = require("crypto");
require("dotenv").config();

const app = express();

app.use(cors());
app.use(express.json());
app.use(
    express.urlencoded({
        extended: true
    })
);

const MERCHANT_ID =
    process.env.PAYHERE_MERCHANT_ID;

const MERCHANT_SECRET =
    process.env.PAYHERE_MERCHANT_SECRET;

const paymentStatuses =
    new Map();


// =========================================================
// MD5
// =========================================================

function md5(value) {

    return crypto
        .createHash("md5")
        .update(String(value))
        .digest("hex")
        .toUpperCase();
}


// =========================================================
// FORMAT AMOUNT
// =========================================================

function formatAmount(amount) {

    return Number(amount)
        .toFixed(2);
}


// =========================================================
// HEALTH CHECK
// =========================================================

app.get(
    "/",
    (req, res) => {

        res.json({
            success: true,
            message:
                "TechFix Payment Backend is running"
        });
    }
);


// =========================================================
// GENERATE PAYHERE PAYMENT HASH
// =========================================================

app.post(
    "/api/payment/hash",
    (req, res) => {

        const body =
            req.body || {};

        console.log(
            "================================="
        );

        console.log(
            "PAYMENT HASH REQUEST"
        );

        console.log(
            "Body:",
            body
        );

        console.log(
            "================================="
        );


        const orderId =
            body.orderId;

        const amount =
            body.amount;

        const currency =
            body.currency;


        // =====================================================
        // VALIDATE REQUEST
        // =====================================================

        if (
            !orderId ||
            amount == null ||
            !currency
        ) {

            return res
                .status(400)
                .json({
                    success: false,
                    message:
                        "orderId, amount and currency are required"
                });
        }


        // =====================================================
        // CHECK ENV
        // =====================================================

        if (
            !MERCHANT_ID ||
            !MERCHANT_SECRET
        ) {

            console.log(
                "PayHere credentials missing"
            );

            return res
                .status(500)
                .json({
                    success: false,
                    message:
                        "PayHere credentials are not configured"
                });
        }


        // =====================================================
        // FORMAT AMOUNT
        // =====================================================

        const formattedAmount =
            formatAmount(
                amount
            );


        // =====================================================
        // HASH SECRET
        // =====================================================

        const hashedSecret =
            md5(
                MERCHANT_SECRET
            );


        // =====================================================
        // PAYMENT HASH
        // =====================================================

        const hash =
            md5(
                MERCHANT_ID +
                orderId +
                formattedAmount +
                currency +
                hashedSecret
            );


        // =====================================================
        // STORE PENDING PAYMENT
        // =====================================================

        paymentStatuses.set(
            orderId,
            {
                orderId: orderId,
                status: "PENDING",
                amount: formattedAmount,
                currency: currency,
                paymentId: null
            }
        );


        console.log(
            "Payment order created"
        );

        console.log({
            merchantId:
                MERCHANT_ID,

            orderId:
                orderId,

            amount:
                formattedAmount,

            currency:
                currency,

            status:
                "PENDING"
        });


        return res.json({
            success: true,
            merchantId:
                MERCHANT_ID,
            orderId:
                orderId,
            amount:
                formattedAmount,
            currency:
                currency,
            hash:
                hash
        });
    }
);


// =========================================================
// TEST NOTIFY SIGNATURE
// =========================================================

app.post(
    "/api/payment/test-signature",
    (req, res) => {

        const body =
            req.body || {};

        const orderId =
            body.orderId;

        const amount =
            body.amount;

        const currency =
            body.currency;

        const statusCode =
            body.statusCode;


        if (
            !orderId ||
            amount == null ||
            !currency ||
            statusCode == null
        ) {

            return res
                .status(400)
                .json({
                    success: false,
                    message:
                        "orderId, amount, currency and statusCode are required"
                });
        }


        if (
            !MERCHANT_ID ||
            !MERCHANT_SECRET
        ) {

            return res
                .status(500)
                .json({
                    success: false,
                    message:
                        "PayHere credentials are not configured"
                });
        }


        const formattedAmount =
            formatAmount(
                amount
            );


        const hashedSecret =
            md5(
                MERCHANT_SECRET
            );


        const md5sig =
            md5(
                MERCHANT_ID +
                orderId +
                formattedAmount +
                currency +
                statusCode +
                hashedSecret
            );


        return res.json({
            success: true,
            merchantId:
                MERCHANT_ID,
            orderId:
                orderId,
            amount:
                formattedAmount,
            currency:
                currency,
            statusCode:
                String(
                    statusCode
                ),
            md5sig:
                md5sig
        });
    }
);


// =========================================================
// PAYHERE NOTIFICATION ENDPOINT
// =========================================================

app.post(
    "/api/payment/notify",
    (req, res) => {

        console.log(
            "\n================================="
        );

        console.log(
            "PAYHERE NOTIFY RECEIVED"
        );

        console.log(
            "Content-Type:",
            req.headers["content-type"]
        );

        console.log(
            "Request body:",
            req.body
        );

        console.log(
            "================================="
        );


        const body =
            req.body || {};


        // =====================================================
        // READ PAYHERE VALUES
        // =====================================================

        const merchantId =
            body.merchant_id;

        const orderId =
            body.order_id;

        const paymentId =
            body.payment_id;

        const payhereAmount =
            body.payhere_amount;

        const payhereCurrency =
            body.payhere_currency;

        const statusCode =
            body.status_code;

        const receivedSignature =
            body.md5sig;


        // =====================================================
        // DEBUG EACH VALUE
        // =====================================================

        console.log(
            "merchantId =",
            merchantId
        );

        console.log(
            "orderId =",
            orderId
        );

        console.log(
            "paymentId =",
            paymentId
        );

        console.log(
            "payhereAmount =",
            payhereAmount
        );

        console.log(
            "payhereCurrency =",
            payhereCurrency
        );

        console.log(
            "statusCode =",
            statusCode
        );

        console.log(
            "md5sig =",
            receivedSignature
        );


        // =====================================================
        // VALIDATE REQUEST
        // =====================================================

        if (
            !merchantId ||
            !orderId ||
            !payhereAmount ||
            !payhereCurrency ||
            statusCode == null ||
            !receivedSignature
        ) {

            console.log(
                "Incomplete PayHere notification"
            );


            return res
                .status(400)
                .json({
                    success: false,
                    message:
                        "INVALID REQUEST",
                    received: {
                        merchantId:
                            merchantId || null,

                        orderId:
                            orderId || null,

                        paymentId:
                            paymentId || null,

                        payhereAmount:
                            payhereAmount || null,

                        payhereCurrency:
                            payhereCurrency || null,

                        statusCode:
                            statusCode ?? null,

                        md5sig:
                            receivedSignature || null
                    }
                });
        }


        // =====================================================
        // MERCHANT CHECK
        // =====================================================

        if (
            String(
                merchantId
            )
                !==
            String(
                MERCHANT_ID
            )
        ) {

            console.log(
                "Merchant ID mismatch"
            );

            console.log(
                "Received:",
                merchantId
            );

            console.log(
                "Expected:",
                MERCHANT_ID
            );


            return res
                .status(400)
                .json({
                    success: false,
                    message:
                        "INVALID MERCHANT"
                });
        }


        // =====================================================
        // HASH MERCHANT SECRET
        // =====================================================

        const hashedSecret =
            md5(
                MERCHANT_SECRET
            );


        // =====================================================
        // GENERATE EXPECTED SIGNATURE
        // =====================================================

        const localSignature =
            md5(
                merchantId +
                orderId +
                payhereAmount +
                payhereCurrency +
                statusCode +
                hashedSecret
            );


        const normalizedReceivedSignature =
            String(
                receivedSignature
            )
                .trim()
                .toUpperCase();


        console.log(
            "Received signature:",
            normalizedReceivedSignature
        );

        console.log(
            "Generated signature:",
            localSignature
        );


        // =====================================================
        // VERIFY SIGNATURE
        // =====================================================

        const signatureValid =
            localSignature ===
            normalizedReceivedSignature;


        if (!signatureValid) {

            console.log(
                "Invalid PayHere signature"
            );


            paymentStatuses.set(
                orderId,
                {
                    orderId:
                        orderId,

                    status:
                        "INVALID",

                    amount:
                        payhereAmount,

                    currency:
                        payhereCurrency,

                    paymentId:
                        paymentId || null
                }
            );


            return res
                .status(400)
                .json({
                    success: false,
                    message:
                        "INVALID SIGNATURE",
                    receivedSignature:
                        normalizedReceivedSignature,
                    generatedSignature:
                        localSignature
                });
        }


        // =====================================================
        // CONVERT PAYHERE STATUS
        // =====================================================

        let paymentStatus;


        switch (
            String(
                statusCode
            )
        ) {

            case "2":

                paymentStatus =
                    "SUCCESS";

                break;


            case "0":

                paymentStatus =
                    "PENDING";

                break;


            case "-1":

                paymentStatus =
                    "CANCELED";

                break;


            case "-2":

                paymentStatus =
                    "FAILED";

                break;


            case "-3":

                paymentStatus =
                    "CHARGEDBACK";

                break;


            default:

                paymentStatus =
                    "UNKNOWN";

                break;
        }


        // =====================================================
        // STORE PAYMENT STATUS
        // =====================================================

        paymentStatuses.set(
            orderId,
            {
                orderId:
                    orderId,

                status:
                    paymentStatus,

                amount:
                    payhereAmount,

                currency:
                    payhereCurrency,

                paymentId:
                    paymentId || null
            }
        );


        // =====================================================
        // LOG SUCCESS
        // =====================================================

        console.log(
            "PayHere notification verified"
        );

        console.log({
            orderId:
                orderId,

            paymentId:
                paymentId,

            amount:
                payhereAmount,

            currency:
                payhereCurrency,

            statusCode:
                statusCode,

            paymentStatus:
                paymentStatus
        });


        // =====================================================
        // RESPONSE
        // =====================================================

        return res
            .status(200)
            .send(
                "OK"
            );
    }
);


// =========================================================
// GET PAYMENT STATUS
// =========================================================

app.get(
    "/api/payment/status/:orderId",
    (req, res) => {

        const orderId =
            req.params.orderId;


        const payment =
            paymentStatuses.get(
                orderId
            );


        if (!payment) {

            return res
                .status(404)
                .json({
                    success: false,
                    message:
                        "Payment order not found"
                });
        }


        return res.json({
            success: true,
            payment:
                payment
        });
    }
);


// =========================================================
// DEBUG ALL PAYMENTS
// =========================================================

app.get(
    "/api/payment/debug/all",
    (req, res) => {

        const payments =
            Array.from(
                paymentStatuses.values()
            );


        return res.json({
            success: true,
            count:
                payments.length,
            payments:
                payments
        });
    }
);


// =========================================================
// START SERVER
// =========================================================

const PORT =
    process.env.PORT ||
    3000;


app.listen(
    PORT,
    () => {

        console.log(
            "================================="
        );

        console.log(
            "TechFix Payment Backend"
        );

        console.log(
            `Running on port ${PORT}`
        );

        console.log(
            "Merchant ID loaded:",
            MERCHANT_ID
                ? "YES"
                : "NO"
        );

        console.log(
            "Merchant Secret loaded:",
            MERCHANT_SECRET
                ? "YES"
                : "NO"
        );

        console.log(
            "================================="
        );
    }
);