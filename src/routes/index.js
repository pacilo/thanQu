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

router.get('/room', function(req, res, next){
  res.render('realClass');
});

module.exports = router;
