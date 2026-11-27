import { Component } from '@angular/core';
import { provideNativeDateAdapter } from '@angular/material/core';
import { Shorario } from '../../../services/shorario';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { HorarioInsertDTO } from '../../../models/HorarioInsertDTO';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import {MatTimepickerModule} from '@angular/material/timepicker';

@Component({
  selector: 'app-horainsertarcomponent',
  providers: [provideNativeDateAdapter()],
  imports: [MatInputModule, MatButtonModule, ReactiveFormsModule, MatTimepickerModule],
  templateUrl: './horainsertarcomponent.html',
  styleUrl: './horainsertarcomponent.css',
})
export class Horainsertarcomponent {
  formHorario: any;
  mensaje: string = '';

  constructor(
    private sH: Shorario,
    private fb: FormBuilder,
  ) {
    this.formHorario = fb.group({
      dia: ['', Validators.required],
      inicio: ['', Validators.required],
      fin: ['', Validators.required],
      asignacion: ['', Validators.required],
    });
  }

  insertarHorario() {
    if (this.formHorario.invalid) {
      this.mensaje = 'Por favor complete todos los campos correctamente.';
      return;
    }

    const str: HorarioInsertDTO = {
      id_horario: 0,
      diaSemana: this.formHorario.value.dia,
      horaInicio: this.formatearHora24h(this.formHorario.value.inicio),
      horaFin: this.formatearHora24h(this.formHorario.value.fin),
      idAsignacion: this.formHorario.value.asignacion,
    };
    this.sH.insertHorario(str).subscribe({
      next: (respuesta) => {
        this.mensaje = 'Horario registrado';
        this.formHorario.reset();
      },
      error: (error) => {
        this.mensaje = 'Error al insertar el horario: ' + error.message;
      },
    });
  }
private formatearHora24h(valor: unknown): string {
    if (!valor) {
      return '';
    }

    if (valor instanceof Date && !isNaN(valor.getTime())) {
      const horas = String(valor.getHours()).padStart(2, '0');
      const minutos = String(valor.getMinutes()).padStart(2, '0');
      return `${horas}:${minutos}`;
    }

    const texto = String(valor).trim();

    // Si ya viene en formato 24h ("H:mm", "HH:mm" o "HH:mm:ss")
    const match24h = texto.match(/^(\d{1,2}):(\d{2})(?::\d{2})?$/);
    if (match24h) {
      return `${match24h[1].padStart(2, '0')}:${match24h[2]}`;
    }

    // Si viene en formato 12h ("12:30 AM", "4:00 PM", etc.)
    const match12h = texto.match(/^(\d{1,2}):(\d{2})\s*(AM|PM)$/i);
    if (match12h) {
      let horas = Number(match12h[1]);
      const minutos = match12h[2];
      const periodo = match12h[3].toUpperCase();

      if (periodo === 'PM' && horas < 12) {
        horas += 12;
      } else if (periodo === 'AM' && horas === 12) {
        horas = 0;
      }

      return `${String(horas).padStart(2, '0')}:${minutos}`;
    }

    return texto;
  }
}
