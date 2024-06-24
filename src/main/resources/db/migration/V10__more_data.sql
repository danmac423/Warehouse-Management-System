DO $$
DECLARE
    addr_id1 bigint;
    addr_id2 bigint;
    addr_id3 bigint;
    addr_id4 bigint;
    addr_id5 bigint;
    addr_id6 bigint;
    addr_id7 bigint;
    addr_id8 bigint;
    addr_id9 bigint;


    work_id1 bigint;
    work_id2 bigint;
    work_id3 bigint;

    supplier_id1 bigint;
    supplier_id2 bigint;
    supplier_id3 bigint;
    supplier_id4 bigint;
    supplier_id5 bigint;
    supplier_id6 bigint;

    cat1_id bigint;
    cat2_id bigint;
    cat3_id bigint;

    prod1_id bigint;
    prod2_id bigint;
    prod3_id bigint;
    prod4_id bigint;
    prod5_id bigint;
    prod6_id bigint;

    cust1_id bigint;
    cust2_id bigint;
    cust3_id bigint;

    order1_id bigint;
    order2_id bigint;
    order3_id bigint;
    order4_id bigint;
	order5_id bigint;

	supply1_id bigint;
    supply2_id bigint;
    supply3_id bigint;
    supply4_id bigint;
    supply5_id bigint;
    supply6_id bigint;
    supply7_id bigint;

    yesterday_timestamp TIMESTAMP WITHOUT TIME ZONE;

BEGIN
    ALTER TABLE supplies ALTER COLUMN worker_id DROP NOT NULL;
    ALTER TABLE orders ALTER COLUMN worker_id DROP NOT NULL;

    yesterday_timestamp := (CURRENT_TIMESTAMP - INTERVAL '1 day')::timestamp;

    -- 6 addresses
    -- Insert Address 1
    INSERT INTO addresses (postal_code, city, street, house_nr, country)
    VALUES ('10001', 'New York', '5th Avenue', '1', 'USA')
    RETURNING id INTO addr_id1;
    
    -- Insert Address 2
    INSERT INTO addresses (postal_code, city, street, house_nr, country)
    VALUES ('WC2N 5DU', 'London', 'Trafalgar Square', '2', 'UK')
    RETURNING id INTO addr_id2;

    -- Insert Address 3
    INSERT INTO addresses (postal_code, city, street, house_nr, country)
    VALUES ('75008', 'Paris', 'Avenue des Champs-Élysées', '3', 'France')
    RETURNING id INTO addr_id3;

    -- Insert Address 4
    INSERT INTO addresses (postal_code, city, street, house_nr, country)
    VALUES ('10117', 'Berlin', 'Unter den Linden', '4', 'Germany')
    RETURNING id INTO addr_id4;

    -- Insert Address 5
    INSERT INTO addresses (postal_code, city, street, house_nr, country)
    VALUES ('00187', 'Rome', 'Via Veneto', '5', 'Italy')
    RETURNING id INTO addr_id5;

    -- Insert Address 6
    INSERT INTO addresses (postal_code, city, street, house_nr, country)
    VALUES ('104-0061', 'Tokyo', 'Ginza', '6', 'Japan')
    RETURNING id INTO addr_id6;

	INSERT INTO addresses (postal_code, city, street, house_nr, country)
	VALUES ('EC2N 1HQ', 'London', 'Bishopsgate', '125', 'UK')
	RETURNING id INTO addr_id7;

	INSERT INTO addresses (postal_code, city, street, house_nr, country)
	VALUES ('92101', 'San Diego', 'Broadway', '123', 'USA')
	RETURNING id INTO addr_id8;

	INSERT INTO addresses (postal_code, city, street, house_nr, country)
	VALUES ('75011', 'Paris', 'Rue de la Roquette', '56', 'France')
	RETURNING id INTO addr_id9;

    -- 3 workers
    INSERT INTO workers (username, password, name, last_name, role)
    VALUES ('bthomas', '$2a$10$KbQiYKnHa20Uq6zErTzpI.O70Z/M4XAGwuztYyX/ezOkFFX5/82iG', 'Brian', 'Thomas', 'WORKER')
    RETURNING id INTO work_id1;

    -- Insert Worker 2
    INSERT INTO workers (username, password, name, last_name, role)
    VALUES ('klane', '$2a$10$0Z3QQKfBe9b/LshxJpEuyOwlZcb6gHhMCXqIcxt06EX/BzXyF5uae', 'Kate', 'Lane', 'WORKER')
    RETURNING id INTO work_id2;

    -- Insert Worker 3
    INSERT INTO workers (username, password, name, last_name, role)
    VALUES ('sjohnson', '$2a$10$ZCkAYJzRy2EZsA41q9obmeH4de3Uz/zl4YTVjcjxVs6jyo/Tmcm6C', 'Sam', 'Johnson', 'WORKER')
    RETURNING id INTO work_id3;

    -- Suppliers
    -- Insert Supplier 1
    INSERT INTO suppliers (name, address_id)
    VALUES ('Apple Inc.', addr_id1)
    RETURNING id INTO supplier_id1;

    -- Insert Supplier 2
    INSERT INTO suppliers (name, address_id)
    VALUES ('Samsung Electronics', addr_id2)
    RETURNING id INTO supplier_id2;

    -- Insert Supplier 3
    INSERT INTO suppliers (name, address_id)
    VALUES ('Google LLC', addr_id3)
    RETURNING id INTO supplier_id3;

    -- Insert Supplier 4
    INSERT INTO suppliers (name, address_id)
    VALUES ('Sony Corporation', addr_id4)
    RETURNING id INTO supplier_id4;

    -- Insert Supplier 5
    INSERT INTO suppliers (name, address_id)
    VALUES ('LG Electronics', addr_id5)
    RETURNING id INTO supplier_id5;

    -- Insert Supplier 6
    INSERT INTO suppliers (name, address_id)
    VALUES ('Dell Technologies', addr_id6)
    RETURNING id INTO supplier_id6;

    -- Insert Categories
    -- Category 1: Smartwatches
    INSERT INTO categories (name)
    VALUES ('Smartwatches')
    RETURNING id INTO cat1_id;

    -- Category 2: Desktop Computers
    INSERT INTO categories (name)
    VALUES ('Desktop Computers')
    RETURNING id INTO cat2_id;

    -- Category 3: Tablets
    INSERT INTO categories (name)
    VALUES ('Tablets')
    RETURNING id INTO cat3_id;

    -- Insert Products
    -- Product 1: Samsung Galaxy Watch 4
    INSERT INTO products (name, price, category_id, stock)
    VALUES ('Samsung Galaxy Watch 4', 299.99, cat1_id, 50)
    RETURNING id INTO prod1_id;

    -- Product 2: LG Desktop Computer
    INSERT INTO products (name, price, category_id, stock)
    VALUES ('LG Desktop Computer', 1299.99, cat2_id, 30)
    RETURNING id INTO prod2_id;

    -- Product 3: Google Pixel Tablet
    INSERT INTO products (name, price, category_id, stock)
    VALUES ('Google Pixel Tablet', 899.99, cat3_id, 20)
    RETURNING id INTO prod3_id;

    INSERT INTO products (name, price, category_id, stock)
    VALUES ('Sony Tablet Xperia Z', 1999.99, cat3_id, 40)
    RETURNING id INTO prod4_id;

    -- Product 5: Apple Watch Series 7
    INSERT INTO products (name, price, category_id, stock)
    VALUES ('Apple Watch Series 7', 399.99, cat1_id, 60)
    RETURNING id INTO prod5_id;

    INSERT INTO products (name, price, category_id, stock)
    VALUES ('Dell Latitude 7320 Detachable', 999.99, cat3_id, 80)
    RETURNING id INTO prod6_id;

    -- Customers
    -- Insert Customer 1
    INSERT INTO customers (name, last_name, address_id, email)
    VALUES ('Johnas', 'Brother', addr_id7, 'john.bro@example.com')
    RETURNING id INTO cust1_id;

    -- Insert Customer 2
    INSERT INTO customers (name, last_name, address_id, email)
    VALUES ('Ali', 'Boo', addr_id8, 'ali.boo@pw.edu.com')
    RETURNING id INTO cust2_id;

    -- Insert Customer 3
    INSERT INTO customers (name, last_name, address_id, email)
    VALUES ('Michael', 'Jordan', addr_id9, 'michael.jordan@gmail.com')
    RETURNING id INTO cust3_id;
    -- Orders
    -- Insert Order 1
    INSERT INTO orders (customer_id, date_processed, worker_id, status, date_received)
    VALUES (cust1_id, null, work_id1, 'received', CURRENT_TIMESTAMP)
    RETURNING id INTO order1_id;

    -- Insert Order 2
    INSERT INTO orders (customer_id, date_processed, worker_id, status, date_received)
    VALUES (cust3_id, null, work_id2, 'received', CURRENT_TIMESTAMP - INTERVAL '1 day')
    RETURNING id INTO order2_id;

    -- Insert Order 3
    INSERT INTO orders (customer_id, date_processed, worker_id, status, date_received)
    VALUES (cust2_id, null, work_id3, 'received', CURRENT_TIMESTAMP - INTERVAL '2 days')
    RETURNING id INTO order3_id;

	INSERT INTO orders (customer_id, date_processed, worker_id, status, date_received)
    VALUES (cust2_id, null, null, 'received', CURRENT_TIMESTAMP - INTERVAL '2 days')
	RETURNING id INTO order4_id;

	INSERT INTO orders (customer_id, date_processed, worker_id, status, date_received)
    VALUES (cust1_id, null, null, 'received', CURRENT_TIMESTAMP - INTERVAL '2 days')
	RETURNING id INTO order5_id;

    -- Products Orders
    INSERT INTO products_orders(product_id, order_id, amount)
    VALUES 
        (prod1_id, order1_id, 2),
        (prod2_id, order1_id, 1),
        (prod3_id, order1_id, 3),
        (prod4_id, order2_id, 1),
        (prod5_id, order2_id, 2),
        (prod6_id, order2_id, 1),
        (prod1_id, order3_id, 1),
        (prod3_id, order3_id, 2),
        (prod1_id, order4_id, 3),
		(prod3_id, order4_id, 5),
        (prod5_id, order4_id, 4),
        (prod6_id, order4_id, 2),
		(prod6_id, order4_id, 10),
		(prod1_id, order5_id, 4),
        (prod4_id, order5_id, 2),
		(prod2_id, order5_id, 1);

    UPDATE orders
    SET status = 'processed'
    WHERE id IN (order2_id, order3_id);

    -- Insert Supply 1
    INSERT INTO supplies (supplier_id, worker_id, status, arrival_date, processed_date, expected_date, product_id, amount)
    VALUES (supplier_id2, work_id2, 'arrived', CURRENT_TIMESTAMP, null, CURRENT_TIMESTAMP, prod1_id, 5)
    RETURNING id INTO supply1_id;

    -- Insert Supply 2
    INSERT INTO supplies (supplier_id, worker_id, status, arrival_date, processed_date, expected_date, product_id, amount)
    VALUES (supplier_id5, null, 'underway', null, null, CURRENT_TIMESTAMP, prod2_id, 3)
    RETURNING id INTO supply2_id;

    -- Insert Supply 3
    INSERT INTO supplies (supplier_id, worker_id, status, arrival_date, processed_date, expected_date, product_id, amount)
    VALUES (supplier_id3, work_id2, 'arrived', CURRENT_TIMESTAMP, null, CURRENT_TIMESTAMP, prod3_id, 2)
    RETURNING id INTO supply3_id;

    -- Insert Supply 4
    INSERT INTO supplies (supplier_id, worker_id, status, arrival_date, processed_date, expected_date, product_id, amount)
    VALUES (supplier_id4, null, 'underway', null, null, CURRENT_TIMESTAMP, prod4_id, 4)
    RETURNING id INTO supply4_id;

    -- Insert Supply 5
    INSERT INTO supplies (supplier_id, worker_id, status, arrival_date, processed_date, expected_date, product_id, amount)
    VALUES (supplier_id1, work_id2, 'arrived', CURRENT_TIMESTAMP, null, CURRENT_TIMESTAMP, prod5_id, 3)
    RETURNING id INTO supply5_id;

    -- Insert Supply 6
    INSERT INTO supplies (supplier_id, worker_id, status, arrival_date, processed_date, expected_date, product_id, amount)
    VALUES (supplier_id6, null, 'arrived', CURRENT_TIMESTAMP, null, CURRENT_TIMESTAMP, prod6_id, 2)
    RETURNING id INTO supply6_id;

    UPDATE supplies
    SET status = 'processed'
    WHERE id IN (supply3_id, supply5_id, supply1_id);

END $$;
