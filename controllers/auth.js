const User = require("../models/user");
const bcrypt = require("bcryptjs");
const jwt = require("jsonwebtoken");



exports.Registration = async (req, res) => {
  try {
    const { name, email, password } = req.body;

    if (!name || !email || !password) {
        return res.status(400).json({ message: "All fields are required" });
    }

    const hashedPassword = await bcrypt.hash(password, 10);
    const existingUser = await User.findOne({ email });

    if (existingUser) {
      return res.status(401).json({ message: "Email already exists" });
    } else {
      await User.create({
        name,
        email,
        password: hashedPassword
      });

      return res.status(201).json({ message: "User registered successfully" });
    }
  } catch (error) {
    console.log(error);
    res.status(500).json({ message: "Internal Server Error BB" });
  }
};





exports.Login = async (req, res) => {
  try {
    const { email, password } = req.body;

    
    const user = await User.findOne({ email });
    if (!user) {
      return res.status(401).json({ message: "Email is wrong!" });
    }

    
    const isMatch = await bcrypt.compare(password, user.password);
    if (!isMatch) {
      return res.status(401).json({ message: "Password is wrong!" });
    }

    
    const token = jwt.sign(
      { userId: user._id, email: user.email },
      process.env.JWT_SECRET_KEY,
      { expiresIn: '30d' }
    );

    // Update the token in the user model
    await User.findByIdAndUpdate(
      user._id,
      { token: token },
      { new: true }
    );

    // Send response back to client, assuming no role and isLogin field
    res.status(200).json({
      token: token,
      name: user.name,
      email: user.email
    });

  } catch (error) {
    console.error(error);
    res.status(500).json({ message: "Internal Server Error" });
  }
};






exports.Logout = async (req, res) => {
  try {
    const { token } = req.body;

    // Ensure the token is provided
    if (!token) {
      return res.status(400).json({ message: "Token is required" });
    }

    // Find the user and update the token to an empty string
    const user = await User.findOneAndUpdate(
      { token: token },
      { $set: { token: "nothing" } },
      { new: true }
    );

    // If no user found, send a 404 response
    if (!user) {
      return res.status(404).json({ message: "User not found" });
    }

    // Send a successful logout response
    return res.status(200).json({ message: "Logout successful" });

  } catch (error) {
    console.error(error);
    return res.status(500).json({ message: "Internal Server Error" });
  }
};
