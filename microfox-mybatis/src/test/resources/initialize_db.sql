create table if not exists client(
  id number primary key ,
  name varchar2(64),
  family varchar2(64)
);
create table if not exists address
(
    id          number  primary key ,
    street      varchar2(64),
    city        varchar2(64),
    state       varchar2(64),
    country     varchar2(64),
    postal_code  varchar2(64),
    phone_number varchar2(64),
    client_id   number,
    foreign key (client_id) references client(id)
);
create sequence client_seq;
create sequence address_seq;