import { Component, inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
@Component({
  selector: 'app-confirmar',
  imports: [MatDialogModule, MatButtonModule],
  template: `<h2 mat-dialog-title>Eliminar registro</h2>
    <mat-dialog-content
      ><p>¿Eliminar {{ data }}?</p>
      <p>Esta acción elimina el registro del sistema.</p></mat-dialog-content
    ><mat-dialog-actions align="end"
      ><button mat-button [mat-dialog-close]="false" cdkFocusInitial>
        Cancelar</button
      ><button mat-flat-button [mat-dialog-close]="true">
        Eliminar
      </button></mat-dialog-actions
    >`,
})
export class ConfirmarComponent {
  readonly data = inject(MAT_DIALOG_DATA);
}
