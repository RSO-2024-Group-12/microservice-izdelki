# Katalog izdelkov (microservice-izdelki)

Mikrostoritev omogoča upravljanje in pridobivanje podatkov o izdelkih. Mikrostoritev ne hrani podatkov o zalogi izdelkov.

## Namen

- pridobivanje seznama vseh izdelkov
- pridobivanje seznama izdelkov za prikaz kupcem
- pridobivanje podatkov posameznega izdelka
- dodajanje novih izdelkov
- posodabljanje podatkov o izdelkih
- brisanje oziroma mehki izbris izdelkov

## Tehnologije

- Java 21
- Quarkus
- PostgreSQL
- Hibernate ORM
- REST
- GraphQL
- OpenAPI
- Swagger

## Integracije

### Odvisnosti

Spodaj so navedene mikrostoritve, ki jih microservice-izdelki uporablja za svoje delovanje.

| Mikrostoritev          | Komunikacija | Namen                                |
|------------------------|--------------|--------------------------------------|
| microservice-skladisce | REST (GET)   | pridobitev podatkov o zalogi izdelka |
| microservice-skladisce | REST (POST)  | dodajanje novega izdelka v skladišče |

### Odjemalci

Spodaj so navedene mikrostoritve, ki uporabljajo microservice-izdelki.

| Mikrostoritev         | Komunikacija | Namen                         |
|-----------------------|--------------|-------------------------------|
| microservice-kosarica | GraphQL      | pridobitev podatkov o izdelku |


## API

### REST
- `GET /v1/izdelki` - pridobivanje seznama vseh izdelkov
- `GET /v1/izdelki/aktivni` - pridobivanje seznama za prikaz kupcem
- `GET /v1/izdelki/{id}` - pridobivanje posameznega izdelka
- `POST /v1/izdelki` - dodajanje novegega izdelka
- `PUT /v1/izdelki` - posodabljanje podatkov izdelka
- `DELETE /v1/izdelki/{id}` - mehki izbris izdelka

Podrobna dokumetacija je na voljo preko **OpenAPI (Swagger UI)**.

### GraphQL
- `POST /graphql` - poizvedba po izdelkih

GraphQL shema in poizvedbe so dostopne v **GraphQL Playground**.


## Razvoj in zagon

### Lokalni zagon v razvojnem načinu

Za zagon aplikacije s podporo za "vroče" ponovno nalaganje kode (live coding) uporabite:

```shell script
./mvnw quarkus:dev
```

Aplikacija bo privzeto dostopna na `http://localhost:8080`. Razvojni vmesnik (Dev UI) je na voljo na `http://localhost:8080/q/dev/`.

### Pakiranje aplikacije

Za pakiranje aplikacije v JAR datoteko:

```shell script
./mvnw package
```

Za izdelavo *über-jar* (vsebuje vse odvisnosti):

```shell script
./mvnw package -Dquarkus.package.jar.type=uber-jar
```

### Izgradnja Docker slike

Aplikacijo lahko zapakirate v Docker sliko z ukazom:

```shell script
docker build -t nakupify/microservice-izdelki .
```

## Konfiguracija

Konfiguracijski parametri se nahajajo v `src/main/resources/application.properties`. Glavne nastavitve vključujejo:

- `quarkus.datasource.jdbc.url`: Povezava do PostgreSQL baze.

## Avtomatski testi

Za zagon vseh testov uporabite:

```shell script
./mvnw test
```
