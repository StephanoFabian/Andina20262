import {
  Component,
  ElementRef,
  OnDestroy,
  ViewChild,
  inject,
  signal,
} from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { firstValueFrom, Subscription } from 'rxjs';
import { MatButtonModule } from '@angular/material/button';
import {
  Room,
  RoomEvent,
  Track,
  RemoteTrack,
  RemoteTrackPublication,
  RemoteParticipant,
  LocalAudioTrack,
} from 'livekit-client';
import { VideollamadaService } from '../../../services/videollamada.service';
import {
  FragmentoTraduccion,
  SubtituloLlamada,
} from '../../../models/Videollamada';
import { mensajeError } from '../../../services/errores';

@Component({
  selector: 'app-videollamada',
  imports: [ReactiveFormsModule, RouterLink, MatButtonModule],
  templateUrl: './videollamadacomponent.html',
  styleUrl: './videollamadacomponent.css',
})
export class VideollamadaComponent implements OnDestroy {
  private readonly service = inject(VideollamadaService);
  private readonly route = inject(ActivatedRoute);
  @ViewChild('videos', { static: true }) videos!: ElementRef<HTMLDivElement>;
  @ViewChild('audios', { static: true }) audios!: ElementRef<HTMLDivElement>;
  readonly titulo = signal('Sesión virtual');
  readonly conectado = signal(false);
  readonly conectando = signal(false);
  readonly error = signal('');
  readonly aviso = signal('');
  readonly microfono = signal(true);
  readonly camara = signal(true);
  readonly traduciendo = signal(false);
  readonly disponible = signal(false);
  readonly subtitulos = signal<SubtituloLlamada[]>([]);
  readonly pendientes = signal(0);
  readonly latencia = signal(0);
  readonly idiomaOrigen = new FormControl<'es' | 'quy'>('es', {
    nonNullable: true,
  });
  readonly idiomaEscucha = new FormControl<'es' | 'quy'>('es', {
    nonNullable: true,
  });
  readonly escucharVoz = new FormControl(true, { nonNullable: true });
  readonly escucharOriginal = new FormControl(true, { nonNullable: true });
  readonly mostrarSubtitulos = new FormControl(true, { nonNullable: true });
  readonly sesion = Number(this.route.snapshot.paramMap.get('id'));
  private room?: Room;
  private context?: AudioContext;
  private salida?: MediaStreamAudioDestinationNode;
  private pistaVoz?: LocalAudioTrack;
  private pistaIdioma = '';
  private siguienteVoz = 0;
  private recorder?: MediaRecorder;
  private grabacionTimer?: ReturnType<typeof setTimeout>;
  private sondeoTimer?: ReturnType<typeof setInterval>;
  private sondeando = false;
  private cerrando = false;
  private guardado = '';
  private trabajos = new Map<string, number>();
  private recibidos = new Set<string>();
  private escuchas: Subscription[] = [];
  private medios = new Map<
    string,
    {
      pista: RemoteTrack;
      nodo: HTMLMediaElement;
      idioma?: string;
      tarjeta?: HTMLElement;
    }
  >();

  constructor() {
    this.escuchas = [
      this.idiomaEscucha.valueChanges.subscribe(() => this.actualizarAudio()),
      this.escucharVoz.valueChanges.subscribe(() => this.actualizarAudio()),
      this.escucharOriginal.valueChanges.subscribe(() =>
        this.actualizarAudio(),
      ),
    ];
  }

  async entrar(): Promise<void> {
    if (this.conectando() || this.conectado()) return;
    if (!window.isSecureContext) {
      this.error.set(
        'Para usar cámara y micrófono abre Andina mediante HTTPS o localhost.',
      );
      return;
    }
    this.conectando.set(true);
    this.error.set('');
    this.cerrando = false;
    try {
      this.context = new AudioContext();
      await this.context.resume();
      const acceso = await firstValueFrom(this.service.acceso(this.sesion));
      if (this.cerrando) return;
      this.titulo.set(acceso.titulo);
      this.disponible.set(acceso.traduccionDisponible);
      this.guardado = `andina.voz.${acceso.sala}.${acceso.identidad}`;
      const room = new Room({ adaptiveStream: true, dynacast: true });
      this.room = room;
      room.on(RoomEvent.TrackSubscribed, (track, publication, participant) =>
        this.adjuntar(track, publication, participant),
      );
      room.on(RoomEvent.TrackUnsubscribed, (_track, publication) =>
        this.quitar(publication.trackSid),
      );
      room.on(RoomEvent.DataReceived, (data, participant, _kind, topic) => {
        if (topic === 'andina.subtitulos' && participant)
          this.recibir(data, participant);
      });
      room.on(RoomEvent.Disconnected, () => {
        if (!this.cerrando) void this.salir();
      });
      await room.connect(acceso.url, acceso.token);
      if (this.cerrando) {
        await room.disconnect();
        return;
      }
      await room.startAudio();
      this.conectado.set(true);
      try {
        await room.localParticipant.setMicrophoneEnabled(true, {
          echoCancellation: true,
          noiseSuppression: true,
        });
      } catch {
        this.microfono.set(false);
        this.aviso.set('No se pudo activar el micrófono. Revisa sus permisos.');
      }
      try {
        const video = await room.localParticipant.setCameraEnabled(true);
        if (video?.videoTrack) {
          const nodo = video.videoTrack.attach();
          nodo.muted = true;
          nodo.dataset['local'] = 'true';
          nodo.style.cssText =
            'width:100%;max-height:360px;border-radius:12px;transform:scaleX(-1)';
          this.videos.nativeElement.appendChild(nodo);
        }
      } catch {
        this.camara.set(false);
        this.aviso.set('Entraste sin cámara. Puedes continuar por audio.');
      }
      this.recuperarPendientes();
      this.sondeoTimer = setInterval(() => void this.sondear(), 1500);
    } catch (ex) {
      this.error.set(
        ex instanceof HttpErrorResponse
          ? mensajeError(ex)
          : 'No se pudo entrar a la llamada. Comprueba el servidor de videollamadas y los permisos.',
      );
      await this.salir();
    } finally {
      this.conectando.set(false);
    }
  }

  private adjuntar(
    track: RemoteTrack,
    publication: RemoteTrackPublication,
    participant: RemoteParticipant,
  ): void {
    const nodo = track.attach();
    let tarjeta: HTMLElement | undefined;
    const idioma = publication.trackName.startsWith('andina-voz-')
      ? publication.trackName.slice(11)
      : undefined;
    if (track.kind === Track.Kind.Video) {
      tarjeta = document.createElement('div');
      const nombre = document.createElement('p');
      nombre.style.color = '#d2e2ed';
      nombre.textContent = `Participante ${participant.identity.slice(-4)}`;
      nodo.style.cssText = 'width:100%;max-height:360px;border-radius:12px';
      tarjeta.append(nodo, nombre);
      this.videos.nativeElement.appendChild(tarjeta);
    } else {
      this.audios.nativeElement.appendChild(nodo);
    }
    this.medios.set(publication.trackSid, {
      pista: track,
      nodo,
      idioma,
      tarjeta,
    });
    this.actualizarAudio();
  }

  private quitar(sid: string): void {
    const medio = this.medios.get(sid);
    if (medio) {
      medio.pista.detach(medio.nodo);
      medio.nodo.remove();
      medio.tarjeta?.remove();
      this.medios.delete(sid);
    }
  }

  private actualizarAudio(): void {
    for (const { pista, nodo, idioma } of this.medios.values()) {
      if (pista.kind !== Track.Kind.Audio) continue;
      nodo.muted = idioma
        ? !this.escucharVoz.value || idioma !== this.idiomaEscucha.value
        : !this.escucharOriginal.value;
      if (!nodo.muted)
        void nodo
          .play()
          .catch(() =>
            this.aviso.set('Pulsa «Activar sonido» para escuchar la llamada.'),
          );
    }
  }

  async activarSonido(): Promise<void> {
    try {
      await this.context?.resume();
      await this.room?.startAudio();
      this.actualizarAudio();
    } catch {
      this.aviso.set('El navegador no permitió reproducir el audio.');
    }
  }

  async cambiarMicrofono(): Promise<void> {
    try {
      const activo = !this.microfono();
      await this.room?.localParticipant.setMicrophoneEnabled(activo);
      this.microfono.set(activo);
      if (!activo) this.detenerTraduccion();
    } catch {
      this.error.set('No se pudo cambiar el micrófono.');
    }
  }

  async cambiarCamara(): Promise<void> {
    try {
      const activo = !this.camara();
      const pub = await this.room?.localParticipant.setCameraEnabled(activo);
      this.camara.set(activo);
      if (
        activo &&
        pub?.videoTrack &&
        !this.videos.nativeElement.querySelector('video[data-local]')
      ) {
        // La vista local original se retira al alternar para evitar videos duplicados.
        const nodo = pub.videoTrack.attach();
        nodo.muted = true;
        nodo.dataset['local'] = 'true';
        nodo.style.cssText = 'width:100%;max-height:360px;border-radius:12px';
        this.videos.nativeElement.appendChild(nodo);
      }
      if (!activo)
        this.videos.nativeElement.querySelector('video[data-local]')?.remove();
    } catch {
      this.error.set('No se pudo cambiar la cámara.');
    }
  }

  async alternarTraduccion(): Promise<void> {
    if (this.traduciendo()) {
      this.detenerTraduccion();
      return;
    }
    if (!this.microfono() || !this.room || !this.context || !this.disponible())
      return;
    if (typeof MediaRecorder === 'undefined') {
      this.error.set('Este navegador no permite grabar fragmentos de voz.');
      return;
    }
    try {
      const destino = this.idiomaOrigen.value === 'es' ? 'quy' : 'es';
      if (this.pistaVoz)
        await this.room.localParticipant.unpublishTrack(this.pistaVoz);
      this.salida?.disconnect();
      this.salida = this.context.createMediaStreamDestination();
      this.pistaIdioma = destino;
      this.pistaVoz = new LocalAudioTrack(
        this.salida.stream.getAudioTracks()[0],
        undefined,
        true,
      );
      await this.room.localParticipant.publishTrack(this.pistaVoz, {
        name: `andina-voz-${destino}`,
        source: Track.Source.Unknown,
      });
      this.idiomaOrigen.disable();
      this.traduciendo.set(true);
      this.error.set('');
      this.aviso.set(
        'Compartiendo traducción de tu voz por fragmentos. Usa audífonos para evitar eco.',
      );
      this.grabar();
    } catch {
      this.error.set('No se pudo activar la traducción de voz.');
      this.detenerTraduccion();
    }
  }

  private grabar(): void {
    if (!this.traduciendo() || !this.room || this.cerrando) return;
    if (this.trabajos.size >= 2) {
      this.aviso.set(
        'La traducción está alcanzando las frases anteriores. Haz una pausa; la llamada continúa.',
      );
      this.grabacionTimer = setTimeout(() => this.grabar(), 1500);
      return;
    }
    const pista = this.room.localParticipant.getTrackPublication(
      Track.Source.Microphone,
    )?.audioTrack;
    if (!pista) {
      this.detenerTraduccion();
      return;
    }
    const formatos = ['audio/webm;codecs=opus', 'audio/mp4', 'audio/webm'];
    const formato = formatos.find((tipo) =>
      MediaRecorder.isTypeSupported(tipo),
    );
    try {
      const recorder = new MediaRecorder(
        new MediaStream([pista.mediaStreamTrack]),
        {
          ...(formato ? { mimeType: formato } : {}),
          audioBitsPerSecond: 32000,
        },
      );
      this.recorder = recorder;
      const partes: BlobPart[] = [];
      recorder.ondataavailable = (e) => {
        if (e.data.size) partes.push(e.data);
      };
      recorder.onstop = () => {
        if (!this.traduciendo() || this.cerrando) return;
        const audio = new Blob(partes, { type: recorder.mimeType });
        if (audio.size > 100 && audio.size <= 1000000) void this.enviar(audio);
        this.grabar();
      };
      recorder.onerror = () => {
        this.error.set('Falló la captura de voz.');
        this.detenerTraduccion();
      };
      recorder.start();
      this.grabacionTimer = setTimeout(() => {
        if (recorder.state === 'recording') recorder.stop();
      }, 6000);
    } catch {
      this.error.set('No se pudo capturar el micrófono.');
      this.detenerTraduccion();
    }
  }

  private async enviar(audio: Blob): Promise<void> {
    const id = crypto.randomUUID();
    this.trabajos.set(id, Date.now());
    this.guardarPendientes();
    try {
      const encoded = await new Promise<string>((resolve, reject) => {
        const reader = new FileReader();
        reader.onload = () => resolve(String(reader.result).split(',')[1]);
        reader.onerror = reject;
        reader.readAsDataURL(audio);
      });
      await firstValueFrom(
        this.service.enviar(this.sesion, {
          id,
          idiomaOrigen: this.idiomaOrigen.value,
          audioBase64: encoded,
        }),
      );
    } catch (ex) {
      this.trabajos.delete(id);
      this.guardarPendientes();
      this.error.set(mensajeError(ex));
      // No se sigue capturando indefinidamente si la API no puede aceptar voz.
      this.detenerTraduccion();
    }
  }

  private async sondear(): Promise<void> {
    if (this.sondeando || this.cerrando || !this.conectado()) return;
    this.sondeando = true;
    try {
      for (const [id, creado] of [...this.trabajos]) {
        if (Date.now() - creado > 180000) {
          this.trabajos.delete(id);
          this.aviso.set(
            'Un fragmento tardó demasiado y dejó de reproducirse en esta llamada.',
          );
          continue;
        }
        try {
          const result = await firstValueFrom(
            this.service.resultado(this.sesion, id),
          );
          if (this.cerrando) return;
          if (result.estado === 'listo' || result.estado === 'error') {
            if (result.estado === 'error')
              this.error.set(
                result.mensaje || 'No se pudo interpretar una frase.',
              );
            else await this.compartir(result, creado);
            this.trabajos.delete(id);
          }
        } catch (ex) {
          if (
            ex instanceof HttpErrorResponse &&
            [401, 403, 404].includes(ex.status)
          )
            this.trabajos.delete(id);
          this.error.set(mensajeError(ex));
        }
      }
    } finally {
      this.guardarPendientes();
      this.sondeando = false;
    }
  }

  private async compartir(
    result: FragmentoTraduccion,
    creado: number,
  ): Promise<void> {
    if (!result.traduccion || !this.room) return;
    const subtitulo: SubtituloLlamada = {
      id: result.id,
      participante: 'Tú',
      idiomaOrigen: result.idiomaOrigen,
      idiomaDestino: result.idiomaDestino!,
      transcripcion: result.transcripcion || '',
      traduccion: result.traduccion,
      creado,
    };
    this.agregar(subtitulo);
    this.latencia.set(result.milisegundos || 0);
    await this.room.localParticipant.publishData(
      new TextEncoder().encode(JSON.stringify(subtitulo)),
      { reliable: true, topic: 'andina.subtitulos' },
    );
    if (result.mensaje) this.aviso.set(result.mensaje);
    // Nunca se reproduce la voz sintetizada en el altavoz del emisor: se publica
    // como una pista distinta y cada receptor decide si quiere escucharla.
    if (
      result.audioBase64 &&
      result.idiomaDestino === this.pistaIdioma &&
      this.traduciendo() &&
      this.context &&
      this.salida
    ) {
      const bytes = Uint8Array.from(atob(result.audioBase64), (c) =>
        c.charCodeAt(0),
      );
      const buffer = await this.context.decodeAudioData(bytes.buffer);
      if (this.cerrando || !this.salida || !this.traduciendo()) return;
      const cuando = Math.max(
        this.context.currentTime + 0.1,
        this.siguienteVoz,
      );
      if (cuando - this.context.currentTime > 24) {
        this.aviso.set(
          'Hay demasiada voz pendiente; se conserva el subtítulo de esta frase.',
        );
        return;
      }
      const voz = this.context.createBufferSource();
      voz.buffer = buffer;
      voz.connect(this.salida);
      voz.onended = () => voz.disconnect();
      voz.start(cuando);
      this.siguienteVoz = cuando + buffer.duration;
    }
  }

  private recibir(data: Uint8Array, participant: RemoteParticipant): void {
    if (data.byteLength > 8000) return;
    try {
      const s = JSON.parse(new TextDecoder().decode(data)) as SubtituloLlamada;
      if (
        typeof s.id !== 'string' ||
        s.id.length > 64 ||
        typeof s.transcripcion !== 'string' ||
        typeof s.traduccion !== 'string' ||
        s.transcripcion.length > 600 ||
        s.traduccion.length > 900 ||
        !['es', 'quy'].includes(s.idiomaOrigen) ||
        !['es', 'quy'].includes(s.idiomaDestino) ||
        !Number.isFinite(s.creado) ||
        Math.abs(Date.now() - s.creado) > 300000
      )
        return;
      this.agregar({
        ...s,
        participante: `Participante ${participant.identity.slice(-4)}`,
      });
    } catch {
      /* Un paquete ajeno a los subtítulos no modifica la vista. */
    }
  }

  private agregar(s: SubtituloLlamada): void {
    if (this.recibidos.has(s.id)) return;
    this.recibidos.add(s.id);
    if (this.recibidos.size > 200)
      this.recibidos.delete(this.recibidos.values().next().value!);
    this.subtitulos.update((items) =>
      [...items, s].sort((a, b) => a.creado - b.creado).slice(-20),
    );
  }

  private guardarPendientes(): void {
    this.pendientes.set(this.trabajos.size);
    if (this.guardado)
      try {
        sessionStorage.setItem(
          this.guardado,
          JSON.stringify([...this.trabajos]),
        );
      } catch {
        /* Sigue funcionando sin almacenamiento. */
      }
  }

  private recuperarPendientes(): void {
    try {
      const value: unknown = JSON.parse(
        sessionStorage.getItem(this.guardado) || '[]',
      );
      if (Array.isArray(value))
        for (const item of value.slice(0, 2)) {
          if (
            Array.isArray(item) &&
            typeof item[0] === 'string' &&
            /^[a-f0-9-]{36}$/.test(item[0]) &&
            typeof item[1] === 'number' &&
            Date.now() - item[1] < 180000
          )
            this.trabajos.set(item[0], item[1]);
        }
    } catch {
      /* No hay fragmentos recuperables. */
    }
    this.guardarPendientes();
  }

  private detenerTraduccion(): void {
    this.traduciendo.set(false);
    this.idiomaOrigen.enable();
    void this.pistaVoz?.mute().catch(() => undefined);
    if (this.grabacionTimer) clearTimeout(this.grabacionTimer);
    if (this.recorder?.state === 'recording') this.recorder.stop();
    this.recorder = undefined;
  }

  async salir(): Promise<void> {
    this.cerrando = true;
    this.detenerTraduccion();
    if (this.sondeoTimer) clearInterval(this.sondeoTimer);
    const room = this.room;
    this.room = undefined;
    await room?.disconnect();
    this.pistaVoz?.stop();
    this.pistaVoz = undefined;
    this.salida?.disconnect();
    this.salida = undefined;
    await this.context?.close().catch(() => undefined);
    this.context = undefined;
    for (const sid of [...this.medios.keys()]) this.quitar(sid);
    this.videos.nativeElement.replaceChildren();
    this.audios.nativeElement.replaceChildren();
    this.conectado.set(false);
    this.conectando.set(false);
    this.siguienteVoz = 0;
  }

  ngOnDestroy(): void {
    this.escuchas.forEach((s) => s.unsubscribe());
    void this.salir();
  }
}
