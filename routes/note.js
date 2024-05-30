const express = require("express");
const router = express.Router();
const noteController = require("../controllers/note");



router.post('/addnote', noteController.addNote);
router.get('/getnotes', noteController.getNotes);
router.get('/getnotebyid', noteController.getNoteById);
router.post('/deletenote', noteController.deleteNote);


module.exports = router;