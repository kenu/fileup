// https://github.com/expressjs/multer/blob/master/doc/README-ko.md
var express = require('express');
var formidable = require('formidable');

var app = express();

app.get('/', (req, res) => {
  res.sendFile(__dirname + '/index.html');
});

app.post('/api/photo', (req, res, next) => {
  console.log(req.files);
  const form = formidable({
    uploadDir: __dirname + '/uploads/',
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
  console.log('Working on port 3000');
});
