/**
 * Colores de los gráficos de los dashboards (Materiales, Materiales por curso y Convenios).
 * Tonos de la paleta verde Andina (styles.css, mejoras.css y edisson.css), de oscuro a claro,
 * para que las porciones vecinas se distingan por luminosidad y no solo por tono.
 */
export const VERDES_ANDINA = [
  '#174c38', // --green-dark / --andina-primario-oscuro
  '#21664b', // --green / --andina-primario
  '#387654', // --green-mid
  '#5c8f6c',
  '#8fb592',
  '#c3d9a8',
] as const;

/** Categoría que agrupa el resto («Otros»): gris neutro, no compite con los verdes */
export const GRIS_OTROS = '#b7c0ba';

/** Alertas (vencidos, cursos sin materiales): --andina-error */
export const ROJO_ALERTA = '#9d3e32';

/** Ámbar de los chips "en proceso" (mejoras.css) */
export const AMBAR_ANDINA = '#b7791f';

/** Texto y rejilla de los ejes */
export const TEXTO_SECUNDARIO = '#75827a'; // --muted
export const LINEA_REJILLA = '#e4e9e4'; // --line

/** Tipografía de los gráficos (la misma del resto de la app) */
export const FUENTE_GRAFICOS = { family: '"DM Sans", sans-serif', size: 13 };
