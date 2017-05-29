var express = require('express');
var router = express.Router();

/* GET home page. */
router.get('/', function(req, res, next) {
  var clname = req.session.clname;
  if(!clname) {
    res.render('index');
  } else {
    res.render('className', {clname : clname});
  }
});

router.post('/class', function(req, res, next){
  req.session.clname = req.body.clname;
  res.redirect('/');
});

router.get('/room', function(req, res, next){
  res.render('realClass');
});

module.exports = router;
