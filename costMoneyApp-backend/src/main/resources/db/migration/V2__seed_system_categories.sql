-- System expense categories
INSERT INTO categories (id, user_id, parent_id, name, icon, color, type, is_system, sort_order) VALUES
-- 餐饮
(1,  NULL, NULL, '餐饮', '🍜', '#FF6B6B', 'expense', TRUE, 1),
(2,  NULL, 1,    '早餐', '🥐', '#FF6B6B', 'expense', TRUE, 0),
(3,  NULL, 1,    '午餐', '🍱', '#FF6B6B', 'expense', TRUE, 1),
(4,  NULL, 1,    '晚餐', '🍲', '#FF6B6B', 'expense', TRUE, 2),
(5,  NULL, 1,    '奶茶', '🧋', '#FF6B6B', 'expense', TRUE, 3),
(6,  NULL, 1,    '零食', '🍪', '#FF6B6B', 'expense', TRUE, 4),
(7,  NULL, 1,    '水果', '🍎', '#FF6B6B', 'expense', TRUE, 5),
-- 交通
(8,  NULL, NULL, '交通', '🚗', '#4ECDC4', 'expense', TRUE, 2),
(9,  NULL, 8,    '公交', '🚌', '#4ECDC4', 'expense', TRUE, 0),
(10, NULL, 8,    '地铁', '🚇', '#4ECDC4', 'expense', TRUE, 1),
(11, NULL, 8,    '打车', '🚕', '#4ECDC4', 'expense', TRUE, 2),
(12, NULL, 8,    '油费', '⛽', '#4ECDC4', 'expense', TRUE, 3),
(13, NULL, 8,    '停车', '🅿️', '#4ECDC4', 'expense', TRUE, 4),
-- 购物
(14, NULL, NULL, '购物', '🛒', '#FFE66D', 'expense', TRUE, 3),
(15, NULL, 14,   '服装', '👔', '#FFE66D', 'expense', TRUE, 0),
(16, NULL, 14,   '数码', '📱', '#FFE66D', 'expense', TRUE, 1),
(17, NULL, 14,   '日用', '🧴', '#FFE66D', 'expense', TRUE, 2),
(18, NULL, 14,   '化妆品', '💄', '#FFE66D', 'expense', TRUE, 3),
-- 住房
(19, NULL, NULL, '住房', '🏠', '#95E1D3', 'expense', TRUE, 4),
(20, NULL, 19,   '房租', '🏘️', '#95E1D3', 'expense', TRUE, 0),
(21, NULL, 19,   '水电煤', '💡', '#95E1D3', 'expense', TRUE, 1),
(22, NULL, 19,   '物业', '🏢', '#95E1D3', 'expense', TRUE, 2),
-- 娱乐
(23, NULL, NULL, '娱乐', '🎮', '#DDA0DD', 'expense', TRUE, 5),
(24, NULL, 23,   '电影', '🎬', '#DDA0DD', 'expense', TRUE, 0),
(25, NULL, 23,   '游戏', '🎮', '#DDA0DD', 'expense', TRUE, 1),
(26, NULL, 23,   '旅游', '✈️', '#DDA0DD', 'expense', TRUE, 2),
-- 医疗
(27, NULL, NULL, '医疗', '🏥', '#FF8C69', 'expense', TRUE, 6),
(28, NULL, 27,   '药品', '💊', '#FF8C69', 'expense', TRUE, 0),
(29, NULL, 27,   '门诊', '🩺', '#FF8C69', 'expense', TRUE, 1),
-- 教育
(30, NULL, NULL, '教育', '📚', '#87CEEB', 'expense', TRUE, 7),
(31, NULL, 30,   '学费', '🎓', '#87CEEB', 'expense', TRUE, 0),
(32, NULL, 30,   '书籍', '📖', '#87CEEB', 'expense', TRUE, 1),
-- 通讯
(33, NULL, NULL, '通讯', '📞', '#98D8C8', 'expense', TRUE, 8),
(34, NULL, 33,   '话费', '📱', '#98D8C8', 'expense', TRUE, 0),
(35, NULL, 33,   '网费', '🌐', '#98D8C8', 'expense', TRUE, 1),
-- 人情
(36, NULL, NULL, '人情', '🎁', '#F7DC6F', 'expense', TRUE, 9),
(37, NULL, 36,   '礼物', '🎁', '#F7DC6F', 'expense', TRUE, 0),
(38, NULL, 36,   '红包', '🧧', '#F7DC6F', 'expense', TRUE, 1),
-- 其他支出
(39, NULL, NULL, '其他', '📦', '#B8B8B8', 'expense', TRUE, 10),

-- System income categories
(100, NULL, NULL, '工资', '💰', '#4CAF50', 'income', TRUE, 0),
(101, NULL, 100,  '基本工资', '💵', '#4CAF50', 'income', TRUE, 0),
(102, NULL, 100,  '奖金', '🎉', '#4CAF50', 'income', TRUE, 1),
(103, NULL, 100,  '补贴', '💲', '#4CAF50', 'income', TRUE, 2),
(104, NULL, NULL, '投资', '📈', '#2196F3', 'income', TRUE, 1),
(105, NULL, 104,  '股票', '📊', '#2196F3', 'income', TRUE, 0),
(106, NULL, 104,  '基金', '💹', '#2196F3', 'income', TRUE, 1),
(107, NULL, 104,  '理财', '🏦', '#2196F3', 'income', TRUE, 2),
(108, NULL, NULL, '兼职', '💼', '#FF9800', 'income', TRUE, 2),
(109, NULL, NULL, '回收', '♻️', '#8BC34A', 'income', TRUE, 3),
(110, NULL, NULL, '其他', '💎', '#9C27B0', 'income', TRUE, 4);
