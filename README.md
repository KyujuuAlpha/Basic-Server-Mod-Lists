# Basic Server Mod Lists

Simple Fabric mod that compares server and client mod lists similarly to Forge.  Mod IDs and versions are explicitly compared such that the client must have the same version running on the server.  Differing version may be supported in the future.  Required client mods on a server have to be listed in the 'required-mods.txt' file, otherwise it will default to every mod.  A Velocity plugin is provided to allow communication mod list pack passthrough in a proxy setup.

## Config File

The current 'required-mods.txt' format requires mods to be listed as 'MODID_VERSION' on separate lines.  An asterisk can be used for prefix/suffix wildcards.

Example config:
```
twilightforest_*
create_*
cobblemon_*
```

## Setup

For setup instructions please see the [fabric wiki page](https://fabricmc.net/wiki/tutorial:setup) that relates to the IDE that you are using.

## License

This template is available under the CC0 license. Feel free to learn from it and incorporate it in your own projects.
