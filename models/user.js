const mongoose = require('mongoose');

// Define schema
const userSchema = new mongoose.Schema({

    name: {
        type: String,
        required: true
    },
    email: {
        type: String,
        required: true,
        unique: true
    },
    password: {
        type: String,
        required: true
    },
   
    token: {
        type: String
    }
});





// Create model
const User = mongoose.model('User', userSchema);

module.exports = User;