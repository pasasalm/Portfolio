#include "datastructures.hh"
#include <random>
#include <cmath>
#include <map>
#include <set>

std::minstd_rand rand_engine; // Reasonably quick pseudo-random generator

template <typename Type>
Type random_in_range(Type start, Type end)
{
    auto range = end - start;
    ++range;

    auto num = std::uniform_int_distribution<unsigned long int>(0, range - 1)(rand_engine);

    return static_cast<Type>(start + num);
}

// Modify the code below to implement the functionality of the class.
// Also remove comments from the parameter names when you implement
// an operation (Commenting out parameter name prevents compiler from
// warning about unused parameters on operations you haven't yet implemented.)

Datastructures::Datastructures()
{
}

Datastructures::~Datastructures()
{
}

unsigned int Datastructures::get_affiliation_count()
{
    return _affiliations.size();
}

void Datastructures::clear_all()
{
    _affiliations.clear();
    _publications.clear();
    _setAlpha.clear();
    _alphaChanged = true;
    _distChanged = true;
    _affiliationsByAlpha.clear();
    _affiliationsByDist.clear();
    _mapCoord.clear();
}

std::vector<AffiliationID> Datastructures::get_all_affiliations()
{
    std::vector<AffiliationID> v = {};
    v.reserve(get_affiliation_count());
    for (const auto &key : _affiliations)
    {
        v.push_back(key.first);
    }
    return v;
}

int get_squared_distance(Coord xy)
{
    int x = xy.x;
    int y = xy.y;
    return x * x + y * y;
}

bool Datastructures::add_affiliation(AffiliationID id, const Name &name, Coord xy)
{
    if (_affiliations.find(id) != _affiliations.end())
    {
        return false;
    }
    _affiliations[id] = {name, xy};
    _setAlpha.insert({name, id});
    _mapCoord[xy] = id;
    _alphaChanged = true;
    _distChanged = true;
    return true;
}

Name Datastructures::get_affiliation_name(AffiliationID id)
{
    if (_affiliations.find(id) == _affiliations.end())
    {
        return NO_NAME;
    }
    return _affiliations[id]._name;
}

Coord Datastructures::get_affiliation_coord(AffiliationID id)
{
    if (_affiliations.find(id) == _affiliations.end())
    {
        return NO_COORD;
    }
    return _affiliations[id]._xy;
}

std::vector<AffiliationID> Datastructures::get_affiliations_alphabetically()
{
    if (_alphaChanged)
    {
        std::vector<AffiliationID> vecId = {};
        vecId.reserve(get_affiliation_count());
        _affiliationsByAlpha.clear();
        for (const auto &element : _setAlpha)
        {
            vecId.push_back(element.second);
        }
        _affiliationsByAlpha = vecId;
        _alphaChanged = false;
    }

    return _affiliationsByAlpha;
}

std::vector<AffiliationID> Datastructures::get_affiliations_distance_increasing()
{
    if (_distChanged)
    {
        std::vector<AffiliationID> vecId = {};
        vecId.reserve(get_affiliation_count());
        _affiliationsByDist.clear();
        for (const auto &element : _mapCoord)
        {
            vecId.push_back(element.second);
        }
        _affiliationsByDist = vecId;
        _distChanged = false;
    }

    return _affiliationsByDist;
}

AffiliationID Datastructures::find_affiliation_with_coord(Coord xy)
{
    if (_mapCoord.find(xy) != _mapCoord.end())
    {
        return _mapCoord[xy];
    }
    else
    {
        return NO_AFFILIATION;
    }
}

bool Datastructures::change_affiliation_coord(AffiliationID id, Coord newcoord)
{
    auto target = _affiliations.find(id);

    if (target == _affiliations.end())
    {
        return false;
    }

    Coord currentCoord = _affiliations[id]._xy;
    _mapCoord.erase(currentCoord);
    _mapCoord[newcoord] = id;
    _distChanged = true;
    _affiliations[id]._xy = newcoord;
    return true;
}

bool Datastructures::add_publication(PublicationID id, const Name &name, Year year,
                                     const std::vector<AffiliationID> &affiliations)
{
    if (_publications.find(id) != _publications.end())
    {
        return false;
    }
    _publications[id] = {name, year, affiliations};

    for (const auto &affiliation : affiliations)
    {
        _affiliations[affiliation]._publications.push_back(id);
    }

    return true;
}

std::vector<PublicationID> Datastructures::all_publications()
{
    std::vector<PublicationID> v;
    v.reserve(_publications.size());
    for (const auto &element : _publications)
    {
        v.push_back(element.first);
    }
    return v;
}

Name Datastructures::get_publication_name(PublicationID id)
{
    if (_publications.find(id) == _publications.end())
    {
        return NO_NAME;
    }
    return _publications[id]._name;
}

Year Datastructures::get_publication_year(PublicationID id)
{
    if (_publications.find(id) == _publications.end())
    {
        return NO_YEAR;
    }
    return _publications[id]._year;
}

std::vector<AffiliationID> Datastructures::get_affiliations(PublicationID id)
{
    if (_publications.find(id) == _publications.end())
    {
        return {NO_AFFILIATION};
    }
    return _publications[id]._affiliations;
}

bool Datastructures::add_reference(PublicationID id, PublicationID parentid)
{
    if (_publications.find(id) == _publications.end() || _publications.find(parentid) == _publications.end())
    {
        return false;
    }
    _publications[parentid]._references.push_back(id);
    _publications[id]._parentId = parentid;
    return true;
}

std::vector<PublicationID> Datastructures::get_direct_references(PublicationID id)
{
    if (_publications.find(id) == _publications.end())
    {
        return {NO_PUBLICATION};
    }
    return _publications[id]._references;
}

bool Datastructures::add_affiliation_to_publication(AffiliationID affiliationid, PublicationID publicationid)
{
    if (_publications.find(publicationid) == _publications.end() || _affiliations.find(affiliationid) == _affiliations.end())
    {
        return false;
    }
    _publications[publicationid]._affiliations.push_back(affiliationid);
    _affiliations[affiliationid]._publications.push_back(publicationid);
    return true;
}

std::vector<PublicationID> Datastructures::get_publications(AffiliationID id)
{
    if (_affiliations.find(id) == _affiliations.end())
    {
        return {NO_PUBLICATION};
    }
    return _affiliations[id]._publications;
}

PublicationID Datastructures::get_parent(PublicationID id)
{
    if (_publications.find(id) == _publications.end())
    {
        return NO_PUBLICATION;
    }
    return _publications[id]._parentId;
}

std::vector<std::pair<Year, PublicationID>>
Datastructures::get_publications_after(AffiliationID affiliationid, Year year)
{
    std::vector<std::pair<Year, PublicationID>> v = {};
    if (_affiliations.find(affiliationid) == _affiliations.end())
    {
        return {{NO_YEAR, NO_PUBLICATION}};
    }

    std::vector<PublicationID> pubIds = _affiliations[affiliationid]._publications;
    v.reserve(pubIds.size());

    for (const PublicationID &id : pubIds)
    {
        if (_publications.at(id)._year >= year)
        {
            v.push_back({_publications.at(id)._year, id});
        }
    }

    std::sort(v.begin(), v.end(), [](const auto &a, const auto &b)
              {
        if (a.first != b.first) {
            return a.first < b.first;
        } else {
            return a.second < b.second;
        } });

    return v;
}

std::vector<PublicationID> Datastructures::get_referenced_by_chain(PublicationID id)
{
    auto it = _publications.find(id);
    if (it == _publications.end())
    {
        return {NO_PUBLICATION};
    }

    std::vector<PublicationID> parentIDs = {};
    PublicationID parentId = it->second._parentId;

    while (parentId != NO_PUBLICATION)
    {
        if (std::find(parentIDs.begin(), parentIDs.end(), parentId) == parentIDs.end())
        {
            parentIDs.push_back(parentId);
        }
        else
        {
            break;
        }

        it = _publications.find(parentId);
        parentId = it->second._parentId;
    }

    return parentIDs;
}

// Non-compulsory operations
std::vector<PublicationID> Datastructures::get_all_references(PublicationID id)
{
    if (_publications.find(id) == _publications.end())
    {
        return {NO_PUBLICATION};
    }
    std::vector<PublicationID> references = _publications[id]._references;
    std::set<PublicationID> setResult(references.begin(), references.end());

    for (const PublicationID ref : references)
    {
        std::vector<PublicationID> indirectRefs = _publications[ref]._references;
        setResult.insert(indirectRefs.begin(), indirectRefs.end());
    }

    std::vector<PublicationID> result(setResult.begin(), setResult.end());
    return result;
}

std::vector<AffiliationID> Datastructures::get_affiliations_closest_to(Coord xy)
{
    std::set<std::pair<int, AffiliationID>> setDist;
    for (const auto &pair : _mapCoord)
    {
        int dx = pair.first.x - xy.x;
        int dy = pair.first.y - xy.y;
        int dist = dx * dx + dy * dy;
        setDist.insert({dist, pair.second});
    }

    std::vector<AffiliationID> v;
    v.reserve(NUM_OF_CLOSEST);
    auto it = setDist.begin();
    for (int i = 0; i < NUM_OF_CLOSEST && it != setDist.end(); ++i, ++it)
    {
        v.push_back(it->second);
    }

    return v;
}

bool Datastructures::remove_affiliation(AffiliationID id)
{
    auto target = _affiliations.find(id);

    if (target == _affiliations.end())
    {
        return false;
    }

    Name targetName = _affiliations[id]._name;
    Coord targetCoord = _affiliations[id]._xy;
    const std::vector<PublicationID> &targetPubs = _affiliations[id]._publications;

    _setAlpha.erase({targetName, id});
    _mapCoord.erase(targetCoord);

    if (!targetPubs.empty())
    {
        for (const PublicationID &pubId : targetPubs)
        {
            auto &affiliations = _publications[pubId]._affiliations;
            affiliations.erase(std::remove(affiliations.begin(), affiliations.end(), id),
                               affiliations.end());
        }
    }

    _alphaChanged = true;
    _distChanged = true;
    _affiliations.erase(target);
    return true;
}

PublicationID Datastructures::get_closest_common_parent(PublicationID id1, PublicationID id2)
{
    std::vector<PublicationID> references1 = get_referenced_by_chain(id1);
    std::vector<PublicationID> references2 = get_referenced_by_chain(id2);
    for (const PublicationID &id : references1)
    {
        auto it = std::find(references2.begin(), references2.end(), id);
        if (it != references2.end())
        {
            return *it;
        }
    }
    return NO_PUBLICATION;
}

bool Datastructures::remove_publication(PublicationID publicationid)
{
    auto target = _publications.find(publicationid);

    if (target == _publications.end())
    {
        return false;
    }

    const std::vector<PublicationID> &targetRefs = _publications[publicationid]._references;
    const std::vector<AffiliationID> &targetAffs = _publications[publicationid]._affiliations;

    if (!targetRefs.empty())
    {
        for (const PublicationID &pubId : targetRefs)
        {
            auto &parent = _publications[pubId]._parentId;
            parent = NO_PUBLICATION;
        }
    }

    if (!targetAffs.empty())
    {
        for (const AffiliationID &affId : targetAffs)
        {
            auto &publications = _affiliations[affId]._publications;
            publications.erase(std::remove(publications.begin(), publications.end(), publicationid),
                               publications.end());
        }
    }

    _publications.erase(target);
    return true;
}
