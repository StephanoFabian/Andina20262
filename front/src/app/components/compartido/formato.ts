import { Pipe, PipeTransform } from '@angular/core';

/** Tono visual de una etiqueta (clase .chip--<tono> en mejoras.css) */
export type Tono =
  | 'verde'
  | 'ambar'
  | 'azul'
  | 'gris'
  | 'rojo'
  | 'naranja'
  | 'salvia'
  | 'critico'
  | 'neutro';

/** Textos que no salen bien con la regla general (tildes y nombres propios) */
const TEXTOS: Record<string, string> = {
  CRITICA: 'Crítica',
  EN_RENOVACION: 'En renovación',
  HIBRIDA: 'Híbrida',
  ADMIN: 'Administrador',
  ADMIN_ESCUELA: 'Administrador de escuela',
  GOBIERNO_REGIONAL: 'Gobierno regional',
  COOPERACION_INTERNACIONAL: 'Cooperación internacional',
};

const TONOS_PRIORIDAD: Record<string, Tono> = {
  CRITICA: 'critico',
  ALTA: 'naranja',
  MEDIA: 'ambar',
  BAJA: 'salvia',
};

const TONOS_ESTADO: Record<string, Tono> = {
  // Positivos
  RESUELTA: 'verde',
  RESUELTO: 'verde',
  ACTIVO: 'verde',
  ACTIVA: 'verde',
  VIGENTE: 'verde',
  PRESENTE: 'verde',
  MATRICULADO: 'verde',
  APROBADO: 'verde',
  // En curso
  EN_PROCESO: 'ambar',
  EN_CURSO: 'ambar',
  EN_RENOVACION: 'ambar',
  TARDANZA: 'ambar',
  // Pendientes
  ABIERTA: 'azul',
  ABIERTO: 'azul',
  PROGRAMADA: 'azul',
  PENDIENTE: 'azul',
  JUSTIFICADA: 'azul',
  // Cerrados
  INACTIVO: 'gris',
  INACTIVA: 'gris',
  CERRADA: 'gris',
  CERRADO: 'gris',
  FINALIZADA: 'gris',
  FINALIZADO: 'gris',
  TRASLADADO: 'gris',
  // Negativos
  VENCIDO: 'rojo',
  RETIRADO: 'rojo',
  CANCELADO: 'rojo',
  CANCELADA: 'rojo',
  AUSENTE: 'rojo',
  ANULADO: 'rojo',
};

function clave(valor: unknown): string {
  return String(valor ?? '')
    .trim()
    .toUpperCase()
    .replace(/\s+/g, '_');
}

/** "EN_PROCESO" → "En proceso". Deja igual los textos que ya vienen escritos con minúsculas. */
export function textoLegible(valor: unknown): string {
  const original = String(valor ?? '').trim();
  if (!original) return '';
  const fijo = TEXTOS[clave(original)];
  if (fijo) return fijo;
  if (original !== original.toUpperCase()) return original;
  const texto = original.replace(/_/g, ' ').toLowerCase();
  return texto.charAt(0).toUpperCase() + texto.slice(1);
}

export function tonoDe(
  valor: unknown,
  tipo: 'estado' | 'prioridad' = 'estado',
): Tono {
  const k = clave(valor);
  return (
    (tipo === 'prioridad' ? TONOS_PRIORIDAD[k] : TONOS_ESTADO[k]) ?? 'neutro'
  );
}

/** yyyy-MM-dd (con o sin hora) → 25/08/2026 o 25/08/2026 14:30. No usa Date: evita desfases de zona horaria. */
export function fechaPeruana(valor: unknown): string {
  const texto = String(valor ?? '').trim();
  if (!texto) return '';
  const m = /^(\d{4})-(\d{2})-(\d{2})(?:[T ](\d{2}):(\d{2}))?/.exec(texto);
  if (!m) return texto;
  const fecha = `${m[3]}/${m[2]}/${m[1]}`;
  return m[4] ? `${fecha} ${m[4]}:${m[5]}` : fecha;
}

/** 14:30:00 → 14:30 */
export function horaPeruana(valor: unknown): string {
  const texto = String(valor ?? '').trim();
  if (!texto) return '';
  const m = /^(\d{1,2}):(\d{2})/.exec(texto);
  return m ? `${m[1].padStart(2, '0')}:${m[2]}` : texto;
}

const DIAS = [
  'Lunes',
  'Martes',
  'Miércoles',
  'Jueves',
  'Viernes',
  'Sábado',
  'Domingo',
];

/** Texto legible para valores en mayúsculas: {{ row.modalidad | etiqueta }} */
@Pipe({ name: 'etiqueta' })
export class EtiquetaPipe implements PipeTransform {
  transform(valor: unknown): string {
    return textoLegible(valor) || '—';
  }
}

/** Fecha en formato peruano: {{ row.fecha | fechaPe }} → 25/08/2026 */
@Pipe({ name: 'fechaPe' })
export class FechaPePipe implements PipeTransform {
  transform(valor: unknown): string {
    return fechaPeruana(valor) || '—';
  }
}

/** Hora de 24 h: {{ row.horaInicio | horaPe }} → 14:30 */
@Pipe({ name: 'horaPe' })
export class HoraPePipe implements PipeTransform {
  transform(valor: unknown): string {
    return horaPeruana(valor) || '—';
  }
}

/** Día de la semana 1–7 (1 = lunes, igual que en el formulario de horarios) */
@Pipe({ name: 'diaSemana' })
export class DiaSemanaPipe implements PipeTransform {
  transform(valor: unknown): string {
    const n = Number(valor);
    return DIAS[n - 1] ?? (valor == null || valor === '' ? '—' : String(valor));
  }
}
