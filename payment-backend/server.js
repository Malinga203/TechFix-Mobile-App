const express = require("express");
const cors = require("cors");
const crypto = require("crypto");
require("dotenv").config();

const app = express();

app.use(cors());
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

const MERCHANT_ID =
    process.env.PAYHERE_MERCHANT_ID;

const MERCHANT_SECRET =
    process.env.PAYHERE_MERCHANT_SECRET;

/*
 * Demo payment status store.
 *
 * Later this could be replaced with a real database.
 *
 * Example:
 * TECHFIX-1001 -> {
 *     status: "SUCCESS",
 *     paymentId: "...",
 *     amount: "5000.00"
 * }
 */
const paymentStatuses =
    new Map();


function md5(value) {

    return crypto
        .createHash("md5")
        .update(String(value))
        .digest("hex")
        .toUpperCase();
}


function formatAmount(amount) {

    return Number(amount)
        .toFixed(2);
}


// =========================================================
// HEALTH CHECK
// =========================================================

app.get("/", (req, res) => {

    res.json({
        success: true,
        message: "TechFix Payment Backend is running"
    });
});


// =========================================================
// GENERATE PAYHERE PAYMENT HASH
// =========================================================

app.post(
    "/api/payment/hash",
    (req, res) => {

        const body =
            req.body || {};

        const orderId =
            body.orderId;

        const amount =
            body.amount;

        const currency =
            body.currency;

        if (
            !orderId ||
            amount == null ||
            !currency
        ) {

            return res.status(400).json({
                success: false,
                message:
                    "orderId, amount and currency are required"
            });
        }

        if (
            !MERCHANT_ID ||
            !MERCHANT_SECRET
        ) {

            return res.status(500).json({
                success: false,
                message:
                    "PayHere credentials are not configured"
            });
        }

        const formattedAmount =
            formatAmount(amount);

        const hashedSecret =
            md5(
                MERCHANT_SECRET
            );

        const hash =
            md5(
                MERCHANT_ID +
                orderId +
                formattedAmount +
                currency +
                hashedSecret
            );

        /*
         * Create a pending payment record.
         */
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

        return res.json({
            success: true,
            merchantId: MERCHANT_ID,
            orderId: orderId,
            amount: formattedAmount,
            currency: currency,
            hash: hash
        });
    }
);


// =========================================================
// PAYHERE NOTIFICATION ENDPOINT
// =========================================================

app.post(
    "/api/payment/notify",
    (req, res) => {

        const body =
            req.body || {};

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
                .send("INVALID REQUEST");
        }

        if (
            merchantId !== MERCHANT_ID
        ) {

            console.log(
                "Merchant ID mismatch"
            );

            return res
                .status(400)
                .send("INVALID MERCHANT");
        }


        // -------------------------------------------------
        // VERIFY PAYHERE SIGNATURE
        // -------------------------------------------------

        const hashedSecret =
            md5(
                MERCHANT_SECRET
            );

        const localSignature =
            md5(
                merchantId +
                orderId +
                payhereAmount +
                payhereCurrency +
                statusCode +
                hashedSecret
            );

        const signatureValid =
            localSignature ===
            String(receivedSignature)
                .toUpperCase();


        if (!signatureValid) {

            console.log(
                "Invalid payment signature:",
                orderId
            );

            paymentStatuses.set(
                orderId,
                {
                    orderId: orderId,
                    status: "INVALID",
                    amount: payhereAmount,
                    currency: payhereCurrency,
                    paymentId: paymentId || null
                }
            );

            return res
                .status(400)
                .send("INVALID SIGNATURE");
        }


        // -------------------------------------------------
        // PAYHERE STATUS
        // -------------------------------------------------

        let paymentStatus;

        switch (
            String(statusCode)
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
        }


        paymentStatuses.set(
            orderId,
            {
                orderId: orderId,
                status: paymentStatus,
                amount: payhereAmount,
                currency: payhereCurrency,
                paymentId:
                    paymentId || null
            }
        );


        console.log(
            "PayHere payment notification"
        );

        console.log({
            orderId,
            paymentId,
            amount: payhereAmount,
            currency: payhereCurrency,
            statusCode,
            paymentStatus
        });


        /*
         * PayHere expects a successful HTTP response.
         */
        return res
            .status(200)
            .send("OK");
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

            return res.status(404).json({
                success: false,
                message:
                    "Payment order not found"
            });
        }

        return res.json({
            success: true,
            payment: payment
        });
    }
);


// =========================================================
// START SERVER
// =========================================================

const PORT =
    process.env.PORT || 3000;

app.listen(
    PORT,
    () => {

        console.log(
            `TechFix Payment Backend running on port ${PORT}`
        );
    }
);