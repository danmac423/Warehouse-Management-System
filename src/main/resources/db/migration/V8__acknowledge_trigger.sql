-- Corrected function definition
CREATE OR REPLACE FUNCTION acknowledge_function()
RETURNS TRIGGER
AS
$$
BEGIN
    -- Update only the row that triggered the function
    UPDATE supplies
    SET arrival_date = CURRENT_TIMESTAMP 
    WHERE id = NEW.id;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Corrected trigger definition
CREATE OR REPLACE TRIGGER acknowledge_supply
AFTER UPDATE OF status ON supplies
FOR EACH ROW
WHEN (NEW.status = 'arrived')
EXECUTE FUNCTION acknowledge_function();
