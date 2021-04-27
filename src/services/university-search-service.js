const UNIVERSITY_URL = "http://universities.hipolabs.com/search?";

export const findAllUniversitiesByName = (name, country) =>
    fetch(`${UNIVERSITY_URL}name=${name}&country=${country}`)
        .then(response => response.json())

export const findAllUniversitiesByOnlyName = (name) =>
    fetch(`${UNIVERSITY_URL}name=${name}`)
        .then(response => response.json())

export default {
    findAllUniversitiesByName,
    findAllUniversitiesByOnlyName
}