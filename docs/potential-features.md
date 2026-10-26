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
| Audius and Internet Archive sources | BazZziliuS, `e76aa86` | Plain Java sources that plug into `SoundSourceManager`. Easy to port, and the Audius one also has a search source for the music search screen. |
| Yandex Music source | BazZziliuS, `e76aa86` | Needs an OAuth token in the config, otherwise only 30 second previews. Uses an undocumented API, so it can break or conflict with the service's terms. |

## Mod compatibility

| Feature | From | Notes |
|---|---|---|
| Create contraption support on NeoForge | BazZziliuS, `6fa2930`, `d10b9b4`, `8a0dfc5`, `0cd76ac` | The Fabric version, with editing while mounted, is done. The NeoForge version is not. The last commit fixes a crash with Create Connected. |
| Sable sub-level distance checks | BazZziliuS, `d10b9b4` | NeoForge only. Uses the Sable companion library so sounds on sub-levels are heard from the right distance. |

## Album covers

| Feature | From | Notes |
|---|---|---|
| Custom cover image URL on album covers | wisplite, branch `customgui` (`ea871b2`) | Adds `SetCoverUrlPacket`. The author's own commit message calls it "terrible no good very bad code", so use it for ideas and write it fresh. |

## Security and privacy

| Item | From | Notes |
|---|---|---|
| Per-server rule for private network stations | Idea only | We have one global client option. A per-server list would be safer. |

## Other

| Item | Notes |
|---|---|
| Automated tests | There are none. The network policy and the AAC decoder are the obvious first targets. |
| Multiplayer test pass | Joining a dedicated server with a second player, and the Google Drive and Dropbox share links, have not been tried. |
| NeoForge in-game test pass | Stalled. The NeoForge build has not been played in game since the move to two loaders. |
