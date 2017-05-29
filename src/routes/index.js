var express = require('express');
var router = express.Router();

/* GET home page. */
router.get('/', function(req, res, next) {
    res.render('index');
});

router.post('/class', function(req, res, next){
  req.session.clname = req.body.clname;
  res.render('className', {clname : req.session.clname});
});

router.post('/room', function(req, res, next){
  res.render('realClass',{calname : req.session.clname});
});

router.post('/end', function(req, res, next){
  req.session.destroy(function(){
    req.session;
  });
  res.redirect('/');
});
module.exports = router;
