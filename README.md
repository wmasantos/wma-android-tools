# WMA Android Tools

### Latest Version
```java
implementation 'com.github.wmasantos:wma-android-tools:1.1.0'
```

[1 - WMAAudioView](#WMAAudioView) - (Disponível a partir da versão: 1.0.0)</br>[2 - WMAVideoView](#WMAVideoView) - (Disponível a partir da versão: 1.1.0)

## WMAAudioView
Fornece um componente de áudio completo e pronto para executar faixas de áudio em MP3

<img src="https://github.com/wmasantos/wma-android-tools/blob/master/WMAAudioView.jpg"/>

### Permissões
Antes de usá-la, precisa liberar as permissões.
```xml
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
<uses-permission android:name="android.permission.INTERNET"/>
```

### XML
```xml
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:audio="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:gravity="center">

    <br.com.wma.tools.widget.WMAAudioView
        android:id="@+id/avAudioView"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginLeft="20dp"
        android:layout_marginRight="20dp"
        audio:progress="0"
        audio:elapsedTimeColor="#000000"
        audio:totalTimeColor="#000000"
        audio:backgroundContainer="@drawable/shape_audio"
        audio:audioTitle="Nome da Musica"
        audio:deleteTrack="true"
        audio:downloadTrack="true"
        audio:backToBegin="true"/>

</LinearLayout>
```

* audio:elapsedTimeColor => Cor em hexadecimal
* audio:totalTimeColor => Cor em hexadecimal
* audio:backgroundContainer => Container de fundo do componente, pode ser um shape
* audio:audioTitle => Título da Faixa de áudio
* audio:deleteTrack => true ou false, faz a lixeira aparecer, precisa implementar o método onDelete
* audio:downloadTrack => true ou false, aparece o botão de baixar faixa de áudio, necessário informar pasta de destino para o download
* audio:backToBegin => true ou false, volta ao começo, quando o áudio termina.

### Java
```java
WMAAudioView wmaAudioView;
wmaAudioView = findViewById(R.id.avAudioView);
wmaAudioView.readyToPlayForStreamAsync(
        Activity,
        "URL STREM DA FAIXA DE AUDIO",
        new File("CAMINHO PARA SALVAR FICHEIRO"),
        new OnPlayerEventListener() {
            @Override
            public void onAudioReady(int duration, String totalTime) {
                System.out.println("Traz a duração total e a duração formatada");
            }

            @Override
            public void onPlayingComplete() {
                System.out.println("Invocado quando o player termina de tocar");
            }

            @Override
            public void onAudioReadyError(Exception e) {
                System.out.println("Lança a exceção quando algo errado ocorrer");
            }
        }
);
wmaAudioView.setAudioTitle("Som Foda!");
// Chamada necessária para poder capturar o evento de click no icone de remoção.
wmaAudioView.addDeleteEvent(new OnDeleteEventListener() {
    @Override
    public void onDelete() {
        System.out.println("Captura o clique na lixeira!");
    }
});
```

## WMAVideoView
Fornece um componente de vídeo completo e pronto para executar trilhas em MP4 ou outras extenções mais conhecidas na web.

<img src="https://github.com/wmasantos/wma-android-tools/blob/master/pre_loading.png"/> <img src="https://github.com/wmasantos/wma-android-tools/blob/master/video_controller.png"/> <img src="https://github.com/wmasantos/wma-android-tools/blob/master/video.png"/>

### Permissões
Antes de usá-la, precisa liberar a permissão.
```xml
<uses-permission android:name="android.permission.INTERNET"/>
```

### XML
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="vertical"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <br.com.wma.tools.widget.WMAVideoView
        android:id="@+id/videoView"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        video:startOnLoad="true"
        video:restartVideo="true"/>

</LinearLayout>
```

* video:startOnLoad="true" => Inicia o player automaticamente
* video:restartVideo="true" => Volta o vídeo para o começo quando terminar

### Java
```java
WMAVideoView wmaVideoView;
wmaVideoView = findViewById(R.id.videoView);
wmaVideoView.setTitle("Boku no Hero Academia");
wmaVideoView.loadVídeoStream(this, "https://s3-us-west-2.amazonaws.com/smn-mobile/fanflix/anime/boku-no-hero-s2-ep1.mp4", new OnVideoEvents() {
            @Override
            public void onPrepared() {
                System.out.println("PREPAROU");
            }

            @Override
            public void onPlaying(int currentTime, String formattedTime) {
                System.out.println("TOCANDO: " + formattedTime);
            }

            @Override
            public void onPlayingComplete() {
                System.out.println("COMPLETOU");
            }
        },
        new OnBackEventListener() {
            @Override
            public void backPressed() {
                //Fecha activity e volta
            }
        }
);
```
