University Domains and Names API
=================================


### An API and JSON list contains domains, names and countries of most of the universities of the world.


Provides a search endpoint you can search for an autocomplete for university name or/and filter by country.



## HTTP GET REQUESTS FROM THE API
-----------------
    
    http://universities.hipolabs.com/search?name={query}
    
    http://universities.hipolabs.com/search?country={query}
    
    http://universities.hipolabs.com/search?name={query}&country={query1}
    
    

## API Search Endpoint

### Sample Request
    /search?name=Middle


### Response
    [
    {
    web_page: "http://www.meu.edu.jo/",
    country: "Jordan",
    domain: "meu.edu.jo",
    name: "Middle East University"
    },
    {
    web_page: "http://www.odtu.edu.tr/",
    country: "Turkey",
    domain: "odtu.edu.tr",
    name: "Middle East Technical University"
    },
    {
    web_page: "http://www.mtsu.edu/",
    country: "USA",
    domain: "mtsu.edu",
    name: "Middle Tennessee State University"
    },
    {
    web_page: "http://www.mga.edu/",
    country: "USA",
    domain: "mga.edu",
    name: "Middle Georgia State College"
    },
    {
    web_page: "http://www.mdx.ac.uk/",
    country: "United Kingdom",
    domain: "mdx.ac.uk",
    name: "Middlesex University"
    },
    {
    web_page: "http://www.middlebury.edu/",
    country: "USA",
    domain: "middlebury.edu",
    name: "Middlebury College"
    }
    ]

### Sample Request
    /search?name=Middle&country=Turkey


### Response
    [
    {
    web_page: "http://www.odtu.edu.tr/",
    country: "Turkey",
    domain: "odtu.edu.tr",
    name: "Middle East Technical University"
    }
    ]

## API Update Endpoint
If the university dataset changes, the API won't automatically update it. Use this endpoint to force a refresh.

### Request
    /update

### Response
    {
        'status': str,
        'message': str
    }



## Contribution
Please contribute to this list! We need your support to keep this list up-to-date.
Do not hesitate to fix any wrong data. It is extremely easy. Just open a PR, or create an issue. 


### Created and maintained by [Hipo](http://www.hipolabs.com)
