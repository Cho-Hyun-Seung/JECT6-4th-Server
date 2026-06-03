INSERT INTO categories (name)
SELECT seed.name
FROM (
    VALUES
        ('음식'),
        ('뷰티'),
        ('문화'),
        ('여행'),
        ('테크/it'),
        ('펫'),
        ('생활용품'),
        ('패션'),
        ('기타(도서/취미/기타)')
) AS seed(name)
WHERE NOT EXISTS (
    SELECT 1
    FROM categories category
    WHERE category.name = seed.name
);
