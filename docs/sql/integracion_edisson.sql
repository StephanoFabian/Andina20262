-- Integración aditiva e idempotente del convenio entregado por Edisson.
-- Conserva institucion e id_colegio para los registros y clientes anteriores.
ALTER TABLE convenios ADD COLUMN IF NOT EXISTS tipo_entidad varchar(50);
ALTER TABLE convenios ADD COLUMN IF NOT EXISTS region varchar(50);
ALTER TABLE convenios ADD COLUMN IF NOT EXISTS presupuesto numeric(14,2);
ALTER TABLE convenios ALTER COLUMN nombre TYPE varchar(200);
ALTER TABLE convenios ALTER COLUMN institucion TYPE varchar(200);
ALTER TABLE convenios ALTER COLUMN id_colegio DROP NOT NULL;
CREATE INDEX IF NOT EXISTS ix_convenio_vencimiento ON convenios (estado,fecha_fin,id_convenio);
CREATE INDEX IF NOT EXISTS ix_material_curso_curso_material ON material_curso (id_curso,id_material);
CREATE INDEX IF NOT EXISTS ix_material_tipos ON material (tipo);
