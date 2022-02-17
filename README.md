# Marketcetera Automated Trading Platform

## Build From Source

### Tools

```
$ java -version
java version "1.8.0_301"

$ mvn -version
Apache Maven 3.8.2 (ea98e05a04480131370aa0c110b8c54cf726c06f)
```

### Environment

You must specify a database to use for tests and running locally. Each user should have their own database. The database configuration is specified in your Maven settings.xml file.

```
$ cat ~/.m2/settings.xml
<settings>
  <profiles>
    <profile>
      <id>default</id>
      <activation>
        <activeByDefault>true</activeByDefault>
      </activation>
      <properties>
        <!-- Postgres -->
        <metc.jdbc.user>metc</metc.jdbc.user>
        <metc.jdbc.password>pw4metc</metc.jdbc.password>
        <metc.jdbc.driver>org.postgresql.Driver</metc.jdbc.driver>
        <metc.jdbc.url>jdbc:postgresql://localhost/metc</metc.jdbc.url>
        <metc.flyway.vendor>psql</metc.flyway.vendor>
        <!-- HSQLDB -->
        <!--metc.jdbc.user>metc</metc.jdbc.user>
        <metc.jdbc.password>pw4metc</metc.jdbc.password>
        <metc.jdbc.driver>org.hsqldb.jdbc.JDBCDriver</metc.jdbc.driver>
        <metc.jdbc.url>jdbc:hsqldb:file:./target/data/metc-hsqldb-data</metc.jdbc.url>
        <metc.flyway.vendor>hsqldb</metc.flyway.vendor-->
      </properties>
    </profile>
  </profiles>
</settings>
```

These settings specify an existing Postgres database named `metc`. You may also use HSQLDB by uncommenting those properties and commenting out the Postgres properties. Other database vendors are also available.

### Build From Source

Clone the repo:
```
$ git clone https://github.com/Marketcetera/marketcetera.git

To just build:

```
$ cd marketcetera
$ mvn -DskipTests install
```

To build and test:

```
$ mvn install
```

### IDE

#### Eclipse

```
$ mvn eclipse:eclipse
```
File->Import Existing Projects

#### IntelliJ

#### VSCode

### Run

#### DARE (Deploy Anywhere Routing Engine)

```
$ cd packages/dare-package

$ mvn -Pexecute exec:java &

$ tail -f target/logs/dare-instance1.log
```

If it doesn't start properly, check target/logs/dareout1.log.

#### Web UI

```
$ cd ui/web-package

$ mvn spring-boot:run
```

Connect to `localhost:8080` and login as `admin/admin` to manage FIX sessions or `trader/trader` for order management.
