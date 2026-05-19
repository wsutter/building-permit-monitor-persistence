module ch.studior2.buildingpermitmonitor.persistence {
  exports ch.studior2.buildingpermitmonitor.persistence.api;
  exports ch.studior2.buildingpermitmonitor.persistence.config;

  requires ch.studior2.buildingpermitmonitor.contracts;
  requires java.sql;
  requires spring.boot;
  requires spring.boot.autoconfigure;
  requires spring.context;
  requires spring.jdbc;
  requires spring.kafka;
  requires spring.tx;
  requires spring.data.jpa;
  requires kafka.clients;
  requires tools.jackson.databind;
  requires jakarta.persistence;

  opens ch.studior2.buildingpermitmonitor.persistence to
      spring.core,
      spring.beans,
      spring.context;
  opens ch.studior2.buildingpermitmonitor.persistence.repository to
      spring.core,
      spring.beans,
      spring.context;
  opens ch.studior2.buildingpermitmonitor.persistence.service to
      spring.core,
      spring.beans,
      spring.context;
}
