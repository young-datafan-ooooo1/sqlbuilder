SELECT
    t.create_time,
    CONVERT ( datetime, CONVERT ( CHAR ( 4 ), YEAR ( t.create_time ) ) + '-12-31' ) AS 年末,
    CONVERT ( datetime, CONVERT ( CHAR ( 4 ), YEAR ( t.create_time ) ) + '-01-01' ) AS 年初,
    DATEADD( quarter, 1, DATEADD( quarter, DATEDIFF( quarter, 0, t.create_time ), - 1 ) ) AS 季末,
    DATEADD( quarter, 1, DATEADD( quarter, DATEDIFF( quarter, 0, t.create_time ) - 1, 0 ) ) AS 季初,
    DATEADD(
        DAY,- 1,
            CONVERT ( datetime, CONVERT ( VARCHAR ( 8 ), DATEADD( MONTH, 1, t.create_time ), 23 ) + '01' )
        ) AS 月末,
    CONVERT ( datetime, CONVERT ( VARCHAR ( 8 ), t.create_time, 23 ) + '01' ) AS 月初 ,
    DATEADD( DAY, ( 8-DATEPART ( weekday, t.create_time ) ), t.create_time ) AS 周末,
    DATEADD( DAY, ( 2-DATEPART ( weekday, t.create_time ) ), t.create_time ) AS 周初
FROM
    test t;