const Note = require('../models/note')
const User = require('../models/user');
const bcrypt = require("bcryptjs");
const jwt = require("jsonwebtoken");



exports.addNote = async (req, res) => {
  try {
    const { topic_name, note_text } = req.body;
    const authHeader = req.headers.authorization;

    if (!authHeader || !authHeader.startsWith('Bearer ')) {
      return res.status(400).json({ message: "Authorization token is required" });
    }

    const token = authHeader.split(' ')[1];

    // Validate input
    if (!token || !note_text) {
      return res.status(400).json({ message: "Token and note text are required" });
    }

    // Find the user by token
    const user = await User.findOne({ token });
    if (!user) {
      return res.status(401).json({ message: "Invalid token" });
    }

    // Create a new note
    const newNote = new Note({
      user_id: user._id,
      topic_name,
      note_text
    });

    // Save the note to the database
    await newNote.save();

    res.status(201).json({ message: "Note added successfully", note: newNote });
  } catch (error) {
    console.error(error);
    res.status(500).json({ message: "Internal Server Error" });
  }
};






exports.getNotes = async (req, res) => {
    try {
      const authHeader = req.headers.authorization;
  
      if (!authHeader || !authHeader.startsWith('Bearer ')) {
        return res.status(400).json({ message: "Authorization token is required" });
      }
  
      const token = authHeader.split(' ')[1];
  
      // Find the user by token
      const user = await User.findOne({ token });
      if (!user) {
        return res.status(401).json({ message: "Invalid token" });
      }
  
      // Find all notes by user_id
      const notes = await Note.find({ user_id: user._id });
  
      res.status(200).json({ notes });
    } catch (error) {
      console.error(error);
      res.status(500).json({ message: "Internal Server Error" });
    }
  };