# Social network application

If you want to practice basics of highload system design and “get hands dirty” then this project is a good billet. You can build, run and modify it using instructions from this repo. 

The project covers nearly all basic blocks in highload architecture. I copied it from the [original repo](https://github.com/alexyakovlev90/otus-highload-social-network), which has a detailed description (in Russian) and made some minor fixes to build and run it. Below is a brief description of the architecture with some comments and instructions of how to build and run it. This description is very useful to understand architecture and dependencies to run service.

Application consists of 3 backend services (details are below): social-backend, social-chat, social-counter. You can run social-backend separately from others. Social-chat and social-counter need social-backend to work. Also I had some troubles in running all services with auto discovery in Consul. As I understand this could occur if you do not deploy MySQL replication fully (MySQL has a master and two slaves, all need to be started for correct work of social-backend service).

# Architecture

![architecture](./social-architecture.png)

# Application consists of 3 backend services

1. Social-backend - the main backend of the social network
  - replicated MySQL cluster is used as a service database 
    - the service uses semi–synchronous replication - for more
  - users’ data replication to Tarantool in–memory database is used to make search on users fast – [for more](./in-memory-tarantool/hw7-tarantool-replication.md)
  - the news feed uses PUB/SUB RabbitMQ, updates are stored to the Redis cache - [for more](./rabbit-redis/hw8-redis-rabbit.md) 
  - for the analytics purposes data is replicated in ClickHouse online analytical processing engine through the KittenHouse bufferizer (I didn't start         KittenHouse due to troubles with reaching its repo) – [for more](./clickhouse/hw9-clickhouse.md) 

2. Social-chat service
  - the data model is divided into chats and messages to make possible further development to group chats functionality
  - gRPC is used for service interconnections – [for more](./grpc-chats/grpc-chats-report.md) 
  - sharded cluster NoSQL database MongoDB of is used for data storage
  - a detailed [description](./sharding/sharding-report.md) of the work of the service with MongoDB
  - service provides horizontal scaling for the writing using sharding

3. Social-counter service
  - [description](./social-counter/README.md) of the service 
  - the service stores the number of unread messages
  - the service assumes a heavy load on reading
  - uses Redis for data storage
  - consistency between the counter and the actual number of unread messages according to the SAGA pattern

# Additional services

  - all services are wrapped in Docker and use Consul for services auto discovery – [for more](./consul-docker/hw13-consul.md) 
  - Prometheus is used for services and infrastructure’s monitoring – [for more](./monitoring/hw15-monitoring.md)
  - Nginx access to services for horizontal scaling – [for more](./load-balancing/hw11-load-balancing.md)
  - access to MySQL slave replicas through the HAProxy for horizontal scalability of reading load

# Build and deploy instruction

1. IDE
   - I run the project in Intelij IDEA community edition 2022.3.2.
3. Docker and docker compose installation
   - You need to install them if you don't have one yet. See instructions there. Note that docker compose is separate until and needs to be installed          after Docker installation. 
5. Java version
   - Project requires Java 12 but works fine with Java 11.
7. Database setup
   - To run the main service – social-baskend – you need to run MySQL cluster first. You need to enable the MySQL plugin in Idea and check connection with    the database. You should see 3 MySQL instances.  
5. Build the backend
   - Build the project using Maven. I use the Maven plugin in Idea, click build-verify-deploy-package. I build from the social-backend directory. After        this you will have a .jar file for social-backend. 
6. Build the frontend
   - Then you need to build social-frontend in the social-frontend directory. Note it will require npm (build manager for javascript, social-frontend          written in Angular) to build. I had trouble with the required npm version and installed nvm from the console in Idea and then obtained the required        version of npm. Then build the frontend from its directory using the “npm build” command in the console of the social-frontend directory in Idea. 
7. Run docker compose file in the social-baskend directory
   - All other instructions are in the docker compose file in the social-baskend directory. RabbitMQ will run when you run this docker-compose file. Other    services (Clickhouse and Tarantool) are optional for social-backend and entire application work.
8. Run docker compose file in the social-baskend directory to build and run altogether
   - Now you can reach the service on localhost. And experiment with additional two applications to run the entire service and set up auto discovery with      Consul. And also I recommend doing some load testing (scripts are in the testing folder in this repository, you can also check the original repo for        more details).
   
# TO DO plan

1.  Write tests for data generation and load testing of feed service of social-backend.
2.  Deploy a Hadoop stack (HDFS, MapReduce, Yarn) and other NoSQL databases (Hive, Cassandra) for user data replication and log analysis.
3.  Add Kubernetes (Minikube) for Docker infrastructure management. 
