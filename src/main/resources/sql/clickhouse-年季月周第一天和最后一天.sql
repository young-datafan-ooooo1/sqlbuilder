select
    t.create_time,
    toStartOfYear(t.create_time) as y,
    toStartOfQuarter(t.create_time) as q,
    toStartOfMonth(t.create_time) as m,
    toStartOfWeek(t.create_time) as w,
    toStartOfDay(t.create_time) as d,
    subtractDays(addYears(toStartOfYear(t.create_time),1),1) as y_end,
    toStartOfYear(t.create_time) as y_begin,
    subtractDays(addMonths(toStartOfQuarter(t.create_time),3),1) as q_end,
    toStartOfQuarter(t.create_time) as q_begin,
    subtractDays(addMonths(toStartOfMonth(t.create_time),1),1) as m_end ,
    toStartOfMonth(t.create_time) as m_bgin,
    addDays(toStartOfWeek(t.create_time),7) as w_end,
    addDays(toStartOfWeek(t.create_time),1) as w_begin
from
    test t