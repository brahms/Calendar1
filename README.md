Calendar Project
================

Overview
--------
This project contains the source code for my calendar project for GMU Dist Systems.

Architecture
------------
The architecture for this client/server program is based on [Hazelcast](http://www.hazelcast.com/docs.jsp). Hazelcast is a distributed library that allows the client and server to communicate using simple java interfaces such as `Map` and `Queue`. The server persists all `Calendar` objects to disk using the database [H2](http://www.h2database.com/html/main.html). Furthermore hazelcast provides replicate and fault tolerance to the backend to continue providing service uptime even if one of the individual server nodes die.


---
### Server
Each server node uses two hazelcast instances. A frontend instance and a backend instance. Clients connect to only the frontend instance so they do not have access to any of the actual backend objects, such as where the Calendars are stored.

The server starts up two queues in the frontend. One is for the CalendarManagerService and the other is for the CalendarService. Clients can communicate by sending serializable pojos via these queues. The server starts up a thread to watch each queue.  The server can in turn respond using a serialize response pojo send to the client's unique answer queue.

Calendars are stored in a distributed `Map` that is stored on the backend instance of hazelcast. Each service queries using this `Map`, and the `Map` will continue to work even if one node fails.

Each server has its own H2 database that is used to keep calendars saved to disk in case of cluster failure. Thus the first server to startup in a cluster will readd all the calendar objects that are saved to disk from its local database and merge them into the cluster's `Map`. Furthermore whenever a new calendar is created and added to the Calendar Map, each server will update its local database file with the new calendar. Each calendar is stored in a row indexed by username, and serialized into a `blob`.

The server is also a shell program that uses [Cliche](https://code.google.com/p/cliche/) to provide the user with status of the server while it's running.

At the shell prompt type `?list` to see any available commands.

##### CalendarService

This service responds to requests dealing with the following request types:
1. RetrieveScheduleRequest (Queries the cluster for events for a given user in a time interval)
2. ScheduleEventRequest (Schedules a new event for one/more calendars in the cluster)

##### CalendarServiceManager

This service responds to requests dealing with the following request types:

1. ListRequest (Lists all calendars in the cluster)
2. CreateRequest (Creates a calendar object in the cluster and persists it)
3. RetrieveCalendarRequest (Retrieves an entire calendar object -- only usable by the owner)

---
### Client
The client is a shell program that uses [Cliche](https://code.google.com/p/cliche/) to create a shell based user-interface to manage a user's calendar.

To receive responses from the servers it creates a cluster-unique id and adds a `Queue` interface at that string. It then passes this along with any requests it makes to the servers in the backend so they know how where to send responses.

Furthermore the client has a thread running every second to check if the local calendar object (which is owned by whoever you logged in as) is currently dirty or not, and keeps it up to date. This thread checks the calendar for any appointments and outputs them `stdout` if so it happens. If another client in the cluster updates your calendar, your local calendar object will stay up to date. Just type `dump` in the command line to see the current calendar outputted to the `stdout`.

At the shell prompt type `?list` to see any available commands.

How To Run
----------
This project uses gradle to build the software. Simply run gradle (all you need installed is JDK 7) by using the command: `./gradlew installApp` from the source's root folder. You need an internet connection for this to work, but if you do it will automatically pull down its dependencies before compilation.

The output after building should look like this give or take some waiting for gradle to download files from the internet

    $ ./gradlew installApp
    :common:compileJava
    :common:compileGroovy
    :common:processResources UP-TO-DATE
    :common:classes
    :common:jar
    :client:compileJava UP-TO-DATE
    :client:compileGroovy
    :client:processResources
    :client:classes
    :client:jar
    :client:startScripts
    :client:installApp
    :server:compileJava
    :server:compileGroovy
    :server:processResources
    :server:classes
    :server:jar
    :server:startScripts
    :server:installApp

    BUILD SUCCESSFUL

    Total time: 46.66 secs

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

To run the on medusa cluster (switches the cluster off from using multicast to find separate members), add the argument "medusa" to the command line:

    -bash-4.1$ ./bin/server medusa
    Starting up
    Using TCP/IP configuration for medusa cluster
    Adding cluster member 1
            Frontend addr: compute-0-1.local:5701
            Backend addr: compute-0-1.local:5702
    Adding cluster member 2
            Frontend addr: compute-0-2.local:5701
            Backend addr: compute-0-2.local:5702
    Adding cluster member 3
            Frontend addr: compute-0-3.local:5701
            Backend addr: compute-0-3.local:5702
    Adding cluster member 4
            Frontend addr: compute-0-4.local:5701
            Backend addr: compute-0-4.local:5702
    Adding cluster member 5
            Frontend addr: compute-0-5.local:5701
            Backend addr: compute-0-5.local:5702
    Adding cluster member 6
            Frontend addr: compute-0-6.local:5701
            Backend addr: compute-0-6.local:5702
    Adding cluster member 7
            Frontend addr: compute-0-7.local:5701
            Backend addr: compute-0-7.local:5702
    Adding cluster member 8
            Frontend addr: compute-0-8.local:5701
            Backend addr: compute-0-8.local:5702
    Adding cluster member 9
            Frontend addr: compute-0-9.local:5701
            Backend addr: compute-0-9.local:5702
    Adding cluster member 10
            Frontend addr: compute-0-10.local:5701
            Backend addr: compute-0-10.local:5702
    Adding cluster member 11
            Frontend addr: compute-0-11.local:5701
            Backend addr: compute-0-11.local:5702
    Adding cluster member 12
            Frontend addr: compute-0-12.local:5701
            Backend addr: compute-0-12.local:5702
    Adding cluster member 13
            Frontend addr: compute-0-13.local:5701
            Backend addr: compute-0-13.local:5702
    Adding cluster member 14
            Frontend addr: compute-0-14.local:5701
            Backend addr: compute-0-14.local:5702
    Adding cluster member 15
            Frontend addr: compute-0-15.local:5701
            Backend addr: compute-0-15.local:5702
    Adding cluster member 16
            Frontend addr: compute-0-16.local:5701
            Backend addr: compute-0-16.local:5702
    Adding cluster member 17
            Frontend addr: compute-0-17.local:5701
            Backend addr: compute-0-17.local:5702
    Adding cluster member 18
            Frontend addr: compute-0-18.local:5701
            Backend addr: compute-0-18.local:5702
    Adding cluster member 19
            Frontend addr: compute-0-19.local:5701
            Backend addr: compute-0-19.local:5702
    Starting frontend instance
    Starting backend instance
    Oct 16, 2013 12:03:57 AM org.springframework.jdbc.datasource.DriverManagerDataSource setDriverC
    lassName
    INFO: Loaded JDBC driver: org.h2.Driver
    Started!

    CalenderServer>

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

Before you can do anything with the client program you need to login using the `l` or `login` command followed by your username. At which point the program will tell you to create a calendar which you can do using the command `cc` or `create-calendar` followed by your username again.

The client program can also be started to use the TCP/IP rather than multicast by adding the "medusa" argument to the command line like so:
`./bin/client medusa`

Source Code
-----------
All source code is written in either `Groovy` or `Java`. There are three  gradle sub-projects `:common`, `:server`, and `:client`. `:common` contains source code used by both the `:server` and `:client` projects, such as the serializable request/response pojos used in the queue system as well the domain objects `Calendar`, `Event`, etc.

Libraries Used
--------------

* Apache Commons for all around java usefulness
* Groovy for groovy language support
* slf4j and logback for logging
* Hazelcast for clustering and client/server communications
* Joda time for time parsing and output formatting
* JUnit for unit tests
* H2 a jdbc database built entirely with java
* Spring JDBC for building the database access layer ontop of jdbc
* DBUnit for unit testing the database access layer
* Cliche for program shells


