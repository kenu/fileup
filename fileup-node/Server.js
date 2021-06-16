// https://github.com/expressjs/multer/blob/master/doc/README-ko.md
var express = require('express');
var multer = require('multer');
var storage = multer.diskStorage({
  destination: function (req, file, cb) {
    cb(null, './uploads/');
  },
  filename: function (req, file, cb) {
    cb(null, Date.now());
  }
});
var upload = multer({ storage: storage });
var app = express();

app.get('/', function (req, res) {
  res.sendFile(__dirname + '/index.html');
});

app.post('/api/photo', upload.array('userPhoto'), function (req, res) {
  console.log(req.files);
  res.end("File uploaded.\n" + JSON.stringify(req.files));
});

app.listen(3000, function () {
  console.log("Working on port 3000");
});
