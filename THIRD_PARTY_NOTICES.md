# Third-party notices

This branch builds on Etched and on work from other projects. This file lists where each piece came from and under which terms it is used.
Which feature came from where is listed in [docs/features.md](docs/features.md).

## Etched

Etched was created by Moonflower Studio: Ocelot and Jackson (development), Farcr (art) and AstraZoey (sound design).

- Original project: https://github.com/jacksonhardaway/etched
- Fork base of this branch: `9cecd79` on the `1.21.x` branch.

The [LICENSE](LICENSE) file of the original project is kept as it is. It reserves the files in the resource directories
(`/common/src/main/resources/`, `/fabric/src/main/resources/` and `/forge/src/main/resources/`) as "All Rights Reserved, Copyright (c) 2021
Moonflower Studio", and licenses everything else under the GNU General Public License version 3. This branch does not claim ownership of the
reserved resources, and nothing here grants additional rights to them. No models, textures, sounds or translations were copied from the forks below.

## Forks of Etched

Code from the following forks was ported into this branch, and the files that came from them carry a note naming the source. Each of these
repositories carries the same `LICENSE` file as the original project, so their code is under the GNU General Public License version 3, the
same as the code of this branch. Copies of those license files are in [licenses/](licenses/).

| Project | Author | License file | What was used |
|---|---|---|---|
| [BazZziliuS/etched](https://github.com/BazZziliuS/etched) | BazZziliuS | [licenses/BazZziliuS-etched-LICENSE](licenses/BazZziliuS-etched-LICENSE) | Code: AAC decoding, the audio buffering and looping classes, segment prefetching, Google Drive and Dropbox links, the client side track lookup, the sound settings behind the volume slider, the music search screen and its sources. Idea and approach: Create contraption support. |
| [wisplite/etched](https://github.com/wisplite/etched) | wisplite | [licenses/wisplite-etched-LICENSE](licenses/wisplite-etched-LICENSE) | Fixes for live radios, and the no-cache request headers. |
| [oogabooga-dev/re-etched](https://github.com/oogabooga-dev/re-etched) | oogabooga-dev | [licenses/oogabooga-dev-re-etched-LICENSE](licenses/oogabooga-dev-re-etched-LICENSE) | Code: the address classifier of the network policy, which is adapted. Ideas written again here: size limits, station history, PLS and M3U stations, stream titles, and the radio play, stop and reconnect states. Re-Etched is a separate fork for Forge 1.20.1 and cannot be installed alongside this mod. |

[VoidAndEther/etched_refabricated](https://github.com/VoidAndEther/etched_refabricated) is an earlier Fabric port of 1.21.1. No code or ideas were
taken from it. It was only used to cross-check how the Fabric side replaces NeoForge's grindstone, item entity and vehicle hooks.

## Libraries that ship inside the jars

| Library | Used for | License |
|---|---|---|
| [JLayer](https://github.com/umjammer/jlayer) by umjammer | MP3 decoding | LGPL-2.1 |
| [JAAD](https://github.com/Tianscar/jaadec) (`com.tianscar.javasound:jaad`) by Tianscar | AAC decoding | Apache-2.0 |

Both are included unchanged. The Fabric jar nests them, and the NeoForge jar includes them with Jar-in-Jar.

## Mods and services used but not shipped

- [Forge Config API Port](https://modrinth.com/mod/forge-config-api-port) is required on Fabric for config files (MPL-2.0).
- [Create Fabric](https://github.com/Livinglive234/createfabric) and [JEI](https://github.com/mezz/JustEnoughItems) are only compiled against.
  Their support is used when the mods are installed. [Valkyrien Skies](https://modrinth.com/mod/valkyrien-skies) is reached by name and is not
  needed to build.
- Music search uses the public [Radio Browser](https://www.radio-browser.info) service and SoundCloud. Tracks can also be played from
  SoundCloud, Bandcamp and addresses given by players. This mod does not include or host any music.
