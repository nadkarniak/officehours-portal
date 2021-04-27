import React from 'react'
import {Link} from "react-router-dom";


const Home = () => {
    return (
        <>
            <h1>Home</h1>
            <div className="list-group">
                <Link to="/universities" className="list-group-item">
                    Search Universities Table
                </Link>
            </div>
        </>
    )}


export default Home