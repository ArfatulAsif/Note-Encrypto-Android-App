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
  .connect(process.env.MONGODB_ATLAS_URL)
  .then((err) => {
    console.log("MyDB is connected");
  })
  .catch((err) => {
    console.log("Check your internet connection");
  });


app.listen(process.env.SERVER_PORT, () => {
  console.log("server is running");
});



app.use("/auth", require("./routes/auth"));
