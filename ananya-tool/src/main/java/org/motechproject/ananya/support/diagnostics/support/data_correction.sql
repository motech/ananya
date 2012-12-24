
-- All quizzes that started and ended within bug time frame

select
  cim.*
from
  report.course_item_measure cim
  join
  report.course_item_dimension cid
    on cim.course_item_id = cid.id
  join
  report.front_line_worker


-- All
