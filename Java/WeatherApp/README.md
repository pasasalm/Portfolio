Tämä on yliopistokurssin, ohjelmointi 3 ryhmätyö, jossa on toteutettu Suomen sääappi.
Kyseinen projekti on tehty yhteistyössä Tommi Paavolan ja Sofia Mustajärven kanssa. 
Ohjelmaan on kirjoitettu kuka henkilö on tehnyt minkäkin osion.
Ohjelman tarkempi dokumentaatio löytyy documentation isosta.

Rakenne
Ohjelman pääluokka on WeatherApp. Kukin Controller-luokka (Primary, Secondary, History, Favorites) vastaa
yhdestä ohjelman näkymästä. PrimaryController huolehtii säädatan näyttämisestä. WeatherDataMap
ja WeatherData ovat tietorakenneluokkia, johon tallennetaan käyttöä varten säätietoja.
iAPI sekä OpenWeatherMapAPI huolehtivat API-kutsuista, ja niihin liittyen on luotu GSonin käyttöä
varten muutama pieni luokkatiedosto. Controller-luokat käyttävät luokkaa iReadAndWriteToFile
json-tiedostojen lukuun ja kirjoittamiseen. WeatherApp-luokan yhteys kyseiseen luokkaan
johtuu siitä, että ohjelman käynnistyessä haetaan tiedostosta viimeisen avoinna ollut näkymä.

Vastuu osat Patrik Salmensaari
WeatherDataMap
WeatherData
OpenWeatherMapAPI
iReadAndWriteToFile
Testitiedostot ja ohjelman testaaminen.


Käyttöohje:

Mikäli haluat ajaa koodia omalla koneellasi, tulee sinun asettaa iAPI luokkaan oma OpenWeatherMap ApiKey, ilman tätä koodi ei toimi.
Avatessa ohjelman ensimmäistä kertaa ohjelma avautuu tilaan, jossa paikkakuntaa ja sen säätietoja ei
ole. Voit hakea paikkakunnan säätietoja hakuvälilehdeltä. Haku tapahtuu kirjoittamalla hakukenttään
paikkakunnan nimen ja painamalla hakunappia. Jos ohjelma ilmoittaa paikkakunnan löytyneen, voit
napilla ”see the weather” nähdä kyseisen paikkakunnan sään. Painamalla ennen säätietojen
näyttämistä kohdasta ”add to favorites” voit myös lisätä paikkakunnan suosikkeihin. Jos et halua
nähdä uuden paikkakunnan säätä vaan palata edeltävään näyttöön, voit painaa ”see the weather” -
napin sijasta nappia ”go back”.
Säätiedoissa näet ylhäällä nykytilanteen, sekä keskivaiheilla päiväkohtaiset koosteet. Klikkaamalla eri
päiviä voit tutkia kyseisen päivän tuntikohtaista ennustetta. Halutessasi voit vaihtaa yksikkö SI-
järjestelmän ja Imperial-järjestelmän välillä napilla ”Change units”.
Sivulla favorites voit katsella suosikkipaikkakuntia. Niitä voi olla maksimissaan kahdeksan kappaletta.
Jos yrität lisätä täyteen suosikkikirjoon uusia paikkakuntia, se ei onnistu, vaan joudut ensin
poistamaan vanhoja suosikkeja klikkaamalla paikkakuntaa hiiren oikealla näppäilemällä ja
valitsemalla ”delete”. Klikkaamalla jotakin suosikkipaikkakuntaa pääset tutkimaan sen sääennustetta.
History-välilehti toimii likimain vastaavasti kuin favorites-välilehti, mutta siitä et voi poistaa
paikkakuntia. Vastaavasti voit klikata mitä tahansa paikkakuntaa välilehdellä nähdäksesi sen sään.
Historian tapauksessa talletettava maksimimäärä on 20 paikkakuntaa.
Jos nyt ohjelman käyttämisen jälkeen päätät sammuttaa sen ja palata ohjelman pariin myöhemmin,
niin sekä historia että suosikit ovat tallella viime istunnolta. Lisäksi viimeksi näyttämäsi paikkakunnan
sää aukeaa ohjelmaan suoraan näkyviin.
