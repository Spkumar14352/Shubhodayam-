-- Shared 10-minute cache for the live-status Edge Function.
create table public.live_status_cache (
  id integer primary key default 1 check (id = 1),
  payload jsonb not null,
  fetched_at timestamptz not null
);

-- RLS with no policies: only the function's service role can read or write.
alter table public.live_status_cache enable row level security;
