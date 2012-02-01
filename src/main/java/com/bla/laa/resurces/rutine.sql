delete from csddliv.answers
where (HASH || CAST(csddid as CHAR(5) )) in
(select HASH || CAST(csddid as CHAR(5) ) from csddliv.answers
  group by HASH, csddid
  having COUNT(*) > 2
)
