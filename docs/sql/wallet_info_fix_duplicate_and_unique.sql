-- 1) 查看重复数据（同一用户+钱包类型出现多条）
SELECT user_id, type, COUNT(*) AS cnt
FROM wallet_info
GROUP BY user_id, type
HAVING COUNT(*) > 1;

-- 2) 删除重复，保留 id 最大的一条（通常是最新）
DELETE w1
FROM wallet_info w1
JOIN wallet_info w2
  ON w1.user_id = w2.user_id
 AND w1.type = w2.type
 AND w1.id < w2.id;

-- 3) 加唯一索引，防止并发创建产生重复
ALTER TABLE wallet_info
  ADD UNIQUE KEY uk_wallet_info_user_type (user_id, type);
