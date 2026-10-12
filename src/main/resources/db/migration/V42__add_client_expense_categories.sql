-- Expense categories the client uses in their own books that had no equivalent here, so expense
-- imports using them landed with no category. Codes follow the existing chart's numbering
-- (2xxx direct crop/farm inputs, 4xxx running costs). Guarded by name because production has
-- had categories added outside migrations (e.g. Salaries, Security), so don't assume its exact
-- contents.
INSERT INTO expense_categories (account_code, account_name)
SELECT v.code, v.name
FROM (VALUES
    ('2007', 'Garden Expenses'),
    ('2008', 'Land Preparation'),
    ('4008', 'Water')
) AS v(code, name)
WHERE NOT EXISTS (
    SELECT 1 FROM expense_categories c WHERE lower(trim(c.account_name)) = lower(v.name)
);
