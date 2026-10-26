# Etched

A new form of entertainment: etch music from the internet onto discs, play it from boomboxes, jukeboxes, minecart jukeboxes and
radios, and collect your discs in album covers.

This is the 1.21.1 multiloader branch. It builds for **NeoForge** and **Fabric** from one codebase. The Fabric side is the one that
is played and tested. Development of the NeoForge side is stalled: it builds, its server starts and its data generation works, but it
has not been played in game since the move to two loaders.

| Module     | Contents                                                                            |
|------------|-------------------------------------------------------------------------------------|
| `common`   | Everything that does not depend on a mod loader, including the mixins and assets    |
| `neoforge` | NeoForge entrypoints, events, configs and the optional Sophisticated Core support   |
| `fabric`   | Fabric entrypoints, mixins that replace NeoForge events, data generation, and the optional Create support |

## What it does

The full list is in [docs/features.md](docs/features.md), with where each feature came from. In short:

- **Etching table, discs and labels.** Etch a track from a URL onto a blank disc, with a music label for the artist and title.
  Playlists and albums make a disc with several tracks. Discs can be dyed, copied and merged into **album covers** that show cover art.
- **Players and blocks.** The **boombox** plays a disc from your inventory or hand. The **jukebox minecart**, **album jukebox** and the
  vanilla jukebox play Etched discs. The **radio** plays an internet radio stream and stops while it is powered by redstone.
- **Radio screen.** Play, Stop and Close buttons (Stop silences the radio for everyone and keeps its station), a list of recent stations that can be given
  names, a volume slider, a search button, and a bounded reconnect when a connection drops.
- **Now Playing.** The screens of radios, jukeboxes, album jukeboxes and boomboxes show what is playing. A radio shows the title its
  station reports, or Internet Radio when there is none.
- **Volume.** Radios, album jukeboxes and, by sneaking and using with empty hands, vanilla jukeboxes have a volume setting. A lower
  volume also lowers how far away the music can be heard.
- **Music search.** Search Radio Browser and SoundCloud from the radio and the etching table.
- **Sources.** Direct links to OGG, WAV, MP3 and AAC, SoundCloud, Bandcamp, Google Drive and Dropbox share links, M3U and HLS streams, and
  radio playlists in PLS and M3U with the first working entry used as a fallback.
- **Create and Valkyrien Skies (Fabric only).** Radios, album jukeboxes and jukeboxes keep playing while mounted on a Create contraption, such as
  a train, and radios and album jukeboxes can be opened and changed while they move. Radios and jukeboxes on Valkyrien Skies ships, such as
  Eureka ships, are heard from where the ship is.
- **Compatibility.** Amendments' jukebox look is supported, and parrots dance to anything playing nearby.

## Using the radio

Open a radio and paste a stream address, or press the magnifier to search. The address can be a stream, or a `.pls` or `.m3u` playlist.
**Play** applies the station for everyone and starts it, **Stop** silences the radio for everyone until someone presses Play, and **Close** leaves
without changing anything. Give a station a name in the name box and it appears under that name in the recent stations list. Names
and the list are kept on your computer only.

## Create and ships

Create support is built into the Fabric jar and only used when Create Fabric is installed. It was written against Create Fabric 6.0.10.
A radio or jukebox on a contraption plays what it had when the contraption was assembled, and right-clicking a radio or album jukebox on
it opens its screen, so you can change the station, the discs or the volume while it moves. A vanilla jukebox on a contraption plays but
cannot be changed. Radios and jukeboxes on Valkyrien Skies ships, such as Eureka ships, are heard from where the ship is. The ship support
has not been played on a ship yet.

## Building

Use Java 21. The wrapper pins Gradle 8.14.

```
./gradlew build
```

The jars are written to `neoforge/build/libs` and `fabric/build/libs`. Every push also builds both jars in GitHub Actions and keeps
them as artifacts. The Fabric build downloads the Create Fabric release it compiles against from the `createfabric` releases.

Data generation can be run with either loader and writes to `common/src/generated/resources`:

```
./gradlew :neoforge:runData
./gradlew :fabric:runDatagen
```

The two loaders write the smelting result of the blank music disc recipe slightly differently, so regenerate with NeoForge to keep the
committed data unchanged.

## Network safety

Players decide which URLs the game and the server connect to, so every request, including each redirect, is checked before it is
made. Only HTTP(S) URLs without credentials are allowed, and addresses on this computer or private networks are refused. On the
client, the `Allow Private Network Stations` option in the client config lets you play from a station on your own network. Only turn it
on with servers you trust, since they decide which radio URL your game connects to. Requests never include your username or UUID.
Playlists, music service replies, pages and cover images are size limited.

## Credits and contributions from the community

Etched was created by Moonflower Studio: Ocelot, Jackson, Farcr (art) and AstraZoey (sound design). Several community projects
have since fixed and extended the mod, and this branch carries over their work. Their changes were ported into the shared code by
reading those repositories, and the new files that came from them carry a note naming the source. Where only an idea was taken, the
code was written again here. [docs/features.md](docs/features.md) lists the origin of every feature.

| Project                                                                                 | Author        | What this branch uses from it |
|-----------------------------------------------------------------------------------------|---------------|-------------------------------|
| [BazZziliuS/etched](https://github.com/BazZziliuS/etched)                               | BazZziliuS    | SoundCloud playback of tracks that are now only served as AAC over HLS. AAC decoding (ADTS and fragmented MP4) through `AacInputStream` and `FragmentedMp4Reader`. `AsyncAudioStream`, which decodes on a separate thread so a slow network never freezes the game, and `RestartingAudioStream` for looping. `AsyncInputStream` fixes. Segment prefetching for long HLS tracks, M3U version 7, and Google Drive and Dropbox share links. The client side track lookup for the etching table. The sound settings that the volume slider and the distance fade are based on. The music search screen with its Radio Browser and SoundCloud sources. The idea and the syncing approach of the Create contraption support. |
| [wisplite/etched](https://github.com/wisplite/etched)                                   | wisplite      | Fixes for live radios: a stalled stream must not look like the end of a track and rewind it, opening and closing a stream must stay off the render thread, and live streams must not be served from the HTTP cache. The no-cache request headers come from this project. |
| [oogabooga-dev/re-etched](https://github.com/oogabooga-dev/re-etched)                   | oogabooga-dev | The network policy: classifying addresses as public, private or forbidden, rejecting hosts that resolve to a mix of public and private addresses, and checking every redirect (`NetworkAddressClassifier` is adapted from its classifier). The `Allow Private Network Stations` option. The ideas of response size limits, the station history, PLS and M3U stations with fallbacks, stream titles, and the explicit Play, Stop and reconnect states of the radio. |
| [VoidAndEther/etched_refabricated](https://github.com/VoidAndEther/etched_refabricated) | VoidAndEther  | An earlier Fabric-only port of 1.21.1. No code was copied. It was used as a reference to cross-check how the Fabric side replaces NeoForge's grindstone, item entity and vehicle hooks. |

Re-Etched is a separate fork of Etched 3.0.4 for Forge 1.20.1 and cannot be installed alongside this mod, since both use the `etched` mod id.

Create support is written for [Create Fabric](https://github.com/Livinglive234/createfabric) and Create by the Create team, and is only
compiled against them.

## Libraries

| Library                                                                              | Used for               | License     |
|--------------------------------------------------------------------------------------|------------------------|-------------|
| [JLayer](https://github.com/umjammer/jlayer) (umjammer)                              | MP3 decoding           | LGPL-2.1    |
| [JAAD](https://github.com/Tianscar/jaadec) (`com.tianscar.javasound:jaad`, Tianscar) | AAC decoding           | Apache-2.0  |
| [Forge Config API Port](https://modrinth.com/mod/forge-config-api-port)              | Config files on Fabric | MPL-2.0     |

## License

See [LICENSE](LICENSE). The source code and the reserved game assets are covered by different terms there.
