SELECT
    t.create_time,
    EXTRACT( YEAR FROM t.create_time ) AS 年,
    EXTRACT( QUARTER FROM t.create_time ) AS 季,
    EXTRACT( MONTH FROM t.create_time ) AS 月,
    EXTRACT( WEEK FROM t.create_time ) AS 周,
    DAYOFWEEK( t.create_time ) AS 周几,
    STR_TO_DATE( concat( date_format( t.create_time, '%Y' ), '-12-31' ), '%Y-%m-%d' ) AS 年末,
    STR_TO_DATE( concat( date_format( t.create_time, '%Y' ), '-01-01' ), '%Y-%m-%d' ) AS 年初,
    DATE_ADD( STR_TO_DATE( concat( date_format( t.create_time, '%Y' ), '-01-31' ), '%Y-%m-%d' ), INTERVAL (3 * EXTRACT( QUARTER FROM t.create_time )-1) MONTH ) 季末,
    DATE_ADD( STR_TO_DATE( concat( date_format( t.create_time, '%Y' ), '-01-01' ), '%Y-%m-%d' ), INTERVAL (3 * EXTRACT( QUARTER FROM t.create_time )-1) MONTH ) 季初,
    LAST_DAY( t.create_time ) AS 月末,
    DATE_ADD( LAST_DAY( date_sub( t.create_time, INTERVAL 1 MONTH )), INTERVAL 1 DAY ) AS 月初,
    DATE_ADD( t.create_time, INTERVAL ( 8-DAYOFWEEK ( t.create_time )) DAY ) 周末,
    DATE_ADD( t.create_time, INTERVAL ( 2-DAYOFWEEK ( t.create_time )) DAY ) 周初
FROM
    `test` t;