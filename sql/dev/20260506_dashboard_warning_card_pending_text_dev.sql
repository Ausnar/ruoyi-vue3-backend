-- 2026-05-06 dashboard warning card pending text update (dev)
-- Keeps permission code unchanged, only renames pending warning card wording.

SET NAMES utf8mb4;

SET @warning_card_menu_name := CONVERT(UNHEX('e5be85e5938de5ba94e9a284e8ada6') USING utf8mb4);
SET @warning_card_remark := CONVERT(UNHEX('e9a696e9a1b5e5be85e5938de5ba94e9a284e8ada6e58da1e78987') USING utf8mb4);

UPDATE sys_menu
SET menu_name = @warning_card_menu_name,
    remark = @warning_card_remark,
    update_by = 'admin',
    update_time = NOW()
WHERE perms = 'dashboard:card:alarm';
