/* 쿼리문 실험 */
SELECT p.*, i.imageUrl
FROM posts p
         LEFT JOIN images i ON p.post_id= i.post_id
WHERE p.post_id = 39;
