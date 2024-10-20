from telegram.ext import Application
from telegram import Update
from telegram.ext import ApplicationBuilder
from telegram.ext import CommandHandler, ContextTypes
from telegram.ext import ConversationHandler
from telegram.ext import MessageHandler
from telegram.ext import filters
import sys
import os
import spotipy
from spotipy.oauth2 import SpotifyClientCredentials
from datetime import datetime, timedelta
from spotify_api_keys import SPOTIPY_CLIENT_ID, SPOTIPY_CLIENT_SECRET, SPOTIPY_REDIRECT_URI
from telegram_bot_token import Telegram_bot_token
import logging




# Luodaan loggeri
logger = logging.getLogger()
logger.setLevel(logging.ERROR)  # Asetetaan loggerin taso ERROR-tasolle

# Luodaan konsolille tarkoitettu käsittelijä
console_handler = logging.StreamHandler()
console_handler.setLevel(logging.ERROR)  # Asetetaan käsittelijän taso ERROR-tasolle

# Lisätään käsittelijä loggeriin
logger.addHandler(console_handler)


def get_current_week():
    # Muokattu viikon numeron hakeminen niin, että se vaihtuu vasta perjantaina
    current_date = datetime.now()
    if current_date.weekday() == 4 and current_date.time() >= datetime.strptime(
            "00:01", "%H:%M").time():
        return current_date.isocalendar()[1]
    else:
        # Jos ei vielä perjantai tai kello ei ole vielä 00:01, käytetään edellisen viikon numeroa
        previous_friday = current_date - timedelta(
            days=(current_date.weekday() + 2) % 7)
        return previous_friday.isocalendar()[1]


async def start(update: Update, context: ContextTypes.DEFAULT_TYPE):
    await update.message.reply_text('Hei! Olen PerjantaiBiisiBottisi.')
    await update.message.reply_text('Syöttämällä koodin /menu, kerron lisää mitä voit tehdä')


async def handle_menu(update: Update, context: ContextTypes.DEFAULT_TYPE):
    await update.message.reply_text(
        "Valitse toiminto:\n"
        "1. /ohjeet\n"
        "2. /hae_soittolista\n"
        "3. /etsi_kappale\n"
        "4. /kommentoi_kappaletta\n"
        "5. /hae_kommentit\n"
        "6. /kaikki_biisit\n"
        "7. /toplistalle\n"
        "8. /tulostatoplista\n"
        "9. /lopeta\n"
    )
    return 1



async def introductions(update: Update, context: ContextTypes.DEFAULT_TYPE):

    await update.message.reply_text("Hei, kiva että päädyit käyttämään ohjelmaani")
    await update.message.reply_text("Olen vielä hieman keskeneräinen, joten palautteen voit lähettää telegrammissa käyttäjälle: PatrikSalmensaari")

    await update.message.reply_text("Kuten huomasit /menu antaa kaikki komennot")
    await update.message.reply_text("Tähän alle on avattu muita komentoja")

    await update.message.reply_text("/hae_soittolista päivittää uuden viikon soittolistan")
    await update.message.reply_text("/etsi_kappale toiminnolla voi etsiä kappaleen tai artistin nimellä olevia kappaleita. Komento on: /etsi_kappale, kappaleen/artistin nimi")
    await update.message.reply_text("/kommentoi_kappaletta toiminnolla voit lisätä oman kommentin kyseiseen kappaleeseen Kometo on: /kommentoi_kappaletta, kappaleen nimi, omat kommentit")
    await update.message.reply_text("/hae_kommentit toiminnolla voit hakea kaikki kommentit kyseisestä kappaleesta. Komento on: /hae_kommentit, kappaleen nimi")
    await update.message.reply_text("/kaikki_biisit toiminto tulostaa kaikki uuden viikon biisit")

    await update.message.reply_text("/toplistalle toiminto lisää kappaleen top listalle. Komento on: /toplistalle, kappaleen nimi - Artisti")
    await update.message.reply_text("/tulostatoplista toiminto tulostaa toplistan")

    await update.message.reply_text("/lopeta toiminto lopettaa ohjelma")

    return ConversationHandler.END

async def create_spotify_playlist_file(update: Update, context: ContextTypes.DEFAULT_TYPE):
    try:
        # Spotify API-avainten määrittely ja autentikointi
        client_credentials_manager = SpotifyClientCredentials(
            client_id=SPOTIPY_CLIENT_ID,
            client_secret=SPOTIPY_CLIENT_SECRET,
        )
        sp = spotipy.Spotify(client_credentials_manager=client_credentials_manager)

        # Spotify-soittolistan URL-osoite
        SPOTIFY_PLAYLIST_URL = "https://open.spotify.com/playlist/37i9dQZF1DWXtcXUwhuzFM"
        playlist = sp.playlist(SPOTIFY_PLAYLIST_URL)

        # Tiedoston nimen määrittely
        tiedoston_nimi = f"BiisiPerjantai Viikko {viikon_numero}.txt"

        if os.path.exists(tiedoston_nimi):
            print(f"Tiedosto {tiedoston_nimi} on jo olemassa.")
            await update.message.reply_text(f"Tiedosto {tiedoston_nimi} on jo olemassa.")
        else:
            with open(tiedoston_nimi, "w", encoding="utf-8") as file:
                file.write(f"BiisiPerjantai Viikko {viikon_numero}\n\n")
                file.write(f"Soittolistan nimi: {playlist['name']}\n")
                file.write(f"Soittolistan omistaja: {playlist['owner']['display_name']}\n\n")
                file.write(f"Tiedot seuraavassa järjestyksessä:\n")
                file.write(f"Kappaleen nimi - Artisti - Linkki kappaleeseen\n\n")
                file.write("Kappaleet:\n\n")

                for track in playlist['tracks']['items']:
                    track_info = track.get('track')
                    if track_info:
                        track_url = f"https://open.spotify.com/track/{track_info['id']}"
                        file.write(
                            f"{track_info['name']} - {', '.join([artist['name'] for artist in track_info['artists']])}\n")
                        file.write(f"Suora linkki: {track_url}\n\n")
                    else:
                        file.write("Tietoja ei saatavilla\n")

            print(f"Tiedosto {tiedoston_nimi} on luotu ja tiedot tallennettu.")
            await update.message.reply_text(f"Tiedosto {tiedoston_nimi} on luotu ja tiedot tallennettu.")

    except Exception as e:
        # Täällä käsitellään virheitä
        logging.error(f"Virhe tapahtui: {str(e)}")



async def search_track(update: Update, context: ContextTypes.DEFAULT_TYPE):

    # Muuta niin, että myös artistin nimellä voi etsiä kappaletta
    # Jos tallentaa biisit esim artisti - kappale dictiin ja toisinpäin

    search_query = update.message.text
    # Erotetaan viesti pilkulla ja poistetaan ensimmäinen osa
    user_input = ','.join(search_query.split()[1:])

    # Tässä voit toteuttaa koodin, joka hakee kappaleen tiedot tekstitiedostosta
    tiedoston_nimi = f"BiisiPerjantai Viikko {viikon_numero}.txt"

    found_track = None  # Muutettu muuttuja, joka tallentaa löydetyn kappaleen

    try:
        with open(tiedoston_nimi, "r", encoding="utf-8") as file:
            lines = file.readlines()
            is_kappaleet_section = False  # Merkki siitä, että ollaan Kappaleet-osiossa
            for line in lines:
                if line.strip().startswith("Kappaleet:"):
                    is_kappaleet_section = True
                    continue

                if is_kappaleet_section and line.strip():
                    # Tässä oletetaan, että tiedot ovat tietyssä muodossa tekstitiedostossa
                    # Voit muokata tätä osaa vastaamaan tekstitiedoston rakennetta
                    if " - " in line:
                        track_name, artist_name = line.strip().split(" - ", 1)
                        track_url = \
                        lines[lines.index(line) + 1].strip().split(": ")[1]

                        if user_input.lower() in track_name.lower() or user_input.lower() in artist_name.lower():
                            found_track = f"{track_name} - {artist_name}\nLinkki: {track_url}"
                            break  # Keskeytä silmukka, kun ensimmäinen osuma löytyy
    except FileNotFoundError:
        await update.message.reply_text(
            "Tiedostoa ei löydy. Voit luoda sen komennolla /Hae_soittolista.")
        return ConversationHandler.END

    if found_track:
        await update.message.reply_text("Löydetty kappale:")
        await update.message.reply_text(found_track)
    else:
        await update.message.reply_text("Kappaletta ei löytynyt.")
        await update.message.reply_text("Anna komento muodossa /etsi_kappale, kappaleen nimi")

    return ConversationHandler.END


async def process_comment(update: Update, context: ContextTypes.DEFAULT_TYPE):

    # Lisäys jossa kappaleen artistin nimi lisätään väistämättä tiedostoon.
    # Hakee artistin esim dictistä

    # Erotetaan komentoon liittyvät tiedot
    command_parts = update.message.text.split(', ', 2)

    if len(command_parts) == 3:
        _, track_name, comment = command_parts
        user_id = update.message.from_user.id

        # Tässä voit käyttää track_name ja comment -muuttujia haluamallasi tavalla
        response_message = f"Kommentit kappaleesta: {track_name}\n{comment}\n"

        # Tallenna kommentti tiedostoon
        comment_filename = f"BiisiPerjantai Viikko {viikon_numero} kommentit .txt"

        with open(comment_filename, 'a') as file:
            file.write(response_message + '\n')
            file.write('\n')  # Lisää yksi tyhjä rivi

        await update.message.reply_text("Kommentti tallennettu onnistuneesti.")
    else:
        await update.message.reply_text("Anna komento muodossa /kommentoi_kappaletta, kappaleen nimi, kommentit")

    return ConversationHandler.END


async def search_comments(update: Update, context: ContextTypes.DEFAULT_TYPE):

    # Kun kommentointia muutettu muuta tuloste niin että myös artisti tulostetaan.

    search_query = update.message.text
    # Erotetaan viesti pilkulla ja poistetaan ensimmäinen osa

    track_name = ' '.join(search_query.split(',')[1:]).strip()
    #track_name = ','.join(search_query.split()[1:])

    # muokkaa niin, että mikäli syötteessä on , jälkeen välilyöntejä niin poista ne

    # Hae kommentit tiedostosta
    comment_filename = f"BiisiPerjantai Viikko {viikon_numero} kommentit .txt"

    found_comments = []
    is_matching_track = False  # Lisää tämä rivi

    try:
        with open(comment_filename, "rb") as file:
            lines = file.readlines()

            for line in lines:
                decoded_line = line.decode("utf-8", errors="replace")

                if decoded_line.startswith("Kommentit kappaleesta:"):
                    # Tarkista, onko tämä etsittävä kappale
                    current_track_name = decoded_line.split(": ")[1].strip()
                    is_matching_track = current_track_name.lower() == track_name.lower()
                    continue

                if is_matching_track and decoded_line:
                    # Löydettiin kappaleen kommentti, lisätään se listalle
                    found_comments.append(decoded_line)

                #if decoded_line.startswith("Kommentit kappaleesta:") and not is_matching_track:
                    # Jos löydetään uuden kappaleen kommentit, lopetetaan nykyisen kappaleen etsiminen
                #    break

    except FileNotFoundError:
        await update.message.reply_text("Kommenttitiedostoa ei löydy. Ei tallennettuja kommentteja.")
        return ConversationHandler.END
    except UnicodeDecodeError:
        await update.message.reply_text("Virhe luettaessa tiedostoa. Tarkista tiedoston koodaus.")
        return ConversationHandler.END

    if found_comments:
        await update.message.reply_text(f"Kappaleen '{track_name}' kommentit:")
        for comment in found_comments:
            if comment.strip():
                await update.message.reply_text(comment)

    else:
        await update.message.reply_text("Kappaleelle ei löytynyt kommentteja.")
        await update.message.reply_text("Anna komento muodossa /hae_kommentit, kappaleen nimi")

    return ConversationHandler.END


async def list_all_tracks(update: Update, context: ContextTypes.DEFAULT_TYPE):
    # Tässä voit toteuttaa koodin, joka hakee kaikki kappaleet tekstitiedostosta
    tiedoston_nimi = f"BiisiPerjantai Viikko {viikon_numero}.txt"

    found_tracks = []

    try:
        with open(tiedoston_nimi, "r", encoding="utf-8") as file:
            lines = file.readlines()
            is_kappaleet_section = False  # Merkki siitä, että ollaan Kappaleet-osiossa
            for line in lines:
                if line.strip().startswith("Kappaleet:"):
                    is_kappaleet_section = True
                    continue

                if is_kappaleet_section and line.strip():
                    # Tässä oletetaan, että tiedot ovat tietyssä muodossa tekstitiedostossa
                    # Voit muokata tätä osaa vastaamaan tekstitiedoston rakennetta
                    if " - " in line:
                        track_info = line.strip().split(" - ", 1)
                        found_tracks.append(f"{track_info[0]} - {track_info[1]}")
    except FileNotFoundError:
        await update.message.reply_text("Tiedostoa ei löydy. Voit luoda sen komennolla /Hae_soittolista.")
        return

    if found_tracks:
        await update.message.reply_text("Kaikki kappaleet:")
        for track_info in found_tracks:
            await update.message.reply_text(track_info)
    else:
        await update.message.reply_text("Kappaleita ei löytynyt.")


async def add_to_top_list(update: Update, context: ContextTypes.DEFAULT_TYPE):

    # Lisä tarkistus onko kyseinen kappale tämän viikon uusi kappale
    # Lisää tiedostoon myös artistin nimi ja tulosta myös artistin nimi
    # Jos käyttäjä syöttää vain kappaleen nimen niin ohjelma hakee artistin nimen?
    # Jos kappaleen nimen lisäisi dictiin arvoksi artistin nimen.

    # Erotetaan kappaleen nimi ja artisti käyttäjän syötteestä
    user_input = ' '.join(context.args).strip()

    # Tarkista, että käyttäjä on antanut kappaleen nimen
    if not user_input:
        await update.message.reply_text(
            "Anna kappaleen nimi ja artisti komennon perään, esim. /toplistalle, kappaleen nimi - artisti")
        return

    # Tarkista, onko top listan tiedosto jo olemassa, ja luo se tarvittaessa
    top_list_filename = f"BiisiPerjantai Viikko {viikon_numero} toplista.txt"
    try:
        with open(top_list_filename, "x", encoding="utf-8"):
            pass
    except FileExistsError:
        pass

    # Lisää kappale top listalle
    with open(top_list_filename, "a", encoding="utf-8") as file:
        file.write(f"{user_input}\n")

    await update.message.reply_text(f"Kappale '{user_input}' lisätty top listalle.")


async def print_top_list(update: Update, context: ContextTypes.DEFAULT_TYPE):

    # Tarkista, onko top listan tiedosto olemassa
    top_list_filename = f"BiisiPerjantai Viikko {viikon_numero} toplista.txt"

    try:
        with open(top_list_filename, "r", encoding="utf-8") as file:
            top_list = file.readlines()

        if top_list:
            await update.message.reply_text("Top-lista:")
            for entry in top_list:
                await update.message.reply_text(entry.strip())
        else:
            await update.message.reply_text("Top-lista on tyhjä.")

    except FileNotFoundError:
        await update.message.reply_text(
            "Top-listaa ei löydy. Lisää kappaleita komennolla /toplistalle.")


async def end(update: Update, context: ContextTypes.DEFAULT_TYPE):
    await update.message.reply_text("Ohjelma suljetaan.")
    print("Käyttäjä sulki oman tg bottinsa, ohjelman tulee jäädä ns käyntiin")
    return ConversationHandler.END



# GLOBAL VARIABLES


viikon_numero = get_current_week()
# Globaali muuttuja mutta voisiko sijoittaa johonkin fiksumpaan paikaan??


def main():
    # Create Application instance
    application = ApplicationBuilder().token(Telegram_bot_token).build()
    print("Ohjelma alkaa")

    # Add ConversationHandler to handle different stages of the menu
    conv_handler = ConversationHandler(
        entry_points=[CommandHandler("menu", handle_menu)],
        states={
            1: [MessageHandler(filters.TEXT & ~filters.COMMAND, introductions)],
            2: [MessageHandler(filters.TEXT & ~filters.COMMAND, create_spotify_playlist_file)],
            3: [MessageHandler(filters.TEXT & ~filters.COMMAND, search_track)],
            4: [MessageHandler(filters.TEXT & ~filters.COMMAND, process_comment)],
            5: [MessageHandler(filters.TEXT & ~filters.COMMAND, search_comments)],
            6: [MessageHandler(filters.TEXT & ~filters.COMMAND, list_all_tracks)],
            7: [MessageHandler(filters.TEXT & ~filters.COMMAND, add_to_top_list)],
            8: [MessageHandler(filters.TEXT & ~filters.COMMAND, print_top_list)],
            9: [MessageHandler(filters.TEXT & ~filters.COMMAND, end)],
        },
        fallbacks=[],
    )

    # Register the conversation handler and other commands
    application.add_handler(conv_handler)
    application.add_handler(CommandHandler("start", start))
    application.add_handler(CommandHandler("ohjeet", introductions))
    application.add_handler(CommandHandler("hae_soittolista", create_spotify_playlist_file))
    application.add_handler(CommandHandler("etsi_kappale", search_track))
    application.add_handler(CommandHandler("kommentoi_kappaletta", process_comment))
    application.add_handler(CommandHandler("hae_kommentit", search_comments))
    application.add_handler(CommandHandler("kaikki_biisit", list_all_tracks))
    application.add_handler(CommandHandler("toplistalle", add_to_top_list))
    application.add_handler(CommandHandler("tulostatoplista", print_top_list))
    application.add_handler(CommandHandler("lopeta", end))

    # Start polling updates
    application.run_polling()

if __name__ == '__main__':
    main()


