
const mysql = require('mysql');

const dbConfig = {
    host: 'localhost',
    port: 3306,
    user: 'root',
    password: 'dbserver1',
    database: 'thanqu',
    waitForConnection:true,
    connectionLimit:23
};
const dbPool=mysql.createPool(dbConfig);

module.exports=dbPool;
