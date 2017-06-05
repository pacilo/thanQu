var express = require('express');
var path = require('path');
var favicon = require('serve-favicon');
var logger = require('morgan');
var cookieParser = require('cookie-parser');
var bodyParser = require('body-parser');

var session = require('express-session');//추가
var FileStore = require('session-file-store')(session);//추가


var index = require('./routes/index');
// var classDetail = require('./routes/classDetail');

var app = express();
var server = app.listen(8080);

// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'ejs');

// uncomment after placing your favicon in /public
//app.use(favicon(path.join(__dirname, 'public', 'favicon.ico')));
app.use(logger('dev'));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));

app.use(bodyParser.urlencoded({extended: false}));//미들웨어 등록부분

//resave 세션아이디를 접속할때마다 발급하지 않는다

app.use(session({
  cookie:{},
  secret: '12312dajfj23rj2po4$#%@#',
  resave: false,
  saveUninitialized: true,
  store:new FileStore()//sessions라는 디렉토리가 생김
}));

app.use('/', index);
app.use('/classConfirm', index);
app.use('/questionList', index);
app.use('/end', index);
app.use('/classDetail', index);

// catch 404 and forward to error handler
app.use(function(req, res, next) {
  var err = new Error('Not Found');
  err.status = 404;
  next(err);
});

// error handler
app.use(function(err, req, res, next) {
  // set locals, only providing error in development
  res.locals.message = err.message;
  res.locals.error = req.app.get('env') === 'development' ? err : {};

  // render the error page
  res.status(err.status || 500);
  res.render('error');
});

module.exports = app;
