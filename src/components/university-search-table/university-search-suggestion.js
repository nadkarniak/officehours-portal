import React from 'react'

const Suggestions = (props) => {
    return (
    props.results.map((university) => (
        <ul>
            <li onClick={() => props.handler(university.name, university.country)}>
                {university.name}
            </li>
        </ul>
    ))
    //return <ul onClick = {() => console.log({options})}>{options}</ul>
)}

export default Suggestions