select DISTINCT  dac.province ,tb.*   from dim_area_cn dac, (
select * from fact_air_cn  where area in ('广州市' , '深圳市' , '昆明市') and dt >= '2013-12-01'
and dt <= '2013-12-05'
 ) tb where dac.city=tb.area  order by dac.province  desc  , tb.area , tb.dt