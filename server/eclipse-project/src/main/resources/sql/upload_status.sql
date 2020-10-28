-- Show status of uploaded files

SELECT cty.name, filename,
       (case when(cdb.cvr_file_id = uf.id) then 'CVRs' else 'manifest' end) as type,
       approximate_record_count AS approx_count,
       size, status, timestamp, computed_hash, submitted_hash
FROM uploaded_file AS uf
LEFT JOIN
  county as cty ON cty.id = uf.county_id
LEFT JOIN
  county_dashboard as cdb ON cdb.id = cty.id
ORDER BY status, timestamp
;
