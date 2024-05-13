// Datastructures.cc
//
// Student name: Patrik Salmensaari
// Student email: patrik.salmensaari@tuni.fi
// Student number: 150987125

#include "datastructures.hh"
#include <random>
#include <cmath>
#include <unordered_map>
#include <iostream>
#include <algorithm>
#include <vector>
#include <unordered_set>
#include <memory>
#include <queue>

std::minstd_rand rand_engine; // Reasonably quick pseudo-random generator

template <typename Type>
Type random_in_range(Type start, Type end)
{
    auto range = end-start;
    ++range;

    auto num = std::uniform_int_distribution<unsigned long int>(0, range-1)(rand_engine);

    return static_cast<Type>(start+num);
}

// Modify the code below to implement the functionality of the class.
// Also remove comments from the parameter names when you implement
// an operation (Commenting out parameter name prevents compiler from
// warning about unused parameters on operations you haven't yet implemented.)

Datastructures::Datastructures()
{
    // Write any initialization you need here
}

Datastructures::~Datastructures()
{
    // Write any cleanup you need here
}


unsigned int Datastructures::get_affiliation_count()
{
    // vektorin koon palautus tehokkaampi O(1)
    // kuin mapin palautus
    return all_aff.size();
}

void Datastructures::clear_all()
{
    // poistetaan ensin Affiliaatioiden julkaisut

    for (auto& affiliationpair : affiliations_map) {
        Affiliation& affiliation = affiliationpair.second;
        affiliation.publicationsID.clear();
        // tyhjentää vektorin jossa id mukaan julkaisut
    }

    affiliations_map.clear(); // tyhjentää kaikki affiliaatiot map rakenteesta
    publications_map.clear();

    // tyhjennetään myös vektorit joissa tiedot tietyn järjestyksen mukaan.
    aff_alphabetically.clear();
    aff_dis_inc.clear();
    all_aff.clear();
    all_pub.clear();
    // muista tyhjentää lisätyt vektorit!!
}

std::vector<AffiliationID> Datastructures::get_all_affiliations()
{

    /*
    // palauttaa vektorin jossa kaikki affiliaatioiden ID
    std::vector<AffiliationID> result;

    for (const auto& pair : affiliations_map) {
        result.push_back(pair.first);
    }

    return result;
    */

    return all_aff;
}

bool Datastructures::add_affiliation(AffiliationID id, const Name &name, Coord xy)
{
    if (affiliations_map.find(id) != affiliations_map.end()) {
        return false;
        // jos affiliaation id löytyy mapista palauttaa false arvon
        // hyödyntää find algorytmia
        // huonoimmassa tapauksessa find tehokkuus n
    }


    // luodaan uusi affiliaatio ja määritetään sen tiedot
    Affiliation newAffiliation;
    newAffiliation.ID = id;
    newAffiliation.name = name;
    newAffiliation.location.x = xy.x;
    newAffiliation.location.y = xy.y;

    // viedään affiliaation id vektroriin
    // tallennetaan affiliaatio id avulla myös map rakenteeseen.
    affiliations_map[id] = newAffiliation;
    all_aff.push_back(id);

    return true;
}

Name Datastructures::get_affiliation_name(AffiliationID id)
{
    // etsitään nimi mapista, sen palautus tai virhe
    auto it = affiliations_map.find(id);
    if (it == affiliations_map.end()) {
        return NO_NAME;
    } else {
        return it->second.name;
    }
}

Coord Datastructures::get_affiliation_coord(AffiliationID id)
{
    // estitään kordinaatit mapista tai virhe
    auto it = affiliations_map.find(id);
    if (it == affiliations_map.end()) {
        return NO_COORD;
    } else {
        return it->second.location;
    }
}

std::vector<AffiliationID> Datastructures::get_affiliations_alphabetically()
{
    // koko tarkistus!! jos eri niin silloin sortataan
    // ekalla kerralla affiliations_alphabetically on tyhjä
    // joten lisää joka tapauksessa kaikki mapin af
    // tämän jälkeen jos mappiin ei ole lisätty ei sorttaa uudestaan
    if (aff_alphabetically.empty() ||
        aff_alphabetically.size() != affiliations_map.size())
    {
        aff_alphabetically.clear();
        for (const auto& pair : affiliations_map) {
            aff_alphabetically.emplace_back(pair.first);
        }

        // Sorttaa affiliaatiot nimen mukaan
        std::sort(aff_alphabetically.begin(),
                  aff_alphabetically.end(),
                  [this](const auto& lhs, const auto& rhs) {
                      return affiliations_map.at(lhs).name < affiliations_map.at(rhs).name;
                  });
    }
    return aff_alphabetically;
}

bool Datastructures::solve_distance(AffiliationID id1, AffiliationID id2){

    // haetaan id:n kordinaatit
    const Coord coord1 = affiliations_map[id1].location;
    const Coord coord2 = affiliations_map[id2].location;

    // lasketaan Euklidinen etäisyys origosta, sqrt(x^2+y^2)
    int distance1 = sqrt(coord1.x*coord1.x+coord1.y*coord1.y);
    int distance2 = sqrt(coord2.x*coord2.x+coord2.y*coord2.y);

    // tehdään vertailu kumpi lähempänä origoa
    if (distance1 < distance2) { return true; }
    if (distance1 > distance2) { return false; }

    // Vertaillaan y-koordinaatteja, jos etäisyys origosta on sama
    return coord1.y < coord2.y;
}

std::vector<AffiliationID> Datastructures::get_affiliations_distance_increasing()
{

    // tarkistaa onko affiliaatioita lisätty viimeisen sorttauksen jälkeen
    if (aff_dis_inc.empty() || aff_dis_inc.size() != affiliations_map.size()) {

        aff_dis_inc.clear();

        // Add elements to affiliations_alphabetically
        for (const auto& pair : affiliations_map) {
            aff_dis_inc.emplace_back(pair.first);
        }
    }

    if (std::is_sorted(aff_dis_inc.begin(), aff_dis_inc.end(),
                       [this](AffiliationID id1, AffiliationID id2) { return solve_distance(id1, id2); })) {
        return aff_dis_inc;
    } else {

        std::sort(aff_dis_inc.begin(), aff_dis_inc.end(),
                  [this](AffiliationID id1, AffiliationID id2) { return solve_distance(id1, id2); });

    }
    return aff_dis_inc;
}

AffiliationID Datastructures::find_affiliation_with_coord(Coord xy)
{

    // tee tästä vielä nopeampi!!
    // ei onnistunut fiksusti

    // käydään kaikki mapissa läpi ja palautetaan se id jonka kordinaatit mätsää
    for (const auto& key : affiliations_map) {
        if (key.second.location.x == xy.x && key.second.location.y == xy.y) {
            return key.first;
        }
    }
    return NO_AFFILIATION;
}

bool Datastructures::change_affiliation_coord(AffiliationID id, Coord newcoord)
{

    auto it = affiliations_map.find(id);
    if ( it != affiliations_map.end()) {

        it->second.location = newcoord;
        return true;
    } else {
        return false;
    }
}

bool Datastructures::add_publication(PublicationID id, const Name &name, Year year, const std::vector<AffiliationID> &affiliations)
{

    // tarkistetaan onko kyseisellä tallennettu id aiemmin.
    if (publications_map.find(id) != publications_map.end()) { return false; }

    // luodaan uusi publication ja määritellään sen tiedot
    Publication newPublication;
    newPublication.ID = id;
    newPublication.title = name;
    newPublication.year = year;

    // lisätään myös unordered_mappiin jossa kaikkien julkaisuiden tiedot
    publications_map[id] = newPublication;
    all_pub.push_back(id);

    // käydään kaikki affiliations vektorin jutut läpi ja lisätään jokaisen AF
    // jos ei ole yhtään affiliaatiota niin palauttaa false.

    // ei lisätä julkaisun creatoreihin

    for (const AffiliationID &aff_id : affiliations) {

        auto it = affiliations_map.find(aff_id);
        if ( it != affiliations_map.end()) {
            it->second.publicationsID.push_back(id);

            // miten alla olevaa ei aiemmin ollut??
            publications_map[id].creator.push_back(aff_id);

            // tarkistaa onko kyseisellä id:llä mapissa tietoa
            // jos on niin lisää affiliaation id:n julkaisuvektoriin julkaisu.

        } else {
            return false;
        }
    }
    return true;
}

std::vector<PublicationID> Datastructures::all_publications()
{
    /*
    std::vector<PublicationID> result;

    for (const auto& pair : publications_map) {
        result.push_back(pair.first);
    }

    return result;
    */

    return all_pub;
}

Name Datastructures::get_publication_name(PublicationID id)
{
    if (publications_map.find(id) != publications_map.end()) {
        return publications_map[id].title;
    }
    return NO_NAME;
}

Year Datastructures::get_publication_year(PublicationID id)
{
    if (publications_map.find(id) != publications_map.end()) {
        return publications_map[id].year;
    }
    return NO_YEAR;
}

std::vector<AffiliationID> Datastructures::get_affiliations(PublicationID id)
{
    std::vector<AffiliationID> result;

    auto it = publications_map.find(id);
    if (it == publications_map.end()) {
        result.push_back(NO_AFFILIATION);
        return result;
    }

    const Publication& publication = it->second;
    for ( const AffiliationID& source : publication.creator) {
        result.push_back(source);
    }
    return result;

}

bool Datastructures::add_reference(PublicationID id, PublicationID parent_id)
{

    auto it = publications_map.find(id);
    auto it2 = publications_map.find(parent_id);

    // tarkistetaan, että molemmat id löytyvät mapista
    // jos ei palautetaan false

    if (it == publications_map.end() || it2 == publications_map.end()) {
        return false;
    }

    // tarkista, ettei viittausta ole jo olemassa
    if (std::find(it2->second.reference.begin(), it2->second.reference.end(), &it->second) != it2->second.reference.end()) {
        // Jos viittaus on jo olemassa, palauta false
        return false;
    }

    // lisää tarkistus, että ei voi lisätä samaa uudelleen.
    // Mikäli kyseinen viittaus löytyy jo niin false

    // viittaus lisätään vektoriin
    it->second.source = &it2->second;
    it2->second.reference.push_back(&it->second);

    return true;

}

std::vector<PublicationID> Datastructures::get_direct_references(PublicationID id)
{
    std::vector<PublicationID> result;

    auto it = publications_map.find(id);
    if (it == publications_map.end()) {
        result.push_back(NO_PUBLICATION);
        return result;
    }

    const Publication& publication = it->second;
    for ( const Publication* reference : publication.reference) {
        result.push_back(reference->ID);
    }
    return result;
}

bool Datastructures::add_affiliation_to_publication(AffiliationID affiliationid, PublicationID publicationid)
{
    // tarkistaa löytyykö julkaisu
    auto iter = publications_map.find(publicationid);
    if (iter == publications_map.end()) {
        return false;
    }

    // tarkistaa löytyykö affiliaatio
    // jos löytyy lisää julkaisun affiliaatioon
    auto it = affiliations_map.find(affiliationid);
    if ( it != affiliations_map.end()) {
        it->second.publicationsID.push_back(publicationid);
    } else {
        return false;
    }

    iter->second.creator.push_back(affiliationid);

    return true;
    // lisää annetulla ID:llä olevan affiliaation julkaisuun ja toisinpäin
}

std::vector<PublicationID> Datastructures::get_publications(AffiliationID id)
{
    std::vector<PublicationID> result;

    auto it = affiliations_map.find(id);
    if (it != affiliations_map.end()) {
        result = it->second.publicationsID;
    } else {
        result.push_back(NO_PUBLICATION);
    }

    return result;
}

PublicationID Datastructures::get_parent(PublicationID id)
{
    auto it = publications_map.find(id);
    if ( it != publications_map.end() && (it->second.source != nullptr)) {
        // ! pois ottaminen muutti tilanteen
        // ylimääräinen lisäys koska ongelma muualla!!
        if ( id == static_cast<PublicationID>(it->second.source->ID)) {
            return NO_PUBLICATION;
        }
        return it->second.source->ID;
    }
    return NO_PUBLICATION;
}

std::vector<std::pair<Year, PublicationID> > Datastructures::get_publications_after(AffiliationID affiliationid, Year year)
{
    auto it = affiliations_map.find(affiliationid);
    if (it == affiliations_map.end()) {
        return {{NO_YEAR, NO_PUBLICATION}};
    }

    std::vector<std::pair<Year, PublicationID>> result;

    const Affiliation& affiliation = it->second;

    for (PublicationID publicationID : affiliation.publicationsID) {
        auto publicationIt = publications_map.find(publicationID);
        if ((publicationIt != publications_map.end()) && publicationIt->second.year >= year) {
            result.push_back({publicationIt->second.year, publicationID});
        }
    }

    std::sort(result.begin(), result.end(),
              [](const auto& a, const auto& b) {
                  return (a.first < b.first) || (a.first == b.first && a.second < b.second);
              });


    return result;
}

std::vector<PublicationID> Datastructures::get_referenced_by_chain(PublicationID id)
{
    auto it = publications_map.find(id);
    if (it == publications_map.end()) {
        return {NO_PUBLICATION};
    }

    std::vector<PublicationID> result;
    Publication* pub = it->second.source;

    // tätä iffiä ei tarvita suoraan tässä
    // mutta kun hyödynnetään tätä closest common parent kohdassa
    // on tämä pakollinen
    if (pub == nullptr) {
        return result;
    }

    while (pub != nullptr) {

        auto pub_iter = publications_map.find(pub->ID);
        if ( pub_iter == publications_map.end()) {
            break;
        } else {
            result.push_back(pub->ID);
            pub = pub->source;
        }
    }
    return result;
}

// alla olevat vielä keskeneräisiä!!! Niillä oma palautus eri kohtaan.
// 27.11. Yksi muistiongelma, funktionaalisesti toimii.

std::vector<PublicationID> Datastructures::get_all_references(PublicationID id)
{

    auto it = publications_map.find(id);
    if (it == publications_map.end()) {
        return {NO_PUBLICATION};
    }

    std::vector<PublicationID> result;
    std::unordered_set<PublicationID> visited;

    std::function<void(PublicationID)> dfs = [&](PublicationID id) {
        visited.insert(id);

        const auto& pub = publications_map[id];

        for (const auto& ref : pub.reference) {
            if ( visited.find(ref->ID) == visited.end()) {
                dfs(ref->ID);
            }
        }
        result.push_back(id);
    };

    dfs(id);
    result.pop_back();

    return result;
}

std::vector<AffiliationID> Datastructures::get_affiliations_closest_to(Coord xy)
{

    // toimii, tästä saa pisteet
    // Lineaarinen kuvassa

    std::vector<std::pair<AffiliationID, std::pair<double, double>>> distances;

    for (const auto& [id, affiliation] : affiliations_map) {

        double distance = std::sqrt(std::pow(xy.x - affiliation.location.x, 2) +
                                    std::pow(xy.y - affiliation.location.y, 2)
                                    );
        distances.emplace_back(id, std::make_pair(distance, affiliation.location.y));

    }

    std::sort(distances.begin(), distances.end(), [](const auto& a, const auto& b) {
        if (a.second.first != b.second.first) {
            return a.second.first < b.second.first;
        } else {
            return a.second.second < b.second.second;
        }
    });

    std::vector<AffiliationID> result;

    for (const auto& pair : distances) {
        result.push_back(pair.first);
        if (result.size() >= 3){
            break;
        }
    }

    return result;
}

bool Datastructures::remove_affiliation(AffiliationID id)
{
    auto it = affiliations_map.find(id);
    if (it == affiliations_map.end()) {
        return false;
        // affiliaatiota ei löydy
    }

    // poistetaan kaikista vektoreista.
    affiliations_map.erase(it);
    aff_alphabetically.erase(std::remove(aff_alphabetically.begin(), aff_alphabetically.end(), id),
                             aff_alphabetically.end());

    aff_dis_inc.erase(std::remove(aff_dis_inc.begin(), aff_dis_inc.end(), id),
                      aff_dis_inc.end());

    // poistetaan kaikkien julkaisuiden tekijöistä
    for (auto& publication_pair : publications_map) {
        auto& pub = publication_pair.second;

        pub.creator.erase(std::remove(pub.creator.begin(), pub.creator.end(), id),
                          pub.creator.end());

        pub.reference.erase(std::remove_if(pub.reference.begin(),
                                           pub.reference.end(),
                                           [id](Publication* ref) {
                                               return ref->creator[0] == id;
                                           }),
                            pub.reference.end());
    }
    return true;
}

PublicationID Datastructures::get_closest_common_parent(PublicationID id1, PublicationID id2)
{
    // nlogn

    auto iter1 = publications_map.find(id1);
    auto iter2 = publications_map.find(id2);

    if (iter1 == publications_map.end() || iter2 == publications_map.end()) {
        return NO_PUBLICATION;
    }

    if(iter1 == publications_map.end() || iter2 == publications_map.end()){
        return NO_PUBLICATION;
    }


    // luodaan käydyt set
    // vektori jossa jo löytyneet vanhemmat ja julkaisut joiden vanhempia etsitään
    std::unordered_set<PublicationID> visited;
    std::vector<PublicationID> returned;
    returned.push_back(id1);
    returned.push_back(id2);

    // käydään while läpi kunnes ei enää löydy vanhempaa
    while (iter1 != publications_map.end()) {
        visited.insert(iter1->first);
        if (iter1->second.source != nullptr) {
            iter1 = publications_map.find(iter1->second.source->ID);
        } else {
            break;
        }
    }

    // sama mutta palauttaa kun löytää yhteisen
    while (iter2 != publications_map.end()) {
        if (visited.find(iter2->first) != visited.end()) {

            auto it = std::find(returned.begin(), returned.end(), iter2->first);
            if (it == returned.end()) {
                returned.push_back(iter2->first);
                return iter2->first;
            }

        }
        if (iter2->second.source != nullptr) {
            iter2 = publications_map.find(iter2->second.source->ID);
        } else {
            break;
        }
    }

    return NO_PUBLICATION;
}


bool Datastructures::remove_publication(PublicationID /*publicationid*/)
{
    // Replace the line below with your implementation
    throw NotImplemented("remove_publication()");
}

std::vector<Connection> Datastructures::get_connected_affiliations(AffiliationID id)
{
    /*
     * Palauttaa yhteydet annetusta alkupisteestä kaikkiin yhdistettyihin loppupisteisiin.
     * Jos yhteyksiä tai annettua alkupistettä ei ole, palautetaan tyhjä vektori.
     * Yhteydet saa palauttaa missä tahansa järjestyksessä.
     * Yksittäiselle yhteydelle (eli Connection-struktille) tulee kuitenkin päteä seuraava:
     * Ensimmäinen affiliaatio (aff1) struktissa on annettu ID,
     * ja toinen affiliaatio (aff2) on tuohon kytketty affiliaatio.
    */


    connection_vec.clear();
    //connection_vec.reserve(get_affiliation_count());

    // tee lisäys, jos connection on jo vectorissa, lisätään connectionin painoa yhdellä

    auto it = affiliations_map.find(id);
    if (it == affiliations_map.end()) {
        // If the given affiliation ID is not found, return an empty vector
        return connection_vec;
    }

    // Iterate through the publications of the given affiliation
    for (PublicationID pubID : it->second.publicationsID) {
        //int paino = 1;  // Reset paino for each publication

        auto pubIt = publications_map.find(pubID);
        if (pubIt != publications_map.end()) {
            // Iterate through the affiliations of the publications
            for (const AffiliationID& affID : pubIt->second.creator) {

                // Ensure the connection is not with the same affiliation
                if (affID != id) {


                    bool pair_found = is_connection_already_in_vector(connection_vec, affID, id);

                    if (pair_found == false) {
                        // lisätään vektoriin luomalla uusi
                        // Yhteyttä ei löytynyt, lisää uusi Connection vektoriin
                        Connection newConnection;
                        newConnection.aff1 = id;
                        newConnection.aff2 = affID;
                        newConnection.weight = 1; // Alusta paino aina yhdellä

                        connection_vec.push_back(newConnection);
                    } else {
                        // kasvatetaan painoa yhdellä
                        for (auto& con : connection_vec) {
                            if ((con.aff1 == id && con.aff2 == affID) || (con.aff1 == affID && con.aff2 == id)) {
                                con.weight++;
                            }
                        }

                    }
                }
            }
        }
    }
    /*
    for (const Connection& conn : connection_vec) {
        std::cout << "Connection Info:" << std::endl;
        std::cout << "Affiliation 1: " << conn.aff1 << std::endl;
        std::cout << "Affiliation 2: " << conn.aff2 << std::endl;
        std::cout << "Weight: " << conn.weight << std::endl;
        std::cout << "---------------------------" << std::endl;
    }
    */
    return connection_vec;
}

namespace std {
template <>
struct hash<std::pair<AffiliationID, AffiliationID>> {
    size_t operator()(const std::pair<AffiliationID, AffiliationID>& p) const {
        // Combine the hash values of the two AffiliationID values
        size_t hash1 = std::hash<AffiliationID>{}(p.first);
        size_t hash2 = std::hash<AffiliationID>{}(p.second);

        // Simple hash combining technique
        return hash1 ^ (hash2 << 1);
    }
};

template <>
struct equal_to<std::pair<AffiliationID, AffiliationID>> {
    bool operator()(const std::pair<AffiliationID, AffiliationID>& lhs, const std::pair<AffiliationID, AffiliationID>& rhs) const {
        // Compare the two AffiliationID pairs for equality
        return lhs.first == rhs.first && lhs.second == rhs.second;
    }
};
}


std::vector<Connection> Datastructures::get_all_connections()
{
    // painot menee väärin
    // painon tulisi nousta jos useita yhteisiä julkaisuja


    all_connections.clear();
    std::unordered_set<std::pair<AffiliationID, AffiliationID>> added_connections;


    for (const auto& id : all_aff) {
        auto it = affiliations_map.find(id);
        if (it == affiliations_map.end()) {
            // If the given affiliation ID is not found, continue to the next one
            continue;
        }

        for (PublicationID pubID : it->second.publicationsID) {
            auto pubIt = publications_map.find(pubID);
            if (pubIt != publications_map.end()) {
                for (const AffiliationID& affID : pubIt->second.creator) {
                    // Ensure the connection is not with the same affiliation
                    if (affID != id) {

                        // Ensure that the connections are stored in a consistent order
                        AffiliationID smallerID = std::min(id, affID);
                        AffiliationID largerID = std::max(id, affID);

                        if (added_connections.find({smallerID, largerID}) != added_connections.end()) {
                                // check if the connetion is found from added_connections
                                // if it does, increment weight
                            for (auto& con : all_connections) {
                                if (con.aff1 == smallerID && con.aff2 == largerID) {
                                    con.weight++;

                                }
                            }

                            // nyt jostain syystä for looppi tekee molemmat! Käy siis kaksi kertaa läpi ja menee if ehtoon kahdesti
                            // johtuu siitä että all_aff
                            // käy läpi TUNI kaikki ja lisää sen
                            // kun menee ISY niin lisää myös TUNI-ISY parin uudestaan

                        } else {
                            Connection newConnection;
                            newConnection.aff1 = smallerID;
                            newConnection.aff2 = largerID;
                            newConnection.weight = 1;

                            all_connections.push_back(newConnection);

                            // Add the connection to the set to avoid duplicates
                            added_connections.insert({smallerID, largerID});
                        }
                    }
                }
            }
        }
    }

    for (Connection& conn : all_connections) {
        /*
        std::cout << "Connection Info:" << std::endl;
        std::cout << "Affiliation 1: " << conn.aff1 << std::endl;
        std::cout << "Affiliation 2: " << conn.aff2 << std::endl;
        std::cout << "Weight: " << conn.weight << std::endl;
        std::cout << "---------------------------" << std::endl;
        */
        conn.weight = conn.weight/2;
    }

    return all_connections;
}


std::vector<Connection> Datastructures::get_any_path(AffiliationID source, AffiliationID target)
{
    /*
     * Palauttaa minkä tahansa polun kahden affiliaation välillä.
     * Polku sisältää affiliaatiot lähtien alkupisteestä ja päättyen loppupisteeseen.
     * Jos alkupistettä, loppupistettä tai niitä yhdistävää polkua ei ole,
     * palautetaan tyhjä vektori.
     * Polku tulee olla kuljettavissa yhteyksiä pitkin
     * (eli yksittäisessä connectionissa aff1 -> aff2)
     */
    // Check if the source and target are the same

    // tääkään ei toimi

    if (source == target) {
        return {};
    }
    std::vector<Connection> path;
    std::unordered_set<AffiliationID> visited;

    // Start the recursive search
    get_any_path_recursive(source, target, visited, path);

    return path;
}

void Datastructures::get_any_path_recursive(AffiliationID current, AffiliationID target, std::unordered_set<AffiliationID> &visited, std::vector<Connection> &path)
{
    // Mark the current affiliation as visited
    visited.insert(current);

    // Get connections from the current affiliation
    std::vector<Connection> connections = get_connected_affiliations(current);

    // Iterate through the connections
    for (const Connection& conn : connections) {
        // Check if the target affiliation is found
        if (conn.aff2 == target) {
            path.push_back(conn);
            return; // Path found, exit the recursive function
        }

        // Check if the next affiliation in the connection has not been visited
        if (visited.find(conn.aff2) == visited.end()) {
            // Recursively search for a path from the next affiliation
            get_any_path_recursive(conn.aff2, target, visited, path);

            // If a path is found, add the current connection to the path
            if (!path.empty()) {
                path.insert(path.begin(), conn);
                return; // Exit the recursive function
            }
        }
    }

}

Path Datastructures::get_path_with_least_affiliations(AffiliationID /*source*/, AffiliationID /*target*/)
{
    // Replace the line below with your implementation
    throw NotImplemented("get_path_with_least_affiliations()");
}

Path Datastructures::get_path_of_least_friction(AffiliationID /*source*/, AffiliationID /*target*/)
{
    // Replace the line below with your implementation
    throw NotImplemented("get_path_of_least_friction()");
}

PathWithDist Datastructures::get_shortest_path(AffiliationID /*source*/, AffiliationID /*target*/)
{
    // Replace the line below with your implementation
    throw NotImplemented("get_shortest_path()");
}

Connection Datastructures::get_direct_connection(AffiliationID source, AffiliationID target)
{

    // Iterate through all connections to find a direct connection
    for (const auto& connection : all_connections) {
        if ((connection.aff1 == source && connection.aff2 == target) ||
            (connection.aff1 == target && connection.aff2 == source)) {
            return connection;
        }
    }

    // If no direct connection is found, return NO_CONNECTION
    return NO_CONNECTION;

}

bool Datastructures::is_connection_already_in_vector(const std::vector<Connection> &connections, AffiliationID affID1, AffiliationID affID2)
{
    for (const Connection& conn : connections) {

        if (conn.aff1 == affID1 && conn.aff2 == affID2) {
                return true;
        }
        if (conn.aff1 == affID2 && conn.aff2 == affID1) {
                return true;
        }
    }

    // Reittiä ei löytynyt
    return false;
}



