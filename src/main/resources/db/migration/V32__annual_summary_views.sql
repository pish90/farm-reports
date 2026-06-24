CREATE VIEW summary_milk_prod AS
SELECT  mr.farm_id,
        mr.year,
        mr.month,
        SUM(mp.litres) AS total_litres
FROM milk_production mp
JOIN monthly_reports mr ON mp.report_id = mr.id
GROUP BY mr.farm_id, mr.year, mr.month;

CREATE VIEW summary_livestock AS
SELECT  mr.farm_id,
        mr.year,
        mr.month,
        lt.category,
        lt.type,
        SUM(lr.count) AS total_count
FROM livestock_returns lr
JOIN monthly_reports mr ON lr.report_id = mr.id
JOIN livestock_types lt  ON lr.livestock_type_id = lt.id
GROUP BY mr.farm_id, mr.year, mr.month, lt.category, lt.type;
