## Getting Started

These instructions will get you a copy of the project up and running on your local machine.

### Prerequisites

* JDK11

### Installing

```
./mvnw clean package
```

### Running the tests

```
./mvnw clean test
```

### Running

Simple startup
```
./mvnw clean spring-boot:run
```

Example curls:

```
crate a new basket: 
curl --location --request POST 'http://localhost:8080/baskets'

add item to a basket: 
curl --location --request PATCH 'http://localhost:8080/baskets/item' \
--header 'Content-Type: application/json' \
--data-raw '{
    "basketId": "636cee1544a4d73b04349d8b",
    "productId": "123",
    "quantity": 1
}'

get items in a basket: 
curl --location --request GET 'http://localhost:8080/baskets/636cee1544a4d73b04349d8b'

delete a basket: 
curl --location --request DELETE 'http://localhost:8080/baskets/636cee1544a4d73b04349d8b'

checkout a basket: 
curl --location --request POST 'http://localhost:8080/baskets/checkout' \
--header 'Content-Type: application/json' \
--data-raw '{
    "basketId": "636cee1544a4d73b04349d8b",
    "vatNumber": "LU26375245"
}'
```

## Built With

* [Spring Boot](https://spring.io/projects/spring-boot/)
* [Maven](https://maven.apache.org/)

## Authors

* **Chantapat Tancharoen** - [GitHub](https://github.com/c7tt8nt2p)