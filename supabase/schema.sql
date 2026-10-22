create extension if not exists pgcrypto;
create table if not exists public.stores (
 id uuid primary key default gen_random_uuid(), name text not null, slug text unique not null,
 logo_url text, region text, is_active boolean not null default true, created_at timestamptz not null default now()
);
create table if not exists public.flyers (
 id uuid primary key default gen_random_uuid(), store_id uuid not null references public.stores(id) on delete cascade,
 title text not null, cover_image_url text, valid_from date, valid_to date,
 status text not null default 'draft' check (status in ('draft','published','expired')), created_at timestamptz not null default now()
);
create table if not exists public.flyer_pages (
 id uuid primary key default gen_random_uuid(), flyer_id uuid not null references public.flyers(id) on delete cascade,
 page_number integer not null check (page_number > 0), image_url text not null, unique(flyer_id,page_number)
);
create table if not exists public.categories (
 id uuid primary key default gen_random_uuid(), name text not null unique, slug text not null unique
);
create table if not exists public.offers (
 id uuid primary key default gen_random_uuid(), flyer_id uuid references public.flyers(id) on delete set null,
 store_id uuid not null references public.stores(id) on delete cascade, category_id uuid references public.categories(id) on delete set null,
 title text not null, brand text, pack_size text, price numeric(12,2) not null check(price >= 0),
 currency char(3) not null default 'FJD', image_url text, valid_from date, valid_to date,
 verification_status text not null default 'pending' check(verification_status in ('pending','verified','rejected')),
 verified_at timestamptz, created_at timestamptz not null default now()
);
create table if not exists public.favourites (
 user_id uuid not null references auth.users(id) on delete cascade,
 offer_id uuid not null references public.offers(id) on delete cascade,
 created_at timestamptz not null default now(), primary key(user_id,offer_id)
);
create table if not exists public.shopping_list_items (
 id uuid primary key default gen_random_uuid(), user_id uuid not null references auth.users(id) on delete cascade,
 offer_id uuid references public.offers(id) on delete set null, item_name text not null,
 quantity numeric(10,2) not null default 1 check(quantity > 0), is_checked boolean not null default false,
 created_at timestamptz not null default now()
);
create table if not exists public.price_alerts (
 id uuid primary key default gen_random_uuid(), user_id uuid not null references auth.users(id) on delete cascade,
 offer_title text not null, target_price numeric(12,2), store_id uuid references public.stores(id) on delete set null,
 is_active boolean not null default true, created_at timestamptz not null default now()
);
create index if not exists offers_store_idx on public.offers(store_id);
create index if not exists offers_validity_idx on public.offers(valid_from,valid_to);
alter table public.stores enable row level security;
alter table public.flyers enable row level security;
alter table public.flyer_pages enable row level security;
alter table public.categories enable row level security;
alter table public.offers enable row level security;
alter table public.favourites enable row level security;
alter table public.shopping_list_items enable row level security;
alter table public.price_alerts enable row level security;
-- Add reviewed RLS policies before using with real users or public data.
