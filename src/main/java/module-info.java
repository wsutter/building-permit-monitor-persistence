module ch.studior2.buildingpermitmonitor.persistence {
  requires spring.boot;
  requires spring.boot.autoconfigure;
  requires spring.context;
  requires spring.jdbc;
  requires spring.kafka;
  requires com.fasterxml.jackson.databind;
  requires java.sql;
  requires ch.studior2.buildingpermitmonitor.contracts;

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
