import { ChangeDetectorRef, Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { provideNativeDateAdapter } from '@angular/material/core';
import { MatInputModule } from '@angular/material/input';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { Scurso } from '../../../services/scurso';
import { ActivatedRoute, Router } from '@angular/router';
import { CursoDTOInsert } from '../../../models/CursoDTOInsert';

@Component({
  selector: 'app-qactualizarcomponent',
  providers: [provideNativeDateAdapter()],
  imports: [
    MatInputModule,
    MatSelectModule,
    MatDatepickerModule,
    MatButtonModule,
    ReactiveFormsModule,
    MatCheckboxModule,
  ],
  templateUrl: './qactualizarcomponent.html',
  styleUrl: './qactualizarcomponent.css',
})
export class Qactualizarcomponent {
  idCurso: any = null;
  formCurso!: FormGroup;
  mensaje: string = '';

  constructor(
    private cS: Scurso,
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.idCurso = Number(this.route.snapshot.paramMap.get('id'));

    console.log('ID recibido:', this.idCurso);

    if (!this.idCurso || isNaN(this.idCurso)) {
      this.mensaje = 'ID no válido';
      return;
    }

    this.formCurso = this.fb.group({
      codigo: [this.idCurso],
      nombre: ['', Validators.required],
      descripcion: ['', Validators.required],
      area: ['', Validators.required],
    });

    this.buscarId();
  }

  buscarId(): void {
    this.cS.listid(this.idCurso).subscribe({
      next: (data: CursoDTOInsert) => {
        console.log('Curso detectado:', data);

        this.formCurso.patchValue({
          codigo: data.id_curso,
          nombre: data.nombre_curso,
          descripcion: data.descripcion,
          area: data.area,
        });
      },

      error: (error) => {
        console.error('Error al buscar el curso', error);
        this.mensaje = 'Curso no encontrado';
      },
    });
  }

  actualizarCurso(): void {
    const dto = {
      id_curso: this.formCurso.value.codigo,
      nombre_curso: this.formCurso.value.nombre,
      descripcion: this.formCurso.value.descripcion,
      area: this.formCurso.value.area,
    };

    this.cS.updateCurso(dto).subscribe({
      next: () => {
        this.mensaje = 'Curso actualizado correctamente';
        this.cdr.detectChanges();
        this.formCurso.reset();
      },

      error: (error) => {
        console.error(error);
        this.mensaje = 'Error al actualizar!!';
      },
    });
  }
}
