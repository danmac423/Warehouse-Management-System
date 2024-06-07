CREATE OR REPLACE FUNCTION unpacked_function()
RETURNS TRIGGER
AS
$$
BEGIN
    -- Log the suppy processing into supply_history
    INSERT INTO supplies_history (supplier_id, worker_id, arrival_date, processed_date, expected_date, product_id, amount)
    VALUES (NEW.supplier_id, NEW.worker_id, NEW.arrival_date, NEW.processed_date, NEW.expected_date, NEW.product_id, NEW.amount);
	-- Update the stock
    UPDATE products SET stock = stock + NEW.amount WHERE id = NEW.product_id;
	-- Delete the used order_history row
    DELETE FROM supplies WHERE id = NEW.id;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER unpacked_supply
AFTER UPDATE OF state on supplies
FOR EACH ROW
WHEN (NEW.state = 'processed')
EXECUTE FUNCTION unpacked_function();
