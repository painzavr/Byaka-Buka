# Music Discord Bot

This Discord bot is a musical delight, crafted with lavaplayer and Java Discord API. With a range of prefix commands starting with "!", it offers seamless music playback in your server.

## Commands:
- **!play**: Add a track to the queue for playback.
- **!stop**: Disconnect the bot from the voice channel.
- **!pause**: Forcefully pause the current track.
- **!resume**: Resume playback after a pause.
- **!nowplaying**: Display the current track in the queue.

## Additional Features:
- Slash commands for creating, formatting, and playing custom playlists.
- Integration with PostgreSQL for efficient data storage.
- Optional Rest API connection for interaction with stored information:
  - Track users' voice channel activity time.
  - Manage and retrieve playlists.

## Getting Started:
1. Clone the repository.
2. Configure your Discord API token. - > Take it on developer portal and parse it  to initialization in class mBot 'UR_TOKEN'
3. Start the PostgreSQL database with the configuration specified in `application.properties`.
4. Install dependencies.
5. Run the bot.

Feel free to explore and enhance the bot as per your preferences! If you encounter any issues or have suggestions, please create an issue in the repository.

Happy listening! 🎶
