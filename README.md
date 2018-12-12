# WMA Android Tools

[1 - WMAAudioView](#WMAAudioView)

## WMAAudioView
Fornece um componente de áudio completo e pronto para executar faixas de áudio em MP3

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

```
audio:elapsedTimeColor="#000000" -> Cor em hexadecimal
audio:totalTimeColor="#000000" -> Cor em hexadecimal
audio:backgroundContainer="@drawable/shape_audio" -> Container de fundo do componente
audio:audioTitle="Nome da Musica" -> Título da Faixa de áudio
audio:deleteTrack="true" -> Faz a lixeira aparecer, precisa implementar o método onDelete
audio:downloadTrack="true" -> Aparece o botão de baixar faixa, necessário informar destino
audio:backToBegin="true" -> Volta ao começo, quando o áudio termina
```
