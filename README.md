# Etched

A new form of entertainment: etch music from the internet onto discs, play it from boomboxes, jukeboxes, minecart jukeboxes and
radios, and collect your discs in album covers.

This is the 1.21.1 multiloader branch. It builds for **NeoForge** and **Fabric** from one codebase:

| Module      | Contents                                                                          |
|-------------|-----------------------------------------------------------------------------------|
| `common`    | Everything that does not depend on a mod loader, including the mixins and assets  |
| `neoforge`  | NeoForge entrypoints, events, configs and the optional Sophisticated Core support |
| `fabric`    | Fabric entrypoints, mixins that replace NeoForge events, and Fabric data generation |

Sophisticated Core support exists on NeoForge only, since Sophisticated Core has no Fabric version.

## Building

Use Java 21. The wrapper pins Gradle 8.

```
./gradlew build
```

The jars are written to `neoforge/build/libs` and `fabric/build/libs`. Data generation can be run with either loader and writes to
`common/src/generated/resources`:

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
on with servers you trust, since they decide which radio URL your game connects to.

## Credits and contributions from the community

Etched was created by Moonflower Studio: Ocelot, Jackson, Farcr (art) and AstraZoey (sound design). Several community projects
have since fixed and extended the mod, and this branch carries over their work. The fixes below were ported into the shared code by
reading the changes in those repositories, and the new files that came from them carry a note naming the source.

| Project                                                                                   | Author          | What this branch uses from it                                                                                                                                                                                                                                                                                                                                                                                                                                  |
|-------------------------------------------------------------------------------------------|-----------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| [BazZziliuS/etched](https://github.com/BazZziliuS/etched)                                 | BazZziliuS      | SoundCloud playback fix for tracks that are now only served as AAC over HLS. AAC decoding (ADTS and fragmented MP4) through `AacInputStream` and `FragmentedMp4Reader`. `AsyncAudioStream`, which decodes on a separate thread so a slow network never freezes the game, and `RestartingAudioStream` for looping. `AsyncInputStream` fixes (timeouts, a read only ends at the true end of the stream, closing no longer blocks the sound thread). Segment prefetching for long HLS tracks, M3U version 7 support, and converting Google Drive and Dropbox share links to direct download links. |
| [wisplite/etched](https://github.com/wisplite/etched)                                     | wisplite        | Fixes for live radios: a stalled stream must not look like the end of a track and rewind it, opening and closing a stream must stay off the render thread, and live streams must not be served from the HTTP cache. The no-cache request headers come from this project. The same problems are also solved in BazZziliuS's `AsyncAudioStream`, which this branch uses for buffering.                                                                           |
| [oogabooga-dev/re-etched](https://github.com/oogabooga-dev/re-etched)                     | oogabooga-dev   | The network policy: classifying addresses as public, private or forbidden, rejecting hosts that resolve to a mix of public and private addresses, and checking every redirect. `NetworkAddressClassifier` is adapted from its address classifier.                                                                                                                                                                                                                |
| [VoidAndEther/etched_refabricated](https://github.com/VoidAndEther/etched_refabricated)   | VoidAndEther    | An earlier Fabric-only port of 1.21.1. No code was copied. It was used as a reference to cross-check how the Fabric side replaces NeoForge's grindstone, item entity and vehicle hooks.                                                                                                                                                                                                                                                                             |

Re-Etched is a separate fork of Etched 3.0.4 for Forge 1.20.1 and cannot be installed alongside this mod, since both use the `etched` mod id.

## Libraries

| Library                                                                               | Used for                                      | License    |
|---------------------------------------------------------------------------------------|-----------------------------------------------|------------|
| [JLayer](https://github.com/umjammer/jlayer) (umjammer)                               | MP3 decoding                                  | see project |
| [JAAD](https://github.com/Tianscar/jaadec) (`com.tianscar.javasound:jaad`, Tianscar)  | AAC decoding                                  | Apache-2.0 |
| [Forge Config API Port](https://modrinth.com/mod/forge-config-api-port)               | Config files on Fabric                        | see project |

## License

See [LICENSE](LICENSE). The source code and the reserved game assets are covered by different terms there.
