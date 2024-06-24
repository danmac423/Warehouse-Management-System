CREATE OR REPLACE FUNCTION delete_addr_function()
RETURNS TRIGGER
AS
$$
BEGIN
    DELETE FROM addresses 
    WHERE id = OLD.address_id;
    
    RETURN OLD;  
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER delete_addr
AFTER DELETE ON suppliers
FOR EACH ROW
EXECUTE FUNCTION delete_addr_function();
