-- List of selections from the random sample for the given contest

-- Listed in random selection order in the contest_cvr_ids field, for verification
-- of the random selection procedure.  The values there can also be
-- correlated with cvr_id in contest_comparison export, and with audited_cvr_count.

SELECT
   cr.min_margin,
   cr.contest_name,
   ca.contest_cvr_ids
FROM
   comparison_audit AS ca
LEFT JOIN
   contest_result AS cr
   ON ca.contest_result_id = cr.id

;
