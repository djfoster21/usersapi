# A Simple Users API

Made with Java 8 and Springboot (2.1.5)

Database used: H2

Base URI: http://localhost:8080


### Endpoints implemented

**GET /users**

Returns the users in the DB

**POST /users**

Creates a new user in the DB

_Body_
```json
{
"username": "user",
"email": "mail@mail.com",
"name": "Juan",
"lastName": "Perez"
}
```

**GET /users/`<user-id>`**

Returns the user that matches the given id

**PUT /users/`<user-id>`**

Modifies the user that matches the given id

_Body_
```json
{
"username": "user",
"email": "mail@mail.com",
"name": "Juan",
"lastName": "Perez"
}
```

**DELETE /users/`<user-id>`**

Deletes the user that matches the given id


