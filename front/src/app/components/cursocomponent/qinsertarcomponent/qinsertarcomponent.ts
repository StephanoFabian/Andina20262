import { Component } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { provideNativeDateAdapter } from '@angular/material/core';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { Scurso } from '../../../services/scurso';
import { CursoDTOInsert } from '../../../models/CursoDTOInsert';

@Component({
  selector: 'app-qinsertarcomponent',
  providers: [provideNativeDateAdapter()],
  imports: [MatInputModule, MatButtonModule, ReactiveFormsModule],
  templateUrl: './qinsertarcomponent.html',
  styleUrl: './qinsertarcomponent.css',
})
export class Qinsertarcomponent {
  formCurso: any;
  mensaje: string = '';

  constructor(
    private cS: Scurso,
    private fb: FormBuilder,
  ) {
    this.formCurso = fb.group({
      nombre: ['', Validators.required],
      descripcion: ['', Validators.required],
      area: ['', Validators.required],
    });
  }

  insertarCurso() {
    if (this.formCurso.invalid) {
      this.mensaje = 'Por favor complete todos los campos correctamente.';
      return;
    }
    const str: CursoDTOInsert = {
      id_curso: 0,
      nombre_curso: this.formCurso.value.nombre,
      descripcion: this.formCurso.value.descripcion,
      area: this.formCurso.value.area,
    };
    this.cS.insertCurso(str).subscribe({
      next: (respuesta) => {
        this.mensaje = 'Curso registrado';
        this.formCurso.reset;
      },
      error: (error) => {
        this.mensaje = 'Error al insertar el curso: ' + error.message;
      },
    });
  }
}
