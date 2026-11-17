import { Component, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import {
  MAT_DIALOG_DATA,
  MatDialog,
  MatDialogModule,
} from '@angular/material/dialog';
import { Observable } from 'rxjs';

export interface ConfirmarDialogoData {
  titulo: string;
  mensaje: string;
  confirmar?: string;
}

/** Diálogo "¿Seguro que deseas…?" que devuelve true al confirmar */
@Component({
  selector: 'app-confirmar-dialogo',
  imports: [MatDialogModule, MatButtonModule],
  template: `
    <h2 mat-dialog-title>{{ data.titulo }}</h2>
    <mat-dialog-content>
      <p>{{ data.mensaje }}</p>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button
        mat-button
        type="button"
        [mat-dialog-close]="false"
        cdkFocusInitial
      >
        Cancelar
      </button>
      <button mat-flat-button type="button" [mat-dialog-close]="true">
        {{ data.confirmar ?? 'Eliminar' }}
      </button>
    </mat-dialog-actions>
  `,
})
export class ConfirmarDialogo {
  protected readonly data = inject<ConfirmarDialogoData>(MAT_DIALOG_DATA);
}

/** Abre el diálogo y emite true solo si el usuario confirma */
export function confirmar(
  dialog: MatDialog,
  data: ConfirmarDialogoData,
): Observable<boolean | undefined> {
  return dialog
    .open<ConfirmarDialogo, ConfirmarDialogoData, boolean>(ConfirmarDialogo, {
      data,
      width: '420px',
      maxWidth: 'calc(100vw - 32px)',
      role: 'alertdialog',
    })
    .afterClosed();
}
