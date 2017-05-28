var express = require('express');
var router = express.Router();

/* GET home page. */
router.get('/', function(req, res, next) {
  res.render('index');
});
router.post('/class', function(req, res, next){
  res.render('className', {classname :  req.body.clname});
});

router.get('/room', function(req, res, next){
  res.render('realClass');
});

module.exports = router;
