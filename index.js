const express = require("express");
const mongoose = require("mongoose");
const cors = require("cors");
const dotenv = require("dotenv");
const path = require("path");

const app = express();
app.use(cors());
app.use(express.json());

dotenv.config({ path: "./.env" });


mongoose
  .connect(process.env.MONGODB_ATLAS_URL, {
    
  })
  .then(() => {
    console.log("MyDB is connected");
  })
  .catch((err) => {
    console.error("Failed to connect to MongoDB", err);
  });



  


app.listen(process.env.SERVER_PORT, () => {
  console.log("server is running on port: "+process.env.SERVER_PORT);
});



app.use("/note", require("./routes/note"));
app.use("/auth", require("./routes/auth"));
