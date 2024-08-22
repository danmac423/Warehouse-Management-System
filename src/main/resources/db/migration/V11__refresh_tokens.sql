CREATE TABLE IF NOT EXISTS public.refresh_tokens
(
    username character varying(20) NOT NULL,
    token character varying(255) NOT NULL,
    created_at timestamp with time zone NOT NULL,
    expires_at timestamp with time zone NOT NULL,
    PRIMARY KEY (username)
);


CREATE EXTENSION IF NOT EXISTS pg_cron;


DO $$
    BEGIN
        IF EXISTS (SELECT 1 FROM cron.job WHERE jobname = 'delete_expired_tokens') THEN
            PERFORM cron.unschedule((SELECT jobid FROM cron.job WHERE jobname = 'delete_expired_tokens'));
        END IF;
    END $$;

SELECT cron.schedule(
               'delete_expired_tokens',
               '0 0 * * *',
               $$DELETE FROM public.refresh_tokens WHERE expires_at < NOW();$$
       );

