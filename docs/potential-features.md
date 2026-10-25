# Potential features

Features from other Etched forks that were looked at and left out for now. Each one lists where to find it, what it needs, and
anything to watch for. Already ported work is credited in the [README](../README.md).

Sources are read-only references:
[BazZziliuS/etched](https://github.com/BazZziliuS/etched) (NeoForge 1.21.1),
[wisplite/etched](https://github.com/wisplite/etched) (NeoForge 1.21.1),
[oogabooga-dev/re-etched](https://github.com/oogabooga-dev/re-etched) (Forge 1.20.1, its own protocol, so ideas only),
[VoidAndEther/etched_refabricated](https://github.com/VoidAndEther/etched_refabricated) (Fabric only, nothing left to take).

## Music sources and search

| Feature | From | Notes |
|---|---|---|
| In-game music search for radios and etching tables | BazZziliuS, `8dc0116` | `MusicSearchScreen` and `client/search/*` with Radio Browser, SoundCloud and Audius. Client code only, plus a button on the etching and radio screens. The SoundCloud search needs the scraped client id. |
| Audius and Internet Archive sources | BazZziliuS, `e76aa86` | Plain Java sources that plug into `SoundSourceManager`. Easy to port. |
| Yandex Music source | BazZziliuS, `e76aa86` | Needs an OAuth token in the config, otherwise only 30 second previews. Uses an undocumented API, so it can break or conflict with the service's terms. |
| Etching table looks up track info on the client | BazZziliuS, `db9d7b8` | Moves the metadata lookup off the server. Pairs well with the network policy since the server makes fewer requests. |

## Radio and playback

| Feature | From | Notes |
|---|---|---|
| Per-jukebox and radio volume and range | BazZziliuS, `d10b9b4` | A sound settings menu, a packet, and gain and range scale on `AbstractOnlineSoundInstance`. Fits the platform layer, but it touches that class heavily. |
| Station history, last 20 per world or server | re-etched `4.1.0` | Client only JSON file with a scrollable list on the radio screen. Stations are only recorded once the server confirms them. |
| ICY stream titles on screen | re-etched `4.0.0-beta.1` | Reads `StreamTitle` metadata from Shoutcast and Icecast streams. |
| M3U and PLS stations with fallbacks | re-etched `4.0.0-beta.1` | Nested playlists and ordered fallback stations. We only handle M3U segments for HLS today. |
| Separate Play, Stop and Close controls with reconnects | re-etched `4.0.0` | A radio session with explicit states and bounded automatic reconnects. A larger rework of `RadioBlockEntity` and `RadioScreen`. |

## Mod compatibility

| Feature | From | Notes |
|---|---|---|
| Create contraption support for radios and jukeboxes | BazZziliuS, `6fa2930`, `d10b9b4`, `8a0dfc5`, `0cd76ac` | NeoForge only and tied to Create's internals. Create has a separate Fabric build, so Fabric would need its own version. The last commit fixes a crash with Create Connected. |
| Sable sub-level distance checks | BazZziliuS, `d10b9b4` | NeoForge only. Uses the Sable companion library so sounds on sub-levels are heard from the right distance. |

## Album covers

| Feature | From | Notes |
|---|---|---|
| Custom cover image URL on album covers | wisplite, branch `customgui` (`ea871b2`) | Adds `SetCoverUrlPacket`. The author's own commit message calls it "terrible no good very bad code", so use it for ideas and write it fresh. |

## Security and privacy

| Item | From | Notes |
|---|---|---|
| Stop sending the player's username and UUID to third party hosts | re-etched `4.0.0-beta.1` | `AudioSource.getDownloadHeaders()` adds `X-Minecraft-Username` and `X-Minecraft-UUID` to every request. A disc or radio can point at any host, so that host can log who played it. Re-Etched removed it. |
| Size caps on playlist and provider responses | re-etched `4.0.0-beta.1` | Limits response bodies, playlist depth and entries so a hostile server cannot make the game read without end. |
| Per-server rule for private network stations | Idea only | We have one global client option. A per-server list would be safer. |

## Other

| Item | Notes |
|---|---|
| Automated tests | There are none. The network policy and the AAC decoder are the obvious first targets. |
| Fabric grindstone shift-click | Shift-clicking an album cover into the grindstone does not work on Fabric, placing it directly does. |
| NeoForge and Fabric in-game test pass | Only the servers and the Fabric client title screen have been started so far. |
