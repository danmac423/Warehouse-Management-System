ALTER TABLE IF EXISTS public.products_orders_history
    DROP CONSTRAINT hist_order_fk;

ALTER TABLE IF EXISTS public.products_orders_history
    ADD CONSTRAINT hist_order_fk FOREIGN KEY (order_id)
    REFERENCES public.orders_history (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION
    NOT VALID;