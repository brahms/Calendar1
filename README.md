Calendar Project
================

Overview
--------
This project contains the source code for my calendar project for GMU Dist Systems.

Architecture
------------
The architecture for this client/server program is based on [Hazelcast](http://www.hazelcast.com/docs.jsp). Hazelcast is a distributed library that allows the client and server to communicate using simple java interfaces such as `Map` and `Queue`. The server persists all `Calendar` objects to disk using the database [H2](http://www.h2database.com/html/main.html). Furthermore hazelcast allows multiple servers to store backend datastructures in distributed fault tolerant objects, allowing for servers and allowing to clients to function as if nothing happened.


### Server
The server starts up two hazelcast instances. A frontend instance and a backend instance. Clients connect to only the frontend instance so they cannot mess with any backend objects. The server starts up two queues in the frontend. One is for the CalendarManagerService and the other is for the CalendarService. Clients can communicate by sending serializable pojos via these queues. The server starts up a thread to watch each queue.  The server can in turn respond using a serialize response pojo send to the client's unique answer queue.

Calendars are stored in a distributed `Map` that is stored on the backend instance of hazelcast. Thus multiple servers use the same data structure for any Calendar queries.

Each server has its own H2 database that is used to keep calendars saved to disk in case of cluster failure. Thus the first server to startup in a cluster will readd all the calendar objects that are saved to disk. Furthermore whenever a new calendar is created and added to the Calendar Map, each server will update its local database file with the new calendar. Each calendar is stored in a row indexed by username, and serialized into a `blob`.

The server is also a shell program that uses [Cliche](https://code.google.com/p/cliche/) to provide the user with status of the server while it's running.

At the shell prompt type `?list` to see any available commands.

### Client
The client is a shell program that uses [Cliche](https://code.google.com/p/cliche/) to create a shell based user-interface to manage a user's calendar.

At the shell prompt type `?list` to see any available commands.

How To Run
----------
This project uses gradle to build the software. Simply run gradle (all you need installed is JDK 7) by using the command: `./gradlew installApp` from the source's root folder. You need an internet connection for this to work, but if you do it will automatically pull down its dependencies before compilation.

The server installation folder is then located in `./server/build/install/server/` and can be executed by calling `bin/server` or on windows `./bin/server.bat`

The same can likewise be said for the client, which is located at `/client/build/install/client/` and can be called using `./bin/client` or on windows `./bin/client.bat`.

### Server
Here's a example of starting up the server

    $ ./bin/server
    Starting up
    Oct 15, 2013 10:10:45 PM org.springframework.jdbc.datasource.DriverManagerDataSource setDriverC
    lassName
    INFO: Loaded JDBC driver: org.h2.Driver
    Started!

    CalenderServer> ?list
    abbrev  name    params
            exit    ()
    s       status  ()
    d       dump    (p1)
    d       dump    ()
    o       obliterate      ()

Tracing logs are located in the cwd in a file called `server.log`

### Client
Here's an example of starting up the client

    $ ./bin/client

    CalendarClient> ?list
    abbrev  name    params
            exit    ()
    l       login   (p1)
    lc      list-calendars  ()
    cs      calendar-services       ()
    wli     whos-logged-in  ()
    d       dump    ()
    cc      create-calendar (p1)
    CalendarClient>`

Tracing logs are located in the cwd in a file called `client.log`

Source Code
-----------
All source code is written in either `Groovy` or `Java`. There are three separatre projects `:common`, `:server`, and `:client`. `:common` contains source code used by both the server and client projects, such as the serializable request/response pojos used in the queue system as well the domain objects `Calendar`, `Event`, etc.


