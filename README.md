
<div>
    <div align="center">
        <h1>Somnus</h1>
        <a href="https://somnus.ddns.net">
            <img src="https://drive.google.com/thumbnail?id=1qt9so1vEwYIKRboanCCEfbGqxpCz3n3p" style="filter: invert(1); height: 100pt;">
        </a>
        <h4 style="width: 80%; margin: 0 auto">This is the GitHub repository for the Somnus website. Head over to our website to see more of our content.</h4>
    </div>
</div>

<hr>

<p>
    <b> NOTE </b>: This project is no longer actively maintained.
</p>

## Dependencies
- Java 11 (soon to be 17)
- Angular 12
- Node 16
- npm 8.9

<hr>

## Backend
To deploy the backend just run the script `exp_backend_prod.sh`.
If you open the script, you will notice that it uses the production environment, requiring you to create the `application-production.properties` file with the corresponding production properties. 
This will test the project and create an executable jar file, which you can use to deploy the server.
You will also need to create a application-dev.properties to run Somnus. Here is a sample of what you need:
```
## Datasource
spring.datasource.url=<db_link>
spring.datasource.username=<db_user>
spring.datasource.password=<password>

## Firebase Config
firebase.database.url=<your_firebase_url>
firebase.service.account.file.path=<path_to_key>

## Minecraft API link
minecraft.api.link=https://api.mcsrvstat.us/2/somnus.ddns.net

# Github Info
github.auth.token=<git_token>
github.api.url=https://api.github.com/repos/MiguelMarcelino/Somnus

## Mail
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=<your_test_email>
spring.mail.password=<email_password>
spring.mail.properties.mail.transport.protocol=smtp
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

#Pusher
pusher.config.appid=<app_id>
pusher.config.key=<app_key>
pusher.config.secret=<app_secret>
pusher.config.cluster=<app_cluster>

#Jpa
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.properties.javax.persistence.schema-generation.create-database-schemas=true

# Restart server
spring.devtools.restart.enabled=true
```

To create this file, you will need the following:
- Create a local database, supplying it your username and password
- Create your own firebase app using the firebase console
- Configure Pusher with the required data (although pusher is still in testing, so no need to create it just yet)
- Configure an email for testing
- Get a github token to test the github functionalities. 


## Frontend
To test the frontend, just jun `npm start` to start the server locally. This will open the server on port 4200.
To deploy the frontend you can run the 
`exp_frontend_prod` script to build the angular project for production. This will
create a `/SomnusFrontEnd/dist` folder containing all the necessary files for deployment. 

<hr>

## Testing environment

### Using Database (MySQL)
- Create a new user on the mysql database: CREATE USER 'testuser'@'localhost' IDENTIFIED BY 'yourpassword';
- Give permissions to new user: GRANT ALL PRIVILEGES ON *.* TO 'testuser'@'localhost' WITH GRANT OPTION;
- Flush mysql privileges: FLUSH PRIVILEGES;
- Create Database: CREATE DB somnustestdb;
- Dont forget to change the password in the <i>application-dev.properties </i> file

### Running Backend
- Run using the created bash script file: run_backend_testing.sh


<div align="center">
    <hr>
    <h4 style="width: 80%; margin: 0 auto">The Somnus Team</h4>
    <a href="https://somnus.ddns.net/team" target="_blank">
        <img src="https://img.icons8.com/material-sharp/96/ffffff/user-group-man-woman.png" style="height: 50pt"/>
    </a> 
</div>

