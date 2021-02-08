# shopping_api
API that allows you to buy products from the available items and transfer founds to your account, save all operations and send you an email with it. 

## Run

* Download or clone repository and run it in IntelliJ IDEA
* Go to  ```meal/src/main/resources/application.properties```
and in ```spring.datasource.url``` connect with your MySql database,
in ```spring.datasource.username and spring.datasource.password```
enter your username and password to database. Next in ```spring.mail.username and spring.mail.password``` enter valid
gmail email and password if you want API be able to send emails

# Register

* To make an account use client like Postman, go to:
```
http://localhost:{your_default_port}/register
```
   and send a body in POST request like example below:
```
{
    "name": "Tom",
    "surname": "Jon",
    "login":"tom",
    "email":"tom@xyz.com",
    "accountBalance": 2000,
    "password": "password123"      
}

```
