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
#include <map>
#include <set>

// Types for IDs
using AffiliationID = std::string;
using PublicationID = unsigned long long int;
using Name = std::string;
using Year = unsigned short int;
using Weight = int;
using Distance = int;

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
inline bool operator!=(Coord c1, Coord c2) { return !(c1 == c2); } // Not strictly necessary

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
    if (c1.y < c2.y)
    {
        return true;
    }
    else if (c2.y < c1.y)
    {
        return false;
    }
    else
    {
        return c1.x < c2.x;
    }
}

// Return value for cases where coordinates were not found
Coord const NO_COORD = {NO_VALUE, NO_VALUE};

// Return value for cases where Distance is unknown
Distance const NO_DISTANCE = NO_VALUE;

// This exception class is there just so that the user interface can notify
// about operations which are not (yet) implemented
class NotImplemented : public std::exception
{
public:
    NotImplemented() : msg_{} {}
    explicit NotImplemented(std::string const &msg) : msg_{msg + " not implemented"} {}

    virtual const char *what() const noexcept override
    {
        return msg_.c_str();
    }

private:
    std::string msg_;
};

// This is the class you are supposed to implement

class Datastructures
{
public:
    Datastructures();
    ~Datastructures();

    // Estimate of performance: O(1)
    // Short rationale for estimate: The time complexity of size()
    // for unordered_map is O(1)
    unsigned int get_affiliation_count();

    // Estimate of performance: O(n)
    // Short rationale for estimate: The time complexity of clear()
    // for unordered_map/map/set is O(n)
    void clear_all();

    // Estimate of performance: O(n)
    // Short rationale for estimate: using for-loop + push_back(O(1))
    // This function is always called n times.
    std::vector<AffiliationID> get_all_affiliations();

    // Estimate of performance: O(n)
    // Short rationale for estimate: The time complexity for searching elements
    // in unordered_map is O(1) in the average case, but O(n) in the worst case.
    bool add_affiliation(AffiliationID id, Name const &name, Coord xy);

    // Estimate of performance: O(n)
    // Short rationale for estimate: The time complexity for searching elements
    // in map is O(1) in the average case, but O(n) in the worst case.
    Name get_affiliation_name(AffiliationID id);

    // Estimate of performance: O(n)
    // Short rationale for estimate: The time complexity for searching elements
    // in map is O(1) in the average case, but O(n) in the worst case.
    Coord get_affiliation_coord(AffiliationID id);

    // We recommend you implement the operations below only after implementing the ones above

    // Estimate of performance: O(n)
    // Short rationale for estimate: using for-loop + push_back(O(1))
    // This function is always called n times.
    // Also, he time complexity of clear() for set is O(n).
    std::vector<AffiliationID> get_affiliations_alphabetically();

    // Estimate of performance: O(n)
    // Short rationale for estimate: using for-loop + push_back(O(1))
    // This function is always called n times.
    // Also, he time complexity of clear() for map is O(n).
    std::vector<AffiliationID> get_affiliations_distance_increasing();

    // Estimate of performance: O(logn)
    // Short rationale for estimate: The time complexity for searching elements
    // in map is O(1ogn)
    AffiliationID find_affiliation_with_coord(Coord xy);

    // Estimate of performance: O(n)
    // Short rationale for estimate: The time complexity for searching elements
    // in unordered_map is O(1) in the average case, but O(n) in the worst case.
    // (The time complexity for removeing elements in map is O(1ogn))
    bool change_affiliation_coord(AffiliationID id, Coord newcoord);

    // We recommend you implement the operations below only after implementing the ones above

    // Estimate of performance: O(n)
    // Short rationale for estimate: using for-loop + push_back(O(1))
    // This function is always called n times.
    bool add_publication(PublicationID id, Name const &name, Year year, const std::vector<AffiliationID> &affiliations);

    // Estimate of performance: O(n)
    // Short rationale for estimate: The time complexity for searching elements
    // in unordered_map is O(1) in the average case, but O(n) in the worst case.
    // (using for-loop + push_back(O(1)) is also O(n))
    std::vector<PublicationID> all_publications();

    // Estimate of performance: O(n)
    // Short rationale for estimate: The time complexity for searching elements
    // in unordered_map is O(1) in the average case, but O(n) in the worst case.
    Name get_publication_name(PublicationID id);

    // Estimate of performance: O(n)
    // Short rationale for estimate: The time complexity for searching elements
    // in unordered_map is O(1) in the average case, but O(n) in the worst case.
    Year get_publication_year(PublicationID id);

    // Estimate of performance: O(n)
    // Short rationale for estimate: The time complexity for searching elements
    // in unordered_map is O(1) in the average case, but O(n) in the worst case.
    std::vector<AffiliationID> get_affiliations(PublicationID id);

    // Estimate of performance: O(n)
    // Short rationale for estimate: The time complexity for searching elements
    // in unordered_map is O(1) in the average case, but O(n) in the worst case.
    bool add_reference(PublicationID id, PublicationID parentid);

    // Estimate of performance: O(n)
    // Short rationale for estimate: The time complexity for searching elements
    // in unordered_map is O(1) in the average case, but O(n) in the worst case.
    std::vector<PublicationID> get_direct_references(PublicationID id);

    // Estimate of performance: O(n)
    // Short rationale for estimate: The time complexity for searching elements
    // in unordered_map is O(1) in the average case, but O(n) in the worst case.
    bool add_affiliation_to_publication(AffiliationID affiliationid, PublicationID publicationid);

    // Estimate of performance: O(n)
    // Short rationale for estimate: The time complexity for searching elements
    // in unordered_map is O(1) in the average case, but O(n) in the worst case.
    std::vector<PublicationID> get_publications(AffiliationID id);

    // Estimate of performance: O(n)
    // Short rationale for estimate: The time complexity for searching elements
    // in unordered_map is O(1) in the average case, but O(n) in the worst case.
    PublicationID get_parent(PublicationID id);

    // Estimate of performance: O(nlogn)
    // Short rationale for estimate: The time complexity for sorting elements
    // in vector is O(nlogn)
    std::vector<std::pair<Year, PublicationID>> get_publications_after(AffiliationID affiliationid, Year year);

    // Estimate of performance: O(n^2)
    // Short rationale for estimate: using while and searching elements in vector
    // Each time complexity is O(n).
    std::vector<PublicationID> get_referenced_by_chain(PublicationID id);

    // Non-compulsory operations

    // Estimate of performance:
    // Short rationale for estimate:
    std::vector<PublicationID> get_all_references(PublicationID id);

    // Estimate of performance:
    // Short rationale for estimate:
    std::vector<AffiliationID> get_affiliations_closest_to(Coord xy);

    // Estimate of performance:
    // Short rationale for estimate:
    bool remove_affiliation(AffiliationID id);

    // Estimate of performance:
    // Short rationale for estimate:
    PublicationID get_closest_common_parent(PublicationID id1, PublicationID id2);

    // Estimate of performance:
    // Short rationale for estimate:
    bool remove_publication(PublicationID publicationid);

private:
    struct Affiliation
    {
        Name _name = NO_NAME;
        Coord _xy = NO_COORD;
        std::vector<PublicationID> _publications = {};
    };

    struct Publication
    {
        Name _name = NO_NAME;
        Year _year = NO_YEAR;
        std::vector<AffiliationID> _affiliations = {};
        std::vector<PublicationID> _references = {};
        PublicationID _parentId = NO_PUBLICATION;
    };

    struct CompareCoords
    {

        int get_squared_distance(const Coord &xy) const
        {
            int x = xy.x;
            int y = xy.y;
            return x * x + y * y;
        }

        bool operator()(const Coord &c1, const Coord &c2) const
        {
            int dist1 = get_squared_distance(c1);
            int dist2 = get_squared_distance(c2);

            if (dist1 < dist2)
            {
                return true;
            }
            else if (dist2 < dist1)
            {
                return false;
            }
            else
            {
                return c1.y < c2.y;
            }
        }
    };
    std::unordered_map<AffiliationID, Affiliation> _affiliations = {};
    std::unordered_map<PublicationID, Publication> _publications = {};
    std::set<std::pair<Name, AffiliationID>> _setAlpha = {};
    std::map<Coord, AffiliationID, CompareCoords> _mapCoord = {};
    bool _alphaChanged = false;
    bool _distChanged = false;
    std::vector<AffiliationID> _affiliationsByAlpha = {};
    std::vector<AffiliationID> _affiliationsByDist = {};
    int NUM_OF_CLOSEST = 3;
};

#endif // DATASTRUCTURES_HH
