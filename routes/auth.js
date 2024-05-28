const express = require("express");
const router = express.Router();
const authController = require("../controllers/auth");



router.post('/registration', authController.Registration)
router.post('/login', authController.Login)
router.post('/logout', authController.Logout)




module.exports = router;