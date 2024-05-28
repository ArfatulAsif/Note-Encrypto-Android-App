const mongoose = require('mongoose')


const noteSchema = new mongoose.Schema({

    user_id : {
        type: String,
        require: true
    },

    topic_name : {
        type: String
    },

    note_text : {
        type : String 
    }

});


const Note = mongoose.model('Note', noteSchema);


module.exports = Note;