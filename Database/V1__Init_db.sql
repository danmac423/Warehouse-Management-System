-- This script was generated by the ERD tool in pgAdmin 4.
-- Please log an issue at https://github.com/pgadmin-org/pgadmin4/issues/new/choose if you find any bugs, including reproduction steps.
BEGIN;


CREATE TABLE IF NOT EXISTS public.products
(
    id bigserial NOT NULL,
    name character(100) NOT NULL,
    price numeric(2) NOT NULL DEFAULT 0,
    category_id bigserial NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.categories
(
    id bigserial NOT NULL,
    name character varying(100) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.product_amounts
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
    date_processed timestamp with time zone NOT NULL,
    worker_id bigserial NOT NULL,
    state character varying(10),
    date_recieved timestamp without time zone NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.address
(
    id bigserial NOT NULL,
    postal_code character varying(5) NOT NULL,
    city character varying(50) NOT NULL,
    street character varying(100) NOT NULL,
    house_nr integer NOT NULL,
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

CREATE TABLE IF NOT EXISTS public.inventory_stock
(
    id bigserial NOT NULL,
    product_id bigserial NOT NULL,
    amount integer NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    CONSTRAINT stock_product_one_to_one UNIQUE (product_id)
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
    arrival_date timestamp without time zone NOT NULL,
    processed_date timestamp without time zone,
    expected_date timestamp without time zone NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.supplier
(
    id bigserial NOT NULL,
    name character varying NOT NULL,
    address_id bigserial NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.product_supply
(
    id bigserial NOT NULL,
    supply_id bigserial NOT NULL,
    product_id bigserial NOT NULL,
    ammount integer NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.supplies_products
(
    id bigserial NOT NULL,
    supply_id bigserial NOT NULL,
    product_id bigserial NOT NULL,
    amount integer NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

ALTER TABLE IF EXISTS public.products
    ADD CONSTRAINT cat_id_fk FOREIGN KEY (category_id)
    REFERENCES public.categories (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION
    NOT VALID;


ALTER TABLE IF EXISTS public.products
    ADD CONSTRAINT stock_product FOREIGN KEY (id)
    REFERENCES public.inventory_stock (product_id) MATCH FULL
    ON UPDATE NO ACTION
    ON DELETE CASCADE
    NOT VALID;


ALTER TABLE IF EXISTS public.product_amounts
    ADD CONSTRAINT product_fk FOREIGN KEY (product_id)
    REFERENCES public.products (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION
    NOT VALID;


ALTER TABLE IF EXISTS public.product_amounts
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


ALTER TABLE IF EXISTS public.address
    ADD CONSTRAINT address_customer FOREIGN KEY (id)
    REFERENCES public.customers (address_id) MATCH SIMPLE
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
    REFERENCES public.supplier (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION
    NOT VALID;


ALTER TABLE IF EXISTS public.supplier
    ADD CONSTRAINT address_fk FOREIGN KEY (address_id)
    REFERENCES public.address (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION
    NOT VALID;


ALTER TABLE IF EXISTS public.product_supply
    ADD CONSTRAINT supplies_to_products FOREIGN KEY (supply_id)
    REFERENCES public.supplies (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION
    NOT VALID;


ALTER TABLE IF EXISTS public.product_supply
    ADD CONSTRAINT products_to_supplies FOREIGN KEY (product_id)
    REFERENCES public.products (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION
    NOT VALID;


ALTER TABLE IF EXISTS public.supplies_products
    ADD CONSTRAINT supply_fk FOREIGN KEY (supply_id)
    REFERENCES public.supplies (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION
    NOT VALID;


ALTER TABLE IF EXISTS public.supplies_products
    ADD CONSTRAINT product_fk FOREIGN KEY (product_id)
    REFERENCES public.products (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION
    NOT VALID;

END;