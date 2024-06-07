-- This script was generated by the ERD tool in pgAdmin 4.
-- Please log an issue at https://github.com/pgadmin-org/pgadmin4/issues/new/choose if you find any bugs, including reproduction steps.

CREATE TABLE IF NOT EXISTS public.products
(
    id bigserial NOT NULL,
    name character(100) NOT NULL,
    price numeric(7, 2) NOT NULL DEFAULT 0,
    category_id bigserial NOT NULL,
    stock integer NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.categories
(
    id bigserial NOT NULL,
    name character varying(100) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.products_orders
(
    id bigserial NOT NULL,
    product_id bigserial NOT NULL,
    order_id bigserial NOT NULL,
    amount integer NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.orders
(
    id bigserial NOT NULL,
    customer_id bigserial NOT NULL,
    date_processed timestamp without time zone,
    worker_id bigserial,
    state character varying(10),
    date_received timestamp without time zone NOT NULL,
    PRIMARY KEY (id)
);


CREATE TABLE IF NOT EXISTS public.addresses
(
    id bigserial NOT NULL,
    street character varying(100) NOT NULL,
    house_nr integer NOT NULL,
    postal_code character varying(5) NOT NULL,
    city character varying(50) NOT NULL,
    country VARCHAR(50) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.customers
(
    id bigserial NOT NULL,
    name character varying(50) NOT NULL,
    last_name character varying(50) NOT NULL,
    address_id bigserial NOT NULL,
    email character varying(100),
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.workers
(
    id bigserial NOT NULL,
    login character varying(20) NOT NULL,
    name character varying(100) NOT NULL,
    last_name character varying(100) NOT NULL,
    "position" character varying(100) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT "unique login" UNIQUE (login)
);

CREATE TABLE IF NOT EXISTS public.supplies
(
    id bigserial NOT NULL,
    supplier_id bigserial NOT NULL,
    worker_id bigserial,
    state character varying NOT NULL,
    arrival_date timestamp without time zone,
    processed_date timestamp without time zone,
    expected_date timestamp without time zone NOT NULL,
    product_id bigserial NOT NULL,
    amount integer NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.suppliers
(
    id bigserial NOT NULL,
    name character varying NOT NULL,
    address_id bigserial NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.supplies_history
(
    id bigserial NOT NULL,
    supplier_id bigserial NOT NULL,
    worker_id bigserial NOT NULL,
    arrival_date timestamp without time zone NOT NULL,
    processed_date timestamp without time zone NOT NULL,
    expected_date timestamp without time zone NOT NULL,
    product_id bigserial NOT NULL,
    amount integer NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.orders_history
(
    id bigserial NOT NULL,
    customer_id bigserial NOT NULL,
    date_processed timestamp without time zone NOT NULL,
    worker_id bigserial NOT NULL,
    date_received timestamp without time zone NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.products_orders_history
(
    id bigserial NOT NULL,
    product_id bigserial NOT NULL,
    order_id bigserial NOT NULL,
    amount integer NOT NULL,
    PRIMARY KEY (id)
);

ALTER TABLE IF EXISTS public.products
    ADD CONSTRAINT cat_id_fk FOREIGN KEY (category_id)
    REFERENCES public.categories (id) MATCH SIMPLE
    ON UPDATE NO ACTION
       ON DELETE NO ACTION
              NOT VALID;


ALTER TABLE IF EXISTS public.products_orders
    ADD CONSTRAINT product_fk FOREIGN KEY (product_id)
    REFERENCES public.products (id) MATCH SIMPLE
    ON UPDATE NO ACTION
       ON DELETE NO ACTION
              NOT VALID;


ALTER TABLE IF EXISTS public.products_orders
    ADD CONSTRAINT product_to_order FOREIGN KEY (order_id)
    REFERENCES public.orders (id) MATCH SIMPLE
    ON UPDATE NO ACTION
       ON DELETE NO ACTION
              NOT VALID;


ALTER TABLE IF EXISTS public.orders
    ADD CONSTRAINT order_customer FOREIGN KEY (customer_id)
    REFERENCES public.customers (id) MATCH SIMPLE
    ON UPDATE NO ACTION
       ON DELETE NO ACTION
              NOT VALID;


ALTER TABLE IF EXISTS public.orders
    ADD CONSTRAINT order_worker FOREIGN KEY (worker_id)
    REFERENCES public.workers (id) MATCH SIMPLE
    ON UPDATE NO ACTION
       ON DELETE NO ACTION
              NOT VALID;


ALTER TABLE IF EXISTS public.customers
    ADD CONSTRAINT address_customer FOREIGN KEY (address_id)
    REFERENCES public.addresses (id) MATCH SIMPLE
    ON UPDATE NO ACTION
       ON DELETE NO ACTION
              NOT VALID;


ALTER TABLE IF EXISTS public.supplies
    ADD CONSTRAINT supplies_workers FOREIGN KEY (worker_id)
    REFERENCES public.workers (id) MATCH SIMPLE
    ON UPDATE NO ACTION
       ON DELETE NO ACTION
              NOT VALID;


ALTER TABLE IF EXISTS public.supplies
    ADD CONSTRAINT supplier_supply FOREIGN KEY (supplier_id)
    REFERENCES public.suppliers (id) MATCH SIMPLE
    ON UPDATE NO ACTION
       ON DELETE NO ACTION
              NOT VALID;


ALTER TABLE IF EXISTS public.supplies
    ADD CONSTRAINT product_supply FOREIGN KEY (product_id)
    REFERENCES public.products (id) MATCH SIMPLE
    ON UPDATE NO ACTION
       ON DELETE NO ACTION
              NOT VALID;


ALTER TABLE IF EXISTS public.suppliers
    ADD CONSTRAINT address_fk FOREIGN KEY (address_id)
    REFERENCES public.addresses (id) MATCH SIMPLE
    ON UPDATE NO ACTION
       ON DELETE NO ACTION
              NOT VALID;


ALTER TABLE IF EXISTS public.supplies_history
    ADD CONSTRAINT hist_supply_worker FOREIGN KEY (worker_id)
    REFERENCES public.workers (id) MATCH SIMPLE
    ON UPDATE NO ACTION
       ON DELETE NO ACTION
              NOT VALID;


ALTER TABLE IF EXISTS public.supplies_history
    ADD CONSTRAINT hist_supply_supplier FOREIGN KEY (supplier_id)
    REFERENCES public.suppliers (id) MATCH SIMPLE
    ON UPDATE NO ACTION
       ON DELETE NO ACTION
              NOT VALID;


ALTER TABLE IF EXISTS public.supplies_history
    ADD CONSTRAINT hist_supply_product FOREIGN KEY (product_id)
    REFERENCES public.products (id) MATCH SIMPLE
    ON UPDATE NO ACTION
       ON DELETE NO ACTION
              NOT VALID;


ALTER TABLE IF EXISTS public.orders_history
    ADD CONSTRAINT hist_order_customer FOREIGN KEY (customer_id)
    REFERENCES public.customers (id) MATCH SIMPLE
    ON UPDATE NO ACTION
       ON DELETE NO ACTION
              NOT VALID;


ALTER TABLE IF EXISTS public.orders_history
    ADD CONSTRAINT hist_worker_id FOREIGN KEY (worker_id)
    REFERENCES public.workers (id) MATCH SIMPLE
    ON UPDATE NO ACTION
       ON DELETE NO ACTION
              NOT VALID;


ALTER TABLE IF EXISTS public.products_orders_history
    ADD CONSTRAINT hist_product_fk FOREIGN KEY (product_id)
    REFERENCES public.products (id) MATCH SIMPLE
    ON UPDATE NO ACTION
       ON DELETE NO ACTION
              NOT VALID;


ALTER TABLE IF EXISTS public.products_orders_history
    ADD CONSTRAINT hist_order_fk FOREIGN KEY (order_id)
    REFERENCES public.orders (id) MATCH SIMPLE
    ON UPDATE NO ACTION
       ON DELETE NO ACTION
              NOT VALID;
