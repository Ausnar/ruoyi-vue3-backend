-- Correct extinguisher warning semantics:
-- 1. The 6/10/10/12-year limit is the whole extinguisher scrap date, not agent expiry.
-- 2. The enabled warning is generated 30 days before scrap_date.
-- 3. Agent expiry remains reserved until a trustworthy fill/expiry source exists.

UPDATE fe_extinguisher
   SET scrap_date = CASE
        WHEN extinguisher_type = 'water_based' THEN date_add(production_date, interval 6 year)
        WHEN extinguisher_type IN ('dry_powder', 'clean_gas') THEN date_add(production_date, interval 10 year)
        WHEN extinguisher_type = 'co2' THEN date_add(production_date, interval 12 year)
        ELSE scrap_date
       END,
       update_by = 'warning-semantics-migration',
       update_time = now()
 WHERE production_date IS NOT NULL
   AND extinguisher_type IN ('water_based', 'dry_powder', 'clean_gas', 'co2')
   AND scrap_date IS NULL;

UPDATE fe_device_warning w
JOIN fe_extinguisher e ON e.extinguisher_id = w.extinguisher_id
LEFT JOIN fe_device_warning canonical
       ON canonical.warning_type = 'extinguisher_scrap_due'
      AND canonical.object_type = w.object_type
      AND canonical.object_id = w.object_id
      AND canonical.warning_id != w.warning_id
      AND (canonical.del_flag IS NULL OR canonical.del_flag = '0')
   SET w.warning_type = 'extinguisher_scrap_due',
       w.window_start_time = e.scrap_date,
       w.window_end_time = e.scrap_date,
       w.threshold_snapshot = 'scrap_date <= current_date + 30 days',
       w.evidence_summary = concat('extinguisher=', coalesce(e.label_code, e.extinguisher_id),
                                   ', scrap_date=', date_format(e.scrap_date, '%Y-%m-%d')),
       w.update_by = 'warning-semantics-migration',
       w.update_time = now()
 WHERE w.warning_type = 'extinguisher_expired'
   AND e.scrap_date IS NOT NULL
   AND canonical.warning_id IS NULL;

UPDATE fe_extinguisher
   SET expiry_date = NULL,
       update_by = 'warning-semantics-migration',
       update_time = now()
 WHERE production_date IS NOT NULL
   AND extinguisher_type IN ('water_based', 'dry_powder', 'clean_gas', 'co2')
   AND scrap_date = CASE
        WHEN extinguisher_type = 'water_based' THEN date_add(production_date, interval 6 year)
        WHEN extinguisher_type IN ('dry_powder', 'clean_gas') THEN date_add(production_date, interval 10 year)
        WHEN extinguisher_type = 'co2' THEN date_add(production_date, interval 12 year)
       END
   AND expiry_date = scrap_date;
