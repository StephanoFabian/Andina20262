import { Component } from '@angular/core';
import { Menucomponent } from '../menucomponent/menucomponent';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-cursocomponent',
  imports: [Menucomponent,RouterOutlet],
  templateUrl: './cursocomponent.html',
  styleUrl: './cursocomponent.css',
})
export class Cursocomponent {}
