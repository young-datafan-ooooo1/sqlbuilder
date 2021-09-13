SELECT
    t."create_time",
    trunc( t."create_time", 'YYYY' ) AS 年,
    trunc( t."create_time", 'Q' ) AS 季,
    trunc( t."create_time", 'MM' ) AS 月,
    trunc( t."create_time", 'W' ) AS 周,
    trunc( t."create_time", 'D' ) AS 日,
    add_months( trunc( t."create_time", 'yyyy' ), 12 ) - 1 AS 年末,
    trunc( t."create_time", 'yyyy' ) AS 年初,
    add_months( trunc( t."create_time", 'Q' ), 3 ) - 1 AS 季末,
    trunc( t."create_time", 'Q' ) AS 季初,
    last_day( t."create_time" ) AS 月末,
    trunc( t."create_time", 'MM' ) AS 月初,
    trunc( t."create_time", 'D' ) + 7 AS 周末,
    trunc( t."create_time", 'D' ) + 1 AS 周初
FROM
    "test" t;