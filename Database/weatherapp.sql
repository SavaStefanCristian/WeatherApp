--
-- PostgreSQL database dump
--

-- Dumped from database version 17.4
-- Dumped by pg_dump version 17.4

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: role; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE public.role AS ENUM (
    'USER',
    'ADMIN'
);


ALTER TYPE public.role OWNER TO postgres;

--
-- Name: weather_state; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE public.weather_state AS ENUM (
    'CLEAR',
    'CLOUDY',
    'RAINING',
    'SNOWING'
);


ALTER TYPE public.weather_state OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: app_current_weather; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.app_current_weather (
    current_weather_id bigint NOT NULL,
    location_id bigint NOT NULL,
    temperature integer NOT NULL,
    state public.weather_state NOT NULL
);


ALTER TABLE public.app_current_weather OWNER TO postgres;

--
-- Name: app_current_weather_current_weather_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.app_current_weather_current_weather_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.app_current_weather_current_weather_id_seq OWNER TO postgres;

--
-- Name: app_current_weather_current_weather_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.app_current_weather_current_weather_id_seq OWNED BY public.app_current_weather.current_weather_id;


--
-- Name: app_current_weather_location_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.app_current_weather_location_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.app_current_weather_location_id_seq OWNER TO postgres;

--
-- Name: app_current_weather_location_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.app_current_weather_location_id_seq OWNED BY public.app_current_weather.location_id;


--
-- Name: app_forecast; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.app_forecast (
    forecast_id bigint NOT NULL,
    location_id bigint NOT NULL,
    forecast_date date NOT NULL,
    high integer NOT NULL,
    low integer NOT NULL,
    state public.weather_state NOT NULL,
    CONSTRAINT check_high CHECK (((high >= '-100'::integer) AND (high <= 100))),
    CONSTRAINT check_low CHECK (((low >= '-100'::integer) AND (low <= 100)))
);


ALTER TABLE public.app_forecast OWNER TO postgres;

--
-- Name: app_forecase_forecast_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.app_forecase_forecast_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.app_forecase_forecast_id_seq OWNER TO postgres;

--
-- Name: app_forecase_forecast_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.app_forecase_forecast_id_seq OWNED BY public.app_forecast.forecast_id;


--
-- Name: app_forecase_location_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.app_forecase_location_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.app_forecase_location_id_seq OWNER TO postgres;

--
-- Name: app_forecase_location_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.app_forecase_location_id_seq OWNED BY public.app_forecast.location_id;


--
-- Name: app_location; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.app_location (
    location_id bigint NOT NULL,
    name text,
    latitude double precision NOT NULL,
    longitude double precision NOT NULL
);


ALTER TABLE public.app_location OWNER TO postgres;

--
-- Name: app_location_location_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.app_location_location_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.app_location_location_id_seq OWNER TO postgres;

--
-- Name: app_location_location_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.app_location_location_id_seq OWNED BY public.app_location.location_id;


--
-- Name: app_role; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.app_role (
    role_id bigint NOT NULL,
    role public.role NOT NULL
);


ALTER TABLE public.app_role OWNER TO postgres;

--
-- Name: app_role_role_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.app_role_role_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.app_role_role_id_seq OWNER TO postgres;

--
-- Name: app_role_role_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.app_role_role_id_seq OWNED BY public.app_role.role_id;


--
-- Name: app_user; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.app_user (
    user_id bigint NOT NULL,
    username text NOT NULL,
    pass_hash text NOT NULL,
    last_location_id bigint
);


ALTER TABLE public.app_user OWNER TO postgres;

--
-- Name: app_user_last_location_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.app_user_last_location_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.app_user_last_location_id_seq OWNER TO postgres;

--
-- Name: app_user_last_location_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.app_user_last_location_id_seq OWNED BY public.app_user.last_location_id;


--
-- Name: app_user_user_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.app_user_user_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.app_user_user_id_seq OWNER TO postgres;

--
-- Name: app_user_user_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.app_user_user_id_seq OWNED BY public.app_user.user_id;


--
-- Name: user_role; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.user_role (
    user_id bigint NOT NULL,
    role_id bigint NOT NULL
);


ALTER TABLE public.user_role OWNER TO postgres;

--
-- Name: user_role_role_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.user_role_role_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.user_role_role_id_seq OWNER TO postgres;

--
-- Name: user_role_role_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.user_role_role_id_seq OWNED BY public.user_role.role_id;


--
-- Name: user_role_user_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.user_role_user_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.user_role_user_id_seq OWNER TO postgres;

--
-- Name: user_role_user_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.user_role_user_id_seq OWNED BY public.user_role.user_id;


--
-- Name: app_current_weather current_weather_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.app_current_weather ALTER COLUMN current_weather_id SET DEFAULT nextval('public.app_current_weather_current_weather_id_seq'::regclass);


--
-- Name: app_current_weather location_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.app_current_weather ALTER COLUMN location_id SET DEFAULT nextval('public.app_current_weather_location_id_seq'::regclass);


--
-- Name: app_forecast forecast_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.app_forecast ALTER COLUMN forecast_id SET DEFAULT nextval('public.app_forecase_forecast_id_seq'::regclass);


--
-- Name: app_forecast location_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.app_forecast ALTER COLUMN location_id SET DEFAULT nextval('public.app_forecase_location_id_seq'::regclass);


--
-- Name: app_location location_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.app_location ALTER COLUMN location_id SET DEFAULT nextval('public.app_location_location_id_seq'::regclass);


--
-- Name: app_role role_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.app_role ALTER COLUMN role_id SET DEFAULT nextval('public.app_role_role_id_seq'::regclass);


--
-- Name: app_user user_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.app_user ALTER COLUMN user_id SET DEFAULT nextval('public.app_user_user_id_seq'::regclass);


--
-- Name: app_user last_location_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.app_user ALTER COLUMN last_location_id SET DEFAULT nextval('public.app_user_last_location_id_seq'::regclass);


--
-- Name: user_role user_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_role ALTER COLUMN user_id SET DEFAULT nextval('public.user_role_user_id_seq'::regclass);


--
-- Name: user_role role_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_role ALTER COLUMN role_id SET DEFAULT nextval('public.user_role_role_id_seq'::regclass);


--
-- Name: app_current_weather app_current_weather_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.app_current_weather
    ADD CONSTRAINT app_current_weather_pkey PRIMARY KEY (current_weather_id);


--
-- Name: app_forecast app_forecast_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.app_forecast
    ADD CONSTRAINT app_forecast_pkey PRIMARY KEY (forecast_id);


--
-- Name: app_location app_location_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.app_location
    ADD CONSTRAINT app_location_pkey PRIMARY KEY (location_id);


--
-- Name: app_role app_role_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.app_role
    ADD CONSTRAINT app_role_pkey PRIMARY KEY (role_id);


--
-- Name: app_user app_user_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.app_user
    ADD CONSTRAINT app_user_pkey PRIMARY KEY (user_id);


--
-- Name: app_user app_user_username_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.app_user
    ADD CONSTRAINT app_user_username_key UNIQUE (username);


--
-- Name: app_location latitude_check; Type: CHECK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE public.app_location
    ADD CONSTRAINT latitude_check CHECK (((latitude >= ('-90'::integer)::double precision) AND (latitude <= (90)::double precision))) NOT VALID;


--
-- Name: app_location longitude_check; Type: CHECK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE public.app_location
    ADD CONSTRAINT longitude_check CHECK (((longitude >= ('-180'::integer)::double precision) AND (longitude <= (180)::double precision))) NOT VALID;


--
-- Name: app_current_weather temperature_check; Type: CHECK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE public.app_current_weather
    ADD CONSTRAINT temperature_check CHECK (((temperature >= '-100'::integer) AND (temperature <= 100))) NOT VALID;


--
-- Name: user_role user_role_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_role
    ADD CONSTRAINT user_role_pkey PRIMARY KEY (user_id, role_id);


--
-- Name: app_current_weather app_current_weather_location_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.app_current_weather
    ADD CONSTRAINT app_current_weather_location_id_fkey FOREIGN KEY (location_id) REFERENCES public.app_location(location_id);


--
-- Name: app_forecast app_forecast_location_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.app_forecast
    ADD CONSTRAINT app_forecast_location_id_fkey FOREIGN KEY (location_id) REFERENCES public.app_location(location_id) NOT VALID;


--
-- Name: app_user app_user_last_location_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.app_user
    ADD CONSTRAINT app_user_last_location_id_fkey FOREIGN KEY (last_location_id) REFERENCES public.app_location(location_id) NOT VALID;


--
-- Name: user_role user_role_role_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_role
    ADD CONSTRAINT user_role_role_id_fkey FOREIGN KEY (role_id) REFERENCES public.app_role(role_id);


--
-- Name: user_role user_role_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_role
    ADD CONSTRAINT user_role_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.app_user(user_id);


--
-- PostgreSQL database dump complete
--

