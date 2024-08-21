do $$
begin

  if not exists(select * from information_schema.columns where table_name = 'users') then
    create table users (
        user_id int4 not null,
        name varchar(255) not null,
        constraint user_id_pk primary key (user_id));
    comment on column users.user_id is 'id of user';
    comment on column users.name is 'name of user';
  end if;

  if not exists(select * from information_schema.columns where table_name = 'orders') then
    create table orders (
        order_id int4 not null,
        user_id int4 not null,
        order_date timestamp(6),
        constraint user_id_fk foreign key (user_id) references users(user_id),
        constraint order_id_pk primary key (order_id));
    comment on column orders.order_id is 'id of order';
    comment on column orders.order_date is 'data of order';
    create index orders_idx1 on orders using btree(user_id);
    create index orders_idx2 on orders using btree(order_date);
  end if;

  if not exists(select * from information_schema.columns where table_name = 'order_products') then
    create table order_products (
        order_id int4 not null,
        product_id int4 not null,
        product_value numeric(12,2) not null,
        constraint order_id_fk foreign key (order_id) references orders(order_id),
        constraint order_product_id_pk primary key (order_id, product_id));
    comment on column order_products.order_id is 'id of order';
  end if;

end;
$$
