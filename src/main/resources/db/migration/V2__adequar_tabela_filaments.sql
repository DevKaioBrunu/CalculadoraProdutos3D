ALTER TABLE filamentos RENAME TO filaments;

ALTER TABLE filaments RENAME COLUMN tipo TO material;
ALTER TABLE filaments RENAME COLUMN marca TO brand;
ALTER TABLE filaments RENAME COLUMN cor TO color;
ALTER TABLE filaments RENAME COLUMN preco_por_kg TO price_per_kg;
ALTER TABLE filaments RENAME COLUMN criado_em TO created_at;

ALTER TABLE filaments ADD COLUMN name VARCHAR(150);
ALTER TABLE filaments ADD COLUMN updated_at TIMESTAMP;

UPDATE filaments
SET name = CONCAT(material, ' - ', COALESCE(color, 'sem cor'))
WHERE name IS NULL;

UPDATE filaments
SET brand = 'Desconhecida'
WHERE brand IS NULL;

UPDATE filaments
SET updated_at = created_at
WHERE updated_at IS NULL;

ALTER TABLE filaments ALTER COLUMN name SET NOT NULL;
ALTER TABLE filaments ALTER COLUMN brand SET NOT NULL;
ALTER TABLE filaments ALTER COLUMN updated_at SET NOT NULL;
ALTER TABLE filaments ALTER COLUMN updated_at SET DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE filaments ALTER COLUMN price_per_kg TYPE NUMERIC(10, 2);
