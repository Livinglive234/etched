# Features

Every feature of this branch, where it came from, and the features that were left out on purpose. Features that might still be added later
are in [potential-features.md](potential-features.md).

The **Origin** column is one of:

- **Original**: in Etched before this branch, by Moonflower Studio (Ocelot, Jackson, Farcr for art and AstraZoey for sound design).
- **Created here**: written for this branch.
- **Fork, code**: code ported from another fork. The fork and its author are credited.
- **Fork, idea**: an idea taken from another fork and written again here. The fork and its author are credited.

The forks are [BazZziliuS/etched](https://github.com/BazZziliuS/etched) (BazZziliuS),
[wisplite/etched](https://github.com/wisplite/etched) (wisplite) and
[oogabooga-dev/re-etched](https://github.com/oogabooga-dev/re-etched) (oogabooga-dev).
[VoidAndEther/etched_refabricated](https://github.com/VoidAndEther/etched_refabricated) (VoidAndEther) was only used as a reference for the
Fabric port and no code or ideas were taken from it.

## Items and blocks

| Feature | Origin | Details |
|---|---|---|
| Etching table | Original | Etches a track from a URL onto a blank music disc, using a music label for the artist and title. Playlist and album URLs make a disc with several tracks. |
| Blank music disc | Original | Can be dyed in a cauldron, which colors the disc, and is made by smelting any music disc. |
| Etched music disc | Original | Holds one or more tracks, with six label patterns and colors taken from the music label. Copied with a music disc cloning recipe. |
| Music label | Original | Edited with a screen for the artist and title, dyed with dyes, merged with another label, and washed clean in a cauldron. |
| Album cover | Original | Holds several discs, with its own menu or by right-clicking discs into it. Shows cover art in the inventory. The cover can be removed in a grindstone. |
| Boombox | Original | Plays a disc while it is in your inventory or hand. Discs are right-clicked into it, and sneak and use pauses it. |
| Jukebox minecart | Original | A minecart that plays a disc, started again by an activator rail. |
| Album jukebox | Original | A jukebox that holds several discs, with a screen to choose the track. |
| Radio | Original | Plays an internet radio stream from a URL, with a menu to set it, and stops while it is powered by redstone. |
| Volume slider on radios, album jukeboxes and jukeboxes | Fork, code and idea | Lowering the volume also lowers how far away the music can be heard, and applies while it plays. Sneak and use a vanilla jukebox with empty hands to open its volume menu. The slider and the distance fade come from the sound settings of BazZziliuS. A single volume that also sets the range, and the vanilla jukebox support, are done here. |
| Station history on the radio screen | Fork, idea | The last 20 stations used in each world or server, kept only on the client with hashed world and server names, with a scrollable list and a clear button. Picking one fills the URL box. The idea comes from Re-Etched by oogabooga-dev. |
| Portal radio | Original | A radio variant with its own look. A radio becomes one with cake, or when a radio item is thrown into the nether. |
| Vanilla jukebox plays Etched discs | Original | Etched discs and album covers can be put in the vanilla jukebox, which then gives a comparator signal. |

## Sound sources and playback

| Feature | Origin | Details |
|---|---|---|
| Direct URLs for OGG, WAV and MP3 files | Original | Any HTTP(S) URL to a supported audio file. |
| Minecraft sound events as tracks | Original | A disc can hold a sound already in the game. |
| SoundCloud and Bandcamp links | Original | Tracks, sets and albums, with cover art. |
| M3U and HLS streams | Original | Live streams and segmented tracks. |
| Disk cache for audio and cover art | Original | Downloaded audio and cover images are kept on disk. |
| Music info on tooltips and the Now Playing text | Original | Includes the source of the track and the album marker. |
| Sophisticated Core jukebox upgrade support | Original | NeoForge only, since Sophisticated Core has no Fabric version. |
| AAC audio from any URL, in ADTS and fragmented MP4 | Fork, code | Decoded with the jaad library. From BazZziliuS. |
| SoundCloud playback of AAC over HLS | Fork, code | SoundCloud no longer serves MP3 for most tracks. From BazZziliuS. |
| Decoding on a separate thread so a slow connection plays silence and never freezes the game | Fork, code | `AsyncAudioStream`, with stream timeouts, a read that only ends at the real end of the stream, and a close that does not block. From BazZziliuS. |
| Looping that does not rewind live streams | Fork, code and idea | From BazZziliuS, with the same problem described and solved separately by wisplite. |
| Live streams never read from the HTTP cache | Fork, code | The no-cache request headers come from wisplite. |
| Segment prefetching, and M3U version 7 | Fork, code | Long tracks no longer download every segment at once. From BazZziliuS. |
| Google Drive and Dropbox share links | Fork, code | Converted to direct download links. From BazZziliuS. |

## Network safety and privacy

| Feature | Origin | Details |
|---|---|---|
| Blocking requests to private networks, this computer and cloud metadata addresses, checked for every redirect | Fork, code and idea | The design and address classifier come from Re-Etched by oogabooga-dev. Redirects are followed by hand with the same check. |
| Client option `Allow Private Network Stations` | Fork, idea | The option and its name come from Re-Etched by oogabooga-dev. |
| No username or UUID in audio requests | Fork, idea | Re-Etched by oogabooga-dev removed it from its radio requests. Etched used to send both to every host. |
| Track info for the etching table looked up by the client | Fork, code | Works when the server cannot reach the music service. The client sends the tracks in an optional packet and clients without it keep the old behavior. From BazZziliuS. |
| Size limits on playlists, music service pages and replies, and cover images | Fork, idea | Playlists are limited to 1 MiB and 8192 entries, replies to 2 MiB, pages to 8 MiB and covers to 16 MiB. The idea comes from the response limits of Re-Etched by oogabooga-dev. |
| Short error messages when etching | Created here | The server makes the request, so players only see status text and generic messages. |

## Platform and compatibility

| Feature | Origin | Details |
|---|---|---|
| NeoForge 1.21.1 | Original | Builds against NeoForge 21.1.265 and supports 21.1.0 or newer. Development of the NeoForge side is stalled. It builds, its server starts and its data generation works, but it has not been played in game since the move to two loaders. |
| Fabric 1.21.1 | Created here | Uses Fabric API and Forge Config API Port. |
| One codebase for both loaders | Created here | `common`, `neoforge` and `fabric` modules with a small platform layer. |
| Data generation on both loaders | Created here | Tags, recipes and loot are defined once. NeoForge generates the item models with its own provider and Fabric with a custom provider that matches it. |
| Amendments jukebox look | Created here | Amendments draws the disc on top of a jukebox with its own renderer. Etched discs now spin there, the gold label is drawn, and the jukebox minecart draws its disc the same way. |
| Build and download of both jars in GitHub Actions | Created here | Every push builds the NeoForge and Fabric jars and keeps them as artifacts. |

## World and villagers

| Feature | Origin | Details |
|---|---|---|
| Bard villager | Original | Works at a note block and trades discs, note blocks, labels, blank discs and Etched blocks. |
| Bard houses in villages | Original | For plains, desert, savanna, snowy and taiga villages. |
| Dancing parrots | Original | Parrots dance to radios, album jukeboxes and jukeboxes that are playing Etched music. They also dance within about 3.5 blocks of anything carrying a playing boombox, and of a jukebox minecart. Parrots on a shoulder do not dance. There is an option for smoother dancing. |
| Now Playing text hidden for far or covered jukeboxes | Original | The text is not shown when a jukebox is covered or too far away. |

## Options

| Feature | Origin | Details |
|---|---|---|
| Force Stereo, Smooth Parrot Animation | Original | Client options. |
| Use boombox menu, Use album cover menu | Original | Server options that swap right-clicking discs for a menu. |
| Allow Private Network Stations | Fork, idea | See network safety above. |
| 13 language files | Original | English plus translations by the community. |

## Omitted features

| Feature | Notes |
|---|---|
| Shift-clicking an album cover into the grindstone on Fabric | Placing an album cover into the grindstone directly works on both loaders, and removes the cover. Shift-clicking it from the inventory does not work on Fabric. Left out on purpose since the direct placement covers the use. |
