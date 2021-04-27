import React from 'react'
import {BrowserRouter, Route, Switch} from "react-router-dom";
import Home from "./components/home"
import UniversityTable from "./components/university-search-table/university-search-table";

function App() {
    return (
        <BrowserRouter>
            <div className="container-fluid">
                <Route path="/" exact={true} component={Home}/>
                <Switch>
                    <Route exact path={["/universities", "/universities/search"]} component={UniversityTable}/>
                </Switch>
            </div>
        </BrowserRouter>
    );
}

export default App;
