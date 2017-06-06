var express = require('express');
var router = express.Router();
const fs = require('fs');
const ejs = require('ejs');
const pool = require('../config/db_pool');
// var aws = require('aws-sdk');
// aws.config.loadFromPath('./config/aws_config.json');

/* GET home page. */
// router.get('/', function(req, res, next) {
//     res.render('index');
// });

router.get('/', function(req, res, next) {
  fs.readFile('./views/index.ejs', 'utf-8', function(err, result) {
    if (err)
    console.log('reading ejs error : ', err);
    else
    res.status(200).send(ejs.render(result));
  });
});

router.post('/classConfirm', function(req, res) {

  function myRandom(max, min) {
    return Math.floor(Math.random()*(max-min)) + min;
  }
  var rand = myRandom(1000,9999);

  console.log('연결');
  req.session.className = req.body.className + rand;
  className=req.session.className;
  likeMinimum = req.body.likeMinimum;
  if (likeMinimum.length <= 0)
  likeMinimum = 0;

  console.log(className);
  console.log(likeMinimum);

  pool.getConnection(function(error, connection){
    console.log('들어왔어요');
    if (error)
    {
      console.log("getConnection Error" + error);
      res.sendStatus(503);
    }

    else {
      connection.query('select count(*) as exist from class where className = ?', [className], function(error, row)
      {
        if(error)
        {
          console.log("getConnection Error" + error);
          res.sendStatus(503);
        }
        else
        {
          if(row[0].exist != 0)
          {
            console.log('현존하는 강의가 존재합니다.');
            res.render('index');
          }

          else
          {
            connection.query('insert into class values(null,?,?,?)', [rand, className, likeMinimum], function(error, comments)
            {
              console.log(comments);
              if(error) console.log('selecting query err: ', error);
              else{
                res.render('className', {className : req.session.className});
                connection.release();
              }
            });
          }
        }
      });
    }
  });
});



router.get('/questionList/:className', function(req, res){
  pool.getConnection(function(error, connection){
    console.log('들어왔어요');
    if (error)
    {
      console.log("getConnection Error" + error);
      res.sendStatus(503);
      //connection.release();
    } else {
      connection.query('select id from class where className =?', req.session.className, function(error, row)
      {
        if(error) console.log("selecting Error" + error);
        else {
          console.log(row[0].id);
          req.session.classID = row[0].id;
          connection.query('select id, classID, userID, content, time, commentCnt, likeCnt from question where classID = ?', [row[0].id] , function(error, result) {
            if(error) console.log("selecting Error" + error);
            else
            {
              if(result.length == 0){
                console.log('텅텅 비었어요');
                res.render('realclass', {className : req.session.className, question : null});
              }
              else{
                console.log(result);
                res.render('realClass', {className : req.session.className, question : result});
              }
              connection.release();
            }
          });
        }

      });
    }
  });
});
//
// router.get('/classDetail/:questionID', function(req, res){
//   questionID = req.params.questionID;
//   console.log(questionID);
//   console.log(req.session.className);
//   pool.getConnection(function(error, connection){
//     console.log('들어왔어요');
//     if (error)
//     {
//       console.log("getConnection Error" + error);
//       res.sendStatus(503);
//       //connection.release();
//     } else {
//       connection.query('select * from question where id = ?', [questionID], function(error, result)
//       {
//         if(error) console.log("selecting Error" + error);
//         else {
//           console.log(result);
//           res.render('classDetail', {className : req.session.className, question : result});
//         }
//         connection.release();
//       });
//     }
//   });
// });

router.get('/classDetail/:questionID', function(req, res){
  questionID = req.params.questionID;
  className = req.session.className;
  classId = req.session.classID;
  console.log(questionID);
  console.log(classId);
  pool.getConnection(function(error, connection){
    console.log('들어왔어요');
    if (error)
    {
      console.log("getConnection Error" + error);
      res.sendStatus(503);
      //connection.release();
    }

    else {
      connection.query('select * from question where question.id=?', [questionID], function(error, questionData)
      {
        if(error) console.log("selecting Error" + error);
        else {
          console.log('넘어갑니다.');

          connection.query('select * from comment where comment.questionID=?', [questionID], function(error, comments) {
            console.log(questionData);
            console.log(comments);
            res.render('classDetail', {className : req.session.className, questionInfo : questionData, commentsInfo : comments});
          })
        }
      });
    }
    connection.release();
  });
});

router.post('/end', function(req, res){
  req.session.destroy(function(err){
    if(err) console.error('err', err);
    res.redirect('/');
  });
});


router.post('/api/join', function(req, res){
  console.log(req.body.id);
  console.log(req.body.pw);
  console.log(req.body.email);
  if (req.body.id == null || req.body.pw == null || req.body.email == null) {
    res.json({success:false});
  } else {
    pool.getConnection(function(error, connection){
      if (error)
      {
        console.log("getConnection Error" + error);
        res.sendStatus(503);
      } else {
        connection.query('insert into user values(null, ?, ?, ?)', [req.body.id, req.body.pw, req.body.email], function(err, result) {
          console.log(result);
          if (err != null) {
            console.log('selecting query err: ', err);
            res.json({success:false, message:"중복된 정보가 존재합니다."});
          } else {
            res.json({success:true});
          }
        });
      }
      connection.release();
    });
  }
});

module.exports = router;
