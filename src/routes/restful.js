var express = require('express');
var router = express.Router();
const fs = require('fs');
const ejs = require('ejs');
const pool = require('../config/db_pool');
var aws = require('aws-sdk');
aws.config.loadFromPath('./config/aws_config.json');

router.post('/join', function(req, res, next){

})
