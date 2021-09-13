SELECT T
           .create_date,
       date_trunc( 'year', T.create_date ) AS 年,
       date_trunc( 'quarter', T.create_date ) AS 季,
       date_trunc( 'month', T.create_date ) AS 月,
       date_trunc( 'week', T.create_date ) AS 周,
       date_trunc( 'day', T.create_date ) AS 日,
       ( ( date_trunc( 'year', T.create_date ) + INTERVAL '1 year' ) - INTERVAL '1 day' ) AS 年末,
    date_trunc( 'year', T.create_date ) AS 年初,
    ( ( date_trunc( 'quarter', T.create_date ) + INTERVAL '3 month' ) - INTERVAL '1 day' ) AS 季末,
    date_trunc( 'quarter', T.create_date ) AS 季初,
    ( ( date_trunc( 'month', T.create_date ) + INTERVAL '1 month' ) - INTERVAL '1 day' ) AS 月末,
    date_trunc( 'month', T.create_date ) AS 月初,
    ( date_trunc( 'week', T.create_date ) + INTERVAL '6 day' ) AS 周末,
    date_trunc( 'week', T.create_date ) AS 周初
FROM
    test T;