var express = require('express');
var router = express.Router();
const fs = require('fs');
const ejs = require('ejs');
const pool = require('../config/db_pool');
// var aws = require('aws-sdk');
// aws.config.loadFromPath('./config/aws_config.json');


router.get('/classDetail/:questionID', function(req, res){
  questionID = req.params.questionID;
  console.log(questionID);
  pool.getConnection(function(error, connection){
console.log('들어왔어요');
    if (error)
        {
          console.log("getConnection Error" + error);
          res.sendStatus(503);
          //connection.release();
        }

    else {
      connection.query('select * from question where q_id =? and class_name = ?', [questionID, req.session.className], function(error, result)
    {
      if(error) console.log("selecting Error" + error);
      else {
  res.render('classDetail', {className : req.session.className, question : result});
connection.release();
      }

      });
}
    });
});


module.exports = router;
