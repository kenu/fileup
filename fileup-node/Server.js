// https://github.com/node-formidable/formidable
const express = require("express");
const formidable = require("formidable");

const app = express();

app.get("/", (_req, res) => {
  res.sendFile(__dirname + "/index.html");
});

app.post("/api/photo", (req, res, next) => {
  console.log(req.files);
  const form = formidable({
    uploadDir: __dirname + "/uploads/",
    filename: Date.now(),
  });

  form.parse(req, (err, fields, files) => {
    if (err) {
      next(err);
      return;
    }
    res.json({ fields, files });
  });
});

app.listen(3000, function () {
  console.log("Working on port 3000");
});
