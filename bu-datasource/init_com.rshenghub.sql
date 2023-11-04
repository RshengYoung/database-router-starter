create database dsp_rshenghub_com encoding = 'UTF8';

create schema bu;

create table bu.company (
    id varchar(64) not null,
    name varchar(128) not null,
    cname varchar(24),
    ename varchar(32),
    since timestamp(3),
    create_user_id varchar(64),
    create_time timestamp(3),
    modify_user_id varchar(64),
    modify_time timestamp(3),
    primary key(id)
);

create table bu.company_site (
    id varchar(64) not null,
    company_id varchar(64) not null,
    name varchar(128) not null,
    cname varchar(24),
    ename varchar(32),
    address varchar(128),
    owner varchar(64),
    create_user_id varchar(64),
    create_time timestamp(3),
    modify_user_id varchar(64),
    modify_time timestamp(3),
    primary key(id),
    foreign key(company_id) references bu.company(id)
);

INSERT INTO
    bu.company (id, name, cname, ename, since)
VALUES
    (
        '1247206b39144662b6f34b349a6a455b',
        'RshengHub Testing',
        'RS',
        '...',
        '1971-04-04 00:00:00'
    );

INSERT INTO
    bu.company_site (
        id,
        company_id,
        name,
        cname,
        ename,
        address,
        owner
    )
VALUES
    (
        '1',
        '1247206b39144662b6f34b349a6a455b',
        'RshengHub Testing',
        'RS',
        'TEP1',
        '123123',
        'ABC'
    );

INSERT INTO
    bu.company_site (
        id,bu.
        company_id,
        name,
        cname,
        ename,
        address,
        owner
    )
VALUES
    (
        '2',
        '1247206b39144662b6f34b349a6a455b',
        'RshengHub Testing',
        'RSH',
        'TEP2',
        'ADSJ123',
        'CBD'
    );