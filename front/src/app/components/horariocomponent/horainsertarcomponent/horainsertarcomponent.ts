import { Component } from '@angular/core';
import { provideNativeDateAdapter } from '@angular/material/core';
import { Shorario } from '../../../services/shorario';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { HorarioInsertDTO } from '../../../models/HorarioInsertDTO';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-horainsertarcomponent',
  providers: [provideNativeDateAdapter()],
  imports: [MatInputModule, MatButtonModule, ReactiveFormsModule],
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
      horaInicio: this.formHorario.value.inicio,
      horaFin: this.formHorario.value.fin,
      idAsignacion: this.formHorario.value.asignacion,
    };
    this.sH.insertHorario(str).subscribe({
      next: (respuesta) => {
        this.mensaje = 'Horario registrado';
        this.formHorario.reset;
      },
      error: (error) => {
        this.mensaje = 'Error al insertar el horario: ' + error.message;
      },
    });
  }
}
