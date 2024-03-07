# Note Management API using Java Spring Boot + MongoDB

This API manages aspects of Note Management , where Notes can have Tags.

## Installation

Build the project.

```bash
mvn clean install
```

Run the JAR.

```bash
java -jar -Dspring.profiles.active=dev target/note-service-0.0.1-SNAPSHOT.jar
```

Build docker image.

```bash
docker build --build-arg JAR_FILE=target/note-service-0.0.1-SNAPSHOT.jar -t teletronics/notes .
```

Run docker image.

```bash
sudo docker run --network mongoCluster -p 8080:8080 teletronics/notes
```

This project uses Mongo DB. Please install mongodb as a replica set. Please run the following commands:

```bash
docker run -d --rm -p 27017:27017 --name mongo1 --network mongoCluster mongo:5 mongod --replSet myReplicaSet
docker run -d --rm -p 27018:27018 --name mongo2 --network mongoCluster mongo:5 mongod --replSet myReplicaSet
docker run -d --rm -p 27019:27019 --name mongo3 --network mongoCluster mongo:5 mongod --replSet myReplicaSet

docker exec -it mongo1 mongosh --eval "rs.initiate({
 _id: \"myReplicaSet\",
 members: [
   {_id: 0, host: \"mongo1\"},
   {_id: 1, host: \"mongo2\"},
   {_id: 2, host: \"mongo3\"}
 ]
})"

```

## REST endpoints

create tag

```bash
POST http://localhost:8080/api/tags
{
  "name": "BUSINESS"
 
}

POST http://localhost:8080/api/tags
{
  "name": "PERSONAL"
 
}

POST http://localhost:8080/api/tags
{
  "name": "IMPORTANT"
 
}

```

create note

```bash
POST http://localhost:8080/api/notes
{
  "title": "TelematicsTitle",
  "text": "hello alan i am here where are you and what are you doing hello are you there",
  "noteTagIds": [
    "65e70a041f46ff663e5e9a4a"
  ]
}

```

get note statistics

```bash

GET http://localhost:8080/api/notes/{id}/stats

```

update note

```bash
PUT http://localhost:8080/api/notes
{
  "title": "TelematicsTitleNew",
  "text": "hello alan i am here where are you and what are you doing hello are you there",
  "noteTagIds": [
    "65e70a041f46ff663e5e9a4a"
  ]
}

```

get notes by id

```bash

GET http://localhost:8080/api/notes/{id}


```

get list of notes paginated

```bash
GET http://localhost:8080/api/notes?page=0&limit=10

```

search notes by tag IDs paginated

```bash
POST http://localhost:8080/api/notes/search
{
  "page": 0,
  "limit": 10,
  "tags": [
    "65e70a041f46ff663e5e9a4a"
  ]
}


```

delete notes

```bash

DELETE http://localhost:8080/api/notes/{id}

```

