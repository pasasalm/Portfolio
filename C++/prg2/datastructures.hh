// Datastructures.hh
//
// Student name: Patrik Salmensaari
// Student email: patrik.salmensaari@tuni.fi
// Student number: 150987125

#ifndef DATASTRUCTURES_HH
#define DATASTRUCTURES_HH

#include <string>
#include <vector>
#include <tuple>
#include <utility>
#include <limits>
#include <functional>
#include <exception>
#include <unordered_set>
#include <memory>

// Types for IDs
using AffiliationID = std::string;
using PublicationID = unsigned long long int;
using Name = std::string;
using Year = unsigned short int;
using Weight = int;
struct Connection;
// Type for a distance (in arbitrary units)
using Distance = int;

using Path = std::vector<Connection>;
using PathWithDist = std::vector<std::pair<Connection,Distance>>;

// Return values for cases where required thing was not found
AffiliationID const NO_AFFILIATION = "---";
PublicationID const NO_PUBLICATION = -1;
Name const NO_NAME = "!NO_NAME!";
Year const NO_YEAR = -1;
Weight const NO_WEIGHT = -1;

// Return value for cases where integer values were not found
int const NO_VALUE = std::numeric_limits<int>::min();

// Type for a coordinate (x, y)
struct Coord
{
    int x = NO_VALUE;
    int y = NO_VALUE;
};


// Example: Defining == and hash function for Coord so that it can be used
// as key for std::unordered_map/set, if needed
inline bool operator==(Coord c1, Coord c2) { return c1.x == c2.x && c1.y == c2.y; }
inline bool operator!=(Coord c1, Coord c2) { return !(c1==c2); } // Not strictly necessary

struct CoordHash
{
    std::size_t operator()(Coord xy) const
    {
        auto hasher = std::hash<int>();
        auto xhash = hasher(xy.x);
        auto yhash = hasher(xy.y);
        // Combine hash values (magic!)
        return xhash ^ (yhash + 0x9e3779b9 + (xhash << 6) + (xhash >> 2));
    }
};

// Example: Defining < for Coord so that it can be used
// as key for std::map/set
inline bool operator<(Coord c1, Coord c2)
{
    if (c1.y < c2.y) { return true; }
    else if (c2.y < c1.y) { return false; }
    else { return c1.x < c2.x; }
}

// Return value for cases where coordinates were not found
Coord const NO_COORD = {NO_VALUE, NO_VALUE};

struct Connection
{
    AffiliationID aff1 = NO_AFFILIATION;
    AffiliationID aff2 = NO_AFFILIATION;
    Weight weight = NO_WEIGHT;
    bool operator==(const Connection& c1) const{
        return (aff1==c1.aff1) && (aff2==c1.aff2) && (weight==c1.weight);
    }
};
const Connection NO_CONNECTION{NO_AFFILIATION,NO_AFFILIATION,NO_WEIGHT};


// Return value for cases where Distance is unknown
Distance const NO_DISTANCE = NO_VALUE;

// This exception class is there just so that the user interface can notify
// about operations which are not (yet) implemented
class NotImplemented : public std::exception
{
public:
    NotImplemented() : msg_{} {}
    explicit NotImplemented(std::string const& msg) : msg_{msg + " not implemented"} {}

    virtual const char* what() const noexcept override
    {
        return msg_.c_str();
    }
private:
    std::string msg_;
};

// This is the class you are supposed to implement


struct Publication
{
    int ID;    // Julkaisun uniikki ID
    std::string title;    // Julkaisun otsikko
    int year;    // Julkaisuvuosi

    std::vector<AffiliationID> creator;    // Julkaisin tekijät eli affiliaatiot vektorissa
    std::vector<Publication*> reference;    // Viitattu julkaisu eli ns julkaisun lapset, voi olla useita
    Publication* source = nullptr;          // lähde julkaisu joita voi olla vain yksi!
};


struct Affiliation
{
    std::string ID;    // Affiliaation uniikki ID
    std::string name;    // Affiliaation nimi
    Coord location;    // kordinaatit

    std::vector<PublicationID> publicationsID;  // Affiliaation julkaisut id mukaan vektorissa

    // onko fiksuin lisätä tähän unordered_set, jossa yhteyksien päät.
    // silloin Af olisi alku ja loppupiste
    // yhdessä isossa mapissa alku ja loppupisteet

    // julkaisuilla on connection, jos heillä on yhteinen julkaisu publication!

};

class Datastructures
{
public:
    Datastructures();
    ~Datastructures();


    // Estimate of performance: O(1)
    // Short rationale for estimate: vektorin pituuden palautus on vakio
    // constant operation size
    unsigned int get_affiliation_count();

    // Estimate of performance: O(n)
    // Short rationale for estimate:
    // possible linear, number of element
    // for loop also linear
    void clear_all();

    // Estimate of performance: O(n)
    // Short rationale for estimate: jos for loopilla niin o (n)
    // constant, vektorin palautus
    std::vector<AffiliationID> get_all_affiliations();

    // Estimate of performance: O(n)
    // worst o n, typically 1
    // Short rationale for estimate: finding from map can be linear
    bool add_affiliation(AffiliationID id, Name const& name, Coord xy);

    // Estimate of performance: O(n)
    // Short rationale for estimate: finding from map can be linear
    Name get_affiliation_name(AffiliationID id);

    // Estimate of performance: O(n)
    // Short rationale for estimate: finding from map can be linear
    Coord get_affiliation_coord(AffiliationID id);

    // Estimate of performance: O(1)
    // Short rationale for estimate:
    // solving is constant
    // Vertailee kahden id:n etäisyyksiä
    bool solve_distance(AffiliationID id1, AffiliationID id2);


    // We recommend you implement the operations below only after implementing the ones above

    // Estimate of performance: O(n)
    // Short rationale for estimate:
    // kuvassa lineaarinen O(N)
    // for loop N, sort log eli nlogn
    std::vector<AffiliationID> get_affiliations_alphabetically();

    // Estimate of performance: O(n)
    // Short rationale for estimate:
    // kuvassa lineaarinen
    // for loop N, sort log eli nlogn
    std::vector<AffiliationID> get_affiliations_distance_increasing();

    // Estimate of performance: O(n)
    // Short rationale for estimate: for loop so it se linear
    AffiliationID find_affiliation_with_coord(Coord xy);

    // Estimate of performance: O(n)
    // Short rationale for estimate: find can be N in worst case
    // kuvassa suora, vakio ellei huonoin tapaus
    bool change_affiliation_coord(AffiliationID id, Coord newcoord);


    // We recommend you implement the operations below only after implementing the ones above

    // Estimate of performance: O(n)
    // Short rationale for estimate: for loop so it is linear
    bool add_publication(PublicationID id, Name const& name, Year year, const std::vector<AffiliationID> & affiliations);

    // Estimate of performance: O(n)
    // Short rationale for estimate: for loop, N jos mapin avulla
    // nyt vektorin palautus vakio
    std::vector<PublicationID> all_publications();

    // Estimate of performance: O(n)
    // Short rationale for estimate: find can be linear
    Name get_publication_name(PublicationID id);

    // Estimate of performance: O(n)
    // Short rationale for estimate: find can be linear
    Year get_publication_year(PublicationID id);

    // Estimate of performance: O(n)
    // Short rationale for estimate: for loop, N
    std::vector<AffiliationID> get_affiliations(PublicationID id);

    // Estimate of performance: O(n)
    // Short rationale for estimate: find can be linear
    bool add_reference(PublicationID id, PublicationID parentid);

    // Estimate of performance: O(n)
    // Short rationale for estimate: for loop, N
    std::vector<PublicationID> get_direct_references(PublicationID id);

    // Estimate of performance: O(n)
    // Short rationale for estimate: find can be linear
    bool add_affiliation_to_publication(AffiliationID affiliationid, PublicationID publicationid);

    // Estimate of performance: O(n)
    // Short rationale for estimate: find can be linear
    std::vector<PublicationID> get_publications(AffiliationID id);

    // Estimate of performance: O(n)
    // Short rationale for estimate: find can be linear
    PublicationID get_parent(PublicationID id);

    // Estimate of performance: O(n*logn)
    // Short rationale for estimate:for loop, sort
    std::vector<std::pair<Year, PublicationID>> get_publications_after(AffiliationID affiliationid, Year year);

    // Estimate of performance: O(n)
    // Short rationale for estimate: while loop, N
    // kuvassa log
    std::vector<PublicationID> get_referenced_by_chain(PublicationID id);

    // Estimate of performance: O(n)
    // Short rationale for estimate: for loop, recursion
    std::vector<PublicationID> get_all_references(PublicationID id);

    // Estimate of performance: O(n)
    // Short rationale for estimate: for loop, sort
    // kuvassa lineaarinen
    std::vector<AffiliationID> get_affiliations_closest_to(Coord xy);

    // Estimate of performance: O(n*logn)
    // Short rationale for estimate: remove ja for looppi
    bool remove_affiliation(AffiliationID id);

    // Estimate of performance: O(n*logn)
    // Short rationale for estimate: kaksi while looppia eli n*m
    // kuvassa n log n
    PublicationID get_closest_common_parent(PublicationID id1, PublicationID id2);

    // Estimate of performance:
    // Short rationale for estimate:
    bool remove_publication(PublicationID publicationid);

    // PRG 2 functions:

    // Estimate of performance:
    // Short rationale for estimate:
    std::vector<Connection> get_connected_affiliations(AffiliationID id);

    // Estimate of performance:
    // Short rationale for estimate:
    std::vector<Connection> get_all_connections();

    // Estimate of performance:
    // Short rationale for estimate:
    std::vector<Connection> get_any_path(AffiliationID source, AffiliationID target);

    void get_any_path_recursive(AffiliationID current, AffiliationID target, std::unordered_set<AffiliationID>& visited, std::vector<Connection>& path);

    // PRG2 optional functions

    // Estimate of performance:
    // Short rationale for estimate:
    Path get_path_with_least_affiliations(AffiliationID source, AffiliationID target);

    // Estimate of performance:
    // Short rationale for estimate:
    Path get_path_of_least_friction(AffiliationID source, AffiliationID target);

    // Estimate of performance:
    // Short rationale for estimate:
    PathWithDist get_shortest_path(AffiliationID source, AffiliationID target);

    Connection get_direct_connection(AffiliationID source, AffiliationID target);

    bool is_connection_already_in_vector(const std::vector<Connection>& connections, AffiliationID affID1, AffiliationID affID2);


private:


    std::unordered_map<AffiliationID, Affiliation> affiliations_map;  // mapissa ID, Af parina.
    std::unordered_map<PublicationID, Publication> publications_map;  // mapissa ID, Pub parina.

    // vektorissa affiliaatiot
    // aakkosjärjestyksessä, etäisyyden mukaan, kaikki julkaisut ja kaikki affiliaatiot
    std::vector<AffiliationID> aff_alphabetically;
    std::vector<AffiliationID> aff_dis_inc;
    std::vector<PublicationID> all_pub;
    std::vector<AffiliationID> all_aff;


    // tästä alaspäin prog2 lisäykset

    std::vector<Connection> connection_vec;
    std::vector<Connection> all_connections;
    bool is_all_connections_vec_correct = false;
    // jos lisätään connectioni, tulee tästä tehdä false;

    //std::vector<std::pair<Connection,Distance>> PathWithDist;

};

#endif // DATASTRUCTURES_HH
