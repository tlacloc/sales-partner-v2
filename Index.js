"use script";

const express = require('express');
var mysql = require('mysql');
const app = express(); //http mas complicada, puede nenviar cosas mas complicadas


var con = mysql.createConnection({
    host: "localhost",
    user: "Saenz1623",
    password: "Saenz1623",
    database: "inventorydb"
});

app.use(express.json());

//simular base de datos


app.get('/api/assemblies', (req , res) => { // diagonal, ponen un numero, pide un id
    //envia texto en formato http
    var sql = 'SELECT * FROM assemblies';
    con.query(sql, function (err, result) {
        if (err) throw err;
        res.send(result);
    });
});

app.get('/api/assembly_products', (req , res) => { // diagonal, ponen un numero, pide un id
    //envia texto en formato http
    var sql = 'SELECT * FROM assembly_products';
    con.query(sql, function (err, result) {
        if (err) throw err;
        res.send(result);
    });
});

app.get('/api/categories', (req , res) => { // diagonal, ponen un numero, pide un id
    //envia texto en formato http
    var sql = 'SELECT * FROM product_categories';
    con.query(sql, function (err, result) {
        if (err) throw err;
        res.send(result);
    });
});

app.get('/api/customers', (req , res) => { // diagonal, ponen un numero, pide un id
    //envia texto en formato http
    var sql = 'SELECT * FROM customers';
    con.query(sql, function (err, result) {
        if (err) throw err;
        res.send(result);
    });
});

app.get('/api/order_assemblies', (req , res) => { // diagonal, ponen un numero, pide un id
    //envia texto en formato http
    var sql = 'SELECT * FROM order_assemblies';
    con.query(sql, function (err, result) {
        if (err) throw err;
        res.send(result);
    });
});

app.get('/api/orders', (req , res) => { // diagonal, ponen un numero, pide un id
    //envia texto en formato http
    var sql = 'SELECT * FROM orders';
    con.query(sql, function (err, result) {
        if (err) throw err;
        res.send(result);
    });
});

app.get('/api/order_status', (req , res) => { // diagonal, ponen un numero, pide un id
    //envia texto en formato http
    var sql = 'SELECT * FROM order_status';
    con.query(sql, function (err, result) {
        if (err) throw err;
        res.send(result);
    });
});

app.get('/api/products', (req , res) => { // diagonal, ponen un numero, pide un id
    //envia texto en formato http
    var sql = 'SELECT * FROM products';
    con.query(sql, function (err, result) {
        if (err) throw err;
        res.send(result);
    });
});

app.get('/api/users/:user', (req , res) => {
    //envia texto en formato http


    var user = req.params.user;
    var sql = 'SELECT * FROM usuarios where user LIKE ?';
    con.query(sql, [user], function (err, result) {
        if (err) throw err;
        res.send(result);
    });

});

app.get('/api/clients', (req , res) => {
    //envia texto en formato http

    if (!!req.query.sort){
        if (req.query.sort === 'ASC'){
            clients.sort((a,b ) => {
                if (a.age > b.age ) return 1;
                else if (a.age === b.age) return 0;
                else return  -1;});
            res.send(clients);
        }else if (req.query.sort === 'DESC'){
            clients.sort((a,b ) => {
                if (a.age < b.age ) return 1;
                else if (a.age === b.age) return 0;
                else return  -1;});
            res.send(clients);
        }else {
            res.status(400).send('Invalid sorting options');
        }
    } else {
        con.query("SELECT * FROM customers", function (err, result, fields) {
            if (err) throw err;
            res.send(result);
        });
    }

});

app.post('/api/users/',(req,res) => {
    //validación
    const usuario = {user:req.body.user, password: req.body.password, name: req.body.name};
    res.send(usuario);
    var sql = "INSERT INTO usuarios (user, password, name) VALUES ?";
    var values = [[req.body.user,req.body.password, req.body.name]];
    con.query(sql,[values], function (err, result) {
        if (err) throw err;

    });
});


app.post('/api/clients/',(req,res) => {
    //validación

    let client = {id : clients.length, fname: req.body.fname,
        lname: req.body.lname,
        age: req.body.age};
    clients.push(client);
    res.send(client);
});

app.put('/api/clients/',(req,res) => {
    //validación

    const client = clients.find(c => c.id === parseInt(req.body.id));
    client.fname = req.body.fname;
    client.lname = req.body.lname;
    client.age = req.body.age;

    clients.push(client);
    res.send(client);
});

app.delete('/api/clients/:id',(req,res) => {
    //validación

    let index = clients.findIndex(c => c.id === parseInt(req.params.id));
    let clientsToDelete = clients.splice(index, 1);
    res.send(clientsToDelete);
});

app.listen(3000,() => {
    console.log('Listening on port 3000...');
});


