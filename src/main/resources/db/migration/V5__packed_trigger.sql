CREATE OR REPLACE FUNCTION packed_function()
RETURNS TRIGGER
AS
$$
DECLARE
    r RECORD;
	hist_order_id bigint;
BEGIN
    -- Log the order processing into orders_history
    INSERT INTO orders_history (customer_id, date_processed, worker_id, date_received)
    VALUES (NEW.customer_id, CURRENT_TIMESTAMP, NEW.worker_id, NEW.date_received)
	returning id into hist_order_id;

    -- Log and update the products_supply: log into the intermediate table and add to the inventory stock
    FOR r IN (SELECT * FROM products_orders WHERE order_id = NEW.id)
    LOOP
        -- Add to inventory stock
        UPDATE products SET stock = stock - r.amount WHERE id = r.product_id;

        -- Archive the row
        INSERT INTO products_orders_history (product_id, order_id, amount)
        VALUES (r.product_id, hist_order_id, r.amount);

        -- Delete the processed row from products_orders
        DELETE FROM products_orders WHERE id = r.id;
    END LOOP;

	-- Delete the used order_history row
    DELETE FROM orders WHERE id = NEW.id;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER packed_order
AFTER UPDATE OF status on orders
FOR EACH ROW
WHEN (NEW.status = 'processed')
EXECUTE FUNCTION packed_function();
