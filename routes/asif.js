const express = require('express');
const router = express.Router();
const axios = require("axios");

/*{  // Send these fix values from Android Activity.kt pages for apis where needed

   "appId": "APP_118909",
   "password": "32cda28df843036ce96e051e93c35599",
   "mobile": "8801688433248"
}
*/

router.post("/subscription/otp/request", async (req, res) => {
    const { appId, password, mobile } = req.body;
    try {
        const requestData = {
            applicationId: `${appId}`,
            password: `${password}`,
            subscriberId: `tel:${mobile}`,
            applicationHash: "abcde",
            applicationMetaData: {
                client: "MOBILEAPP",
                device: "Samsung S10",
                os: "android 8",
                appCode:
                    "https://play.google.com/store/apps/details?id=lk.dialog.megarunlor",
            },
        };

        const response = await axios.post(
            "https://developer.bdapps.com/subscription/otp/request",
            requestData
        );

        res.status(response.status).json({
            statusCode: response.data.statusCode,
            statusDetail: response.data.statusDetail,
            referenceNo: response.data.referenceNo,
            version: response.data.version,
        });
    } catch (error) {
        res.status(500).json({
            error: "An error occurred while making the request",
        });
    }
});
// test 


router.post("/subscription/otp/verify", async (req, res) => {
    const { appId, password, referenceNo, otp } = req.body;
    try {
        const requestData = {
            applicationId: `${appId}`,
            password: `${password}`,
            referenceNo: referenceNo,
            otp:otp,
        };

        const response = await axios.post(
            "https://developer.bdapps.com/subscription/otp/verify",
            requestData
        );

        res.status(response.status).json({
            statusCode: response.data.statusCode,
            version: response.data.version,
            subscriptionStatus:response.data.subscriptionStatus,
            statusDetail:response.data.statusDetail,
            subscriberId:response.status.subscriberId
        });
    } catch (error) {
        res.status(500).json({
            error: "An error occurred while making the request",
        });
    }
});





module.exports=router