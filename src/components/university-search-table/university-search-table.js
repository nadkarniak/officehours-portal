import React from 'react'
import UniversityRow from "./university-search-row";
import univService from "../../services/university-search-service"
import Suggestions from "./university-search-suggestion"
import "./university-search-table.css"
import queryString from "query-string"
let value="";
export default class UniversityTable
    extends React.Component {

    constructor(props) {
        super(props)
        //console.log(props)
        this.state = {
            univName: "",
            country: "",
            results: [],
            universities: []
        }
        value=queryString.parse(this.props.location.search);
        console.log(value)
    }

    renderSearchData = (name, country) => {
        this.setState({
            univName: name,
            country: country
        })
    }

    searchUniversityByOnlyName = (name) => {
        univService.findAllUniversitiesByOnlyName(name)
            .then(results => this.setState(
                (prevState) => ({
                    ...prevState,
                    results: results
                })))
        //console.log(this.state.results)
    }

    setInputs = (name, country) => {
        this.setState({
            univName: name,
            country: country
        })
        console.log(this.state.univName)
    }

    handleChange = (event) => {
        //event.preventDefault();
        this.setState({ [event.target.name]: event.target.value,
        },() => {
            //console.log(this.state.univName)
            if (this.state.univName && this.state.univName.length > 1) {
                //console.log(this.state.univName)
                this.searchUniversityByOnlyName(this.state.univName)
            } else if (!this.state.univName) {
            }
        });
    }

    searchUniversity = (event) => {
        //console.log(this.state.univName)
        //console.log(this.state.country)
        let name = this.state.univName
        let country = this.state.country
        if (name === "") alert("Please enter the University Name")
        else if(country === "") alert("Please enter the country")
        else {
            univService.findAllUniversitiesByName(name, country)
                .then(universities => this.setState(
                    (prevState) => ({
                        ...prevState,
                        universities: universities
                    })))
            //console.log(this.state.universities)
        }
    }

    render() {
        if (value.name === undefined || value.country === undefined) {
            return (
                <div>
                    <div className="row">
                        <div className="col-5">
                            <input className="form-control" onChange={this.handleChange}
                                   placeholder="Search University with name" name="univName"
                                   value={this.state.univName}/>
                            <Suggestions results={this.state.results} handler={this.setInputs}/>
                        </div>
                        <div className="col-5">
                            <input className="form-control" onChange={this.handleChange}
                                   placeholder="Enter Country" name="country" value={this.state.country}/>
                        </div>
                        <div className="col-2">
                            <i onClick={this.searchUniversity} className="fa fa-search fa-2x"></i>
                        </div>
                    </div>
                    <table className="table">
                        <tbody>
                        <tr>
                            <td>University Name</td>
                            <td className="d-none d-sm-table-cell">Country</td>
                            <td className="d-none d-md-table-cell">Country Code</td>
                        </tr>
                        {
                            this.state.universities.map((university, ndx) =>
                                <UniversityRow
                                    key={ndx}
                                    university={university}
                                    name={university.name}
                                    country={university.country}
                                />)
                        }
                        </tbody>
                    </table>
                </div>
            )
        }
        else {
            univService.findAllUniversitiesByName(value.name, value.country)
                .then(universities => this.setState(
                    (prevState) => ({
                        ...prevState,
                        universities: universities
                    })))
            return (
                <div>
                    <table className="table">
                        <tbody>
                        <tr>
                            <td>University Name</td>
                            <td className="d-none d-sm-table-cell">Country</td>
                            <td className="d-none d-md-table-cell">Country Code</td>
                        </tr>
                        {
                            this.state.universities.map((university, ndx) =>
                                <UniversityRow
                                    key={ndx}
                                    university={university}
                                    name={university.name}
                                    country={university.country}
                                />)
                        }
                        </tbody>
                    </table>
                </div>
            )
        }
    }
}