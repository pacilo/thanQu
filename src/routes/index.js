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

/* restful api */
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

router.post('/api/login', function(req,res){
  console.log(req.body.id);
  console.log(req.body.pw);
  if(req.body.id ==null || req.body.pw==null){
    res.json({sucess:false});
  } else {
    pool.getConnection(function(error, connection){
      if (error)
      {
        console.log("getConnection Error"+ error);
        res.sendStatus(503);
      } else {
        connection.query('select * from user where userID=?',[req.body.id], function(error, result){
          if (error != null) {
            res.json({success:false, message:"Internal Error"});
          } else {
            console.log(result);
            if (result.length <= 0) {
              res.json({success:false, message:"입력하신 ID가 없습니다."});
            } else {
              if (result[0].userPW == req.body.pw) {
                req.session.userID = result[0].id;
                req.session.userName = req.body.id;
                res.json({success:true, message :"로그인 성공", userID:result[0].id});
              } else {
                res.json({success:false, message:"비밀번호가 일치하지 않습니다."});
              }
            }
          }
          connection.release();
        });
      }
    });
  }
});

router.post('/api/logout',function(req, res){
  req.session.destroy(function(){
    req.session;
  });
  res.json({success:true});
});

router.get('/api/classList', function(req, res) {
  console.log('API: get classlist');
  pool.getConnection(function(error, connection) {
    if (error) {
      console.log('db connection failed: ' + error);
      res.sendStatus(503);
    } else {
      connection.query('select * from class', function(err, result) {
        console.log(result);
        if (err == null) {
          res.json({
            success:true,
            count:result.length,
            classinfos:result
          });
        } else {
          res.json({success:false});
        }
      });
      connection.release();
    }
  });
});

router.post('/api/makeQuestion', function(req, res) {
  console.log('API: post makeQuestion');
  console.log(req.body.classID);
  console.log(req.body.userID);
  console.log(req.body.content);
  pool.getConnection(function(error, connection) {
    if (error) {
      console.log('db connection failed: ' + error);
      res.sendStatus(503);
    } else {
      connection.query('insert into question (classID, userID, content) values (?, ?, ?)', [req.body.classID, req.body.userID, req.body.content], function(err, result) {
        console.log(result);
        if (err != null) {
          console.log('insert query err: ', err);
          res.json({success:false});
        } else {
          res.json({success:true});
        }
      });
      connection.release();
    }
  });
});

router.get('/api/questionList/:classID', function(req, res) {
  console.log('API: get questinoList');
  console.log(req.params.classID);
  pool.getConnection(function(error, connection) {
    if (error) {
      console.log('db connection failed: ' + error);
      res.sendStatus(503);
    } else {
      connection.query('select * from question where classID=?', [req.params.classID], function(err, result) {
        console.log(result);
        if (err == null) {
          res.json({
            success:true,
            count:result.length,
            questionInfos:result
          });
        } else {
          res.json({success:false});
        }
      });
      connection.release();
    }
  });
});

router.get('/api/questionDetail/:questionID', function(req, res) {
  console.log('API: get question detail');
  pool.getConnection(function(error, connection) {
    if (error) {
      console.log('db connection failed: ' + error);
      res.sendStatus(503);
    } else {
      connection.query('select * from question where id=?', [req.params.questionID], function(err, result) {
        if (err == null) {
          console.log(result);
          console.log(req.params.questionID);
          console.log(req.session.userID);
          connection.query('select * from heart where (questionID=? AND userID=?)', [req.params.questionID, req.session.userID], function(err2, result2) {
            if (err2 == null) {
              console.log(result2);
              res.json({
                success:true,
                info:result,
                didLike: (result2.length!=0)
              });
            } else {
              res.json({success:false, message:err2});
            }
          });
          connection.release();
        } else {
          res.json({success:false, message:err});
        }
      });
    }
  });
});

router.put('/api/addLike', function(req, res) {
  console.log('API: put addLike');
  console.log(req.body.questionID);
  console.log(req.body.userID);
  pool.getConnection(function(error, connection) {
    if(error){
      console.log('db connection failed: ' + error);
      res.sendStatus(503);
    } else {
      connection.query('insert into heart(questionID, userID) values(?,?)',[req.body.questionID, req.body.userID], function(error, result) {
        console.log(result);
        if(error !=null){
          console.log('insert query err: ', error);
          res.json({success:false});
        } else{
          res.json({success: true});
        }
      });
      connection.release();
    }
  });
});

router.delete('/api/removeLike', function(req, res){
  console.log('API : delete remove like');
  console.log(req.body.questionID);
  console.log(req.body.userID);
  pool.getConnection(function(error, connection) {
    if(error){
      console.log('db connection failed: ' + error);
      res.sendStatus(503);
    } else {
      connection.query('delete from heart where (questionID=? and userID=?)',[req.body.questionID, req.body.userID], function(error, result) {
        console.log(result);
        if(error !=null){
          console.log('insert query err: ', error);
          res.json({success:false});
        } else{
          res.json({success: true});
        }
      });
      connection.release();
    }
  });
});

router.post('/api/makeComment', function(req, res){
  console.log('API : post makeComment');
  console.log(req.body.questionID);
  console.log(req.body.userID);
  console.log(req.body.content);
  pool.getConnection(function(error, connection) {
    if(error){
      console.log('db connection failed: ' + error);
      res.sendStatus(503);
    } else {
      connection.query('insert into comment(questionID, userID, content) values(?,?,?)',[req.body.questionID, req.body.userID, req.body.content], function(error, result) {
        console.log(result);
        if(error !=null){
          console.log('insert query err: ', error);
          res.json({success:false});
        } else{
          res.json({success: true});
        }
      });
      connection.release();
    }
  });
});

router.get('/api/commentList/:questionID', function(req,res){
  console.log('API: get commentList');
  console.log(req.params.questionID);
  pool.getConnection(function(error, connection){
    if(error){
      console.log('db connection failed: '+error);
      res.sendStatus(503);
    } else {
      connection.query('select * from comment where questionID=?', [req.params.questionID], function(error, result){
        console.log(result);
        if(error == null){
          res.json({
            success : true,
            count : result.length,
            questioninfos : result
          });
        } else {
          res.json({success:false});
        }
      });
      connection.release();
    }
  });
});

module.exports = router;
