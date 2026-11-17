import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

/** Fecha de hoy en formato yyyy-MM-dd (hora local) */
export function hoyISO(): string {
  const hoy = new Date();
  const mes = String(hoy.getMonth() + 1).padStart(2, '0');
  const dia = String(hoy.getDate()).padStart(2, '0');
  return `${hoy.getFullYear()}-${mes}-${dia}`;
}

/** Equivale a @NotBlank: rechaza textos con solo espacios */
export const noVacio: ValidatorFn = (control) => {
  const valor = control.value;
  return typeof valor === 'string' && valor.length > 0 && valor.trim() === ''
    ? { vacio: true }
    : null;
};

/** Equivale a @PastOrPresent sobre un yyyy-MM-dd */
export const fechaNoFutura: ValidatorFn = (control) => {
  const valor = control.value as string | null;
  return valor && valor > hoyISO() ? { fechaFutura: true } : null;
};

/** Equivale a @Past sobre un yyyy-MM-dd: estrictamente anterior a hoy */
export const fechaPasada: ValidatorFn = (control) => {
  const valor = control.value as string | null;
  return valor && valor >= hoyISO() ? { fechaPasada: true } : null;
};

/** Correo con usuario, @ y dominio con punto (más estricto que Validators.email) */
export const correo: ValidatorFn = (control) => {
  const valor = control.value as string | null;
  return valor && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(valor.trim())
    ? { email: true }
    : null;
};

/** Número entero (sin decimales) */
export const entero: ValidatorFn = (control) => {
  const valor = control.value;
  if (valor === null || valor === '' || valor === undefined) {
    return null;
  }
  return Number.isInteger(Number(valor)) ? null : { entero: true };
};

/** Como @Digits(integer, fraction) */
export function digitos(enteros: number, decimales: number): ValidatorFn {
  const patron = new RegExp(`^-?\\d{1,${enteros}}(\\.\\d{1,${decimales}})?$`);
  return (control) => {
    const valor = control.value;
    if (valor === null || valor === '' || valor === undefined) {
      return null;
    }
    return patron.test(String(valor))
      ? null
      : { digitos: { enteros, decimales } };
  };
}

/**
 * Validador de grupo: la fecha fin debe ser posterior a la de inicio
 * (o igual, si `permitirIgual`: encuesta lo acepta; convenio y periodo no).
 * El error se marca también en el control fin para mostrarlo debajo de ese campo.
 */
export function rangoFechas(
  inicio: string,
  fin: string,
  permitirIgual = false,
): ValidatorFn {
  return (grupo: AbstractControl): ValidationErrors | null => {
    const ctrlInicio = grupo.get(inicio);
    const ctrlFin = grupo.get(fin);
    if (!ctrlInicio || !ctrlFin) {
      return null;
    }
    const a = ctrlInicio.value as string | null;
    const b = ctrlFin.value as string | null;
    const invalido = !!a && !!b && (permitirIgual ? b < a : b <= a);

    const otros = { ...(ctrlFin.errors ?? {}) };
    delete otros['rangoFechas'];
    delete otros['rangoFechasIgual'];
    if (invalido) {
      ctrlFin.setErrors({
        ...otros,
        [permitirIgual ? 'rangoFechasIgual' : 'rangoFechas']: true,
      });
    } else {
      ctrlFin.setErrors(Object.keys(otros).length > 0 ? otros : null);
    }
    return invalido ? { rangoFechas: true } : null;
  };
}

/** Primer mensaje de error en español para mostrar en <mat-error> */
export function mensajeCampo(control: AbstractControl | null): string {
  const e = control?.errors;
  if (!e) {
    return '';
  }
  if (e['required'] || e['vacio']) return 'Este campo es obligatorio.';
  if (e['maxlength'])
    return `Admite como máximo ${e['maxlength'].requiredLength} caracteres.`;
  if (e['minlength'])
    return `Debe tener al menos ${e['minlength'].requiredLength} caracteres.`;
  if (e['min']) {
    return e['min'].min === 0
      ? 'No puede ser negativo.'
      : e['min'].min === 1
        ? 'Debe ser mayor que 0.'
        : `Debe ser mayor o igual a ${e['min'].min}.`;
  }
  if (e['max']) return `Debe ser menor o igual a ${e['max'].max}.`;
  if (e['entero']) return 'Debe ser un número entero.';
  if (e['digitos']) {
    return `Admite hasta ${e['digitos'].enteros} dígitos enteros y ${e['digitos'].decimales} decimales.`;
  }
  if (e['email']) return 'Ingresa un correo válido.';
  if (e['fechaFutura']) return 'La fecha no puede ser futura.';
  if (e['fechaPasada']) return 'La fecha debe ser anterior a hoy.';
  if (e['fueraDelPeriodo'])
    return 'La fecha debe estar dentro del periodo académico elegido.';
  if (e['rangoFechas'])
    return 'La fecha de fin debe ser posterior a la fecha de inicio.';
  if (e['rangoFechasIgual'])
    return 'La fecha de fin no puede ser anterior a la fecha de inicio.';
  if (e['pattern']) return 'El formato no es válido.';
  return 'El valor no es válido.';
}
