#hyperskill.org project AccountService

Stage 1:
        a) create POST api/auth/signup endpoint that accepts data in the JSON format:
                {
                   "name": "<String value, not empty>",
                   "lastname": "<String value, not empty>",
                   "email": "<String value, not empty>",
                   "password": "<String value, not empty>"
                }
        b) returns response in the JSON format:
                {
                   "name": "<String value>",
                   "lastname": "<String value>",
                   "email": "<String value>"
                }

        UserRestController.java

Stage 2:
        a) Add the Spring security to your project and configure the HTTP basic authentication;
        b) For storing users and passwords, add a JDBC implementation of UserDetailsService with an H2 database;
        c) Change the POST api/auth/signup endpoint.
           It must be available to unauthorized users for registration and accepts data in the JSON format:
        {
           "name": "<String value, not empty>",
           "lastname": "<String value, not empty>",
           "email": "<String value, not empty>",
           "password": "<String value, not empty>"
        }
        d) Add the GET api/empl/payment/ endpoint that allows for testing the authentication.
           It should be available only to authenticated users and return a response in the JSON format
            representing the user who has sent the request.

Stage 3:
        a) Implement password check: passwords contain at least 12 characters;
        if a password fails this check, respond with 400 Bad Request and the following JSON body:
        {
            "timestamp": "<data>",
            "status": 400,
            "error": "Bad Request",
            "message": "The password length must be at least 12 chars!",
            "path": "<api>"
        }
        b) Passwords must be stored in a form that is resistant to offline attacks.
           Use BCryptPasswordEncoder with a strength of at least 13 to store the passwords in the database.
           Check the submitted passwords against the set of breached passwords.
           If the password is in the list of breached passwords, the service must respond with 400 Bad Request and the following JSON body:
        c) Implement the POST api/auth/changepass endpoint for changing passwords.

Stage 4:
        a) Add the POST api/acct/payments endpoint.
           It must be available to unauthorized users, accept data in JSON format, and store it in the database.
           The operation must be transactional! The JSON is as follows:
           [
               {
                   "employee": "<user email>",
                   "period": "<mm-YYYY>",
                   "salary": <Long>
               },....
        b) Add the PUT api/acct/payments endpoint.
           It must be available to unauthorized users, accept data in JSON format,
           and update the salary for the specified users in the database.
        c) Add the GET api/empl/payment endpoint.
           It should be available only to authenticated users.
           It takes the period parameter that specifies the period (the month and year) and provides
           the information for this period. If the parameter period is not specified,
           the endpoint provides information about the employee's salary for each period from the database
           as an array of objects in descending order by date.

Stage 5:
        a) Add the authorization to the service and implement the role model shown in the table.
           The first registered user should receive the Administrator role, the rest — Users.
           In case of authorization violation, respond with HTTP Forbidden status (403) and JSON
        b) Change the response for the POST api/auth/signup endpoint.
           It should respond with HTTP OK status (200) and the body with a JSON object
           with the information about a user.
           Add the roles field that contains an array with roles, sorted in ascending order.
        c) Add the GET api/admin/user endpoint.
           It must respond with an array of objects representing the users sorted by ID
           in ascending order. Return an empty JSON array if there's no information.
        d) Add the DELETE api/admin/user/{user email} endpoint, where {user email}
           specifies the user that should be deleted.
           The Administrator should not be able to delete himself.
           In that case, respond with the HTTP Bad Request status (400) and the following body:
        e) Add the PUT api/admin/user/role endpoint that changes user roles.
Stage 6:
        a) Implement logging security events in the application following the requirements described above.
        b) Add the GET api/security/events endpoint that must respond
           with an array of objects representing the security events of the service
           sorted in ascending order by ID. If no data is found, the service should return
           an empty JSON array.
        c) Implement a mechanism to block the user after 5 consecutive failed logins.
Stage 7:
        a) Our last step in the project is to ensure the security of our service.
           By now, all data, authentication, and business information is transmitted
           over an unsecured communication channel and can be easily intercepted.
           The solution to this problem is the HTTPS protocol.
