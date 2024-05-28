const express = require("express");
const router = express.Router();
const noteController = require("../controllers/note");



router.post('/addnote', noteController.addNote);
router.get('/getnotes', noteController.getNotes);


module.exports = router;