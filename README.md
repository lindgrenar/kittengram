# kittengram
The instagram for kitten lovers

## Start
The following command will start the server at port 4000. 
Note that the command must be called from within the project folder.
```
java -jar server-1.0.jar
```

This will start a webserver at port 4000, and server html from the 'www/' folder.

## options
To change the default port of the server you can pass another port as an argument.
```
java -jar server-1.0.jar port=5000
```

The default database is SQLite. If you're using MySQL as a database you must pass that as an argument, and the MySQL user and password if you want to set the database user.
```
java -jar server-1.0.jar port=5000 mysql=true user=jeff password=secret
```

Default username and password are 'user' and 'password'

## Build jar
To build a jar from scratch you type this command from project directory:
```
mvn clean package
```

This will create a new jar in the project directory, and is extremely useful to know how to do if working with [CD], Continuous Deployment.