const express = require("express");
const router = express.Router();
const noteController = require("../controllers/note");



router.post('/addnote', noteController.addNote);
router.get('/getnotes', noteController.getNotes);
router.get('/getnotebyid', noteController.getNoteById);


module.exports = router;