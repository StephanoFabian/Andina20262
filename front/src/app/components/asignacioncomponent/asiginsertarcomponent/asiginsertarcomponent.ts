import { Component } from '@angular/core';
import { provideNativeDateAdapter } from '@angular/material/core';
import { Sasignacion } from '../../../services/sasignacion';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { AsignacionDTOInsert } from '../../../models/AsignacionDTOInsert';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-asiginsertarcomponent',
  providers: [provideNativeDateAdapter()],
  imports: [MatInputModule, MatButtonModule, ReactiveFormsModule],
  templateUrl: './asiginsertarcomponent.html',
  styleUrl: './asiginsertarcomponent.css',
})
export class Asiginsertarcomponent {
  formAsignacion: any;
  mensaje: string = '';

  constructor(
    private aS: Sasignacion,
    private fb: FormBuilder,
  ) {
    this.formAsignacion = fb.group({
      aula: ['', Validators.required],
      curso: ['', Validators.required],
      periodo: ['', Validators.required],
      persona: ['', Validators.required],
      modalidad: ['', Validators.required],
      horas: ['', Validators.required],
      colegio: ['', Validators.required],
    });
  }

  insertarasignacion() {
    if (this.formAsignacion.invalid) {
      this.mensaje = 'Por favor complete todos los campos correctos';
      return;
    }
    const str: AsignacionDTOInsert = {
      id_asignacion: 0,
      id_aula: this.formAsignacion.value.aula,
      id_curso: this.formAsignacion.value.curso,
      id_periodo: this.formAsignacion.value.periodo,
      id_persona: this.formAsignacion.value.persona,
      modalidad: this.formAsignacion.value.modalidad,
      horassemanales: this.formAsignacion.value.horas,
      id_colegio: this.formAsignacion.value.colegio,
    }
    this.aS.insertAsignacion(str).subscribe({
      next:(respuesta) =>{
        this.mensaje = 'Asignacion registrada'
        this.formAsignacion.reset
      },error: (error) => {
        this.mensaje = 'Error al insertar el curso: ' + error.message
      }
    })
  }
}