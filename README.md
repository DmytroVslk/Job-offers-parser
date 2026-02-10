Java Job Aggregator

The project aggregates Java developer vacancies from the public The Muse and displays them in a clean web interface. It includes a built‑in HTTP server that serves the UI and an API endpoint for search. The architecture follows MVC with a Strategy pattern for data sources.

Features
1. Job search by location (and level selection).
2. Results shown in a web table.
3. Support for multiple cities.
4. Clear separation of data fetching, controller logic, and view.

How it works
1. The user selects search parameters in the web UI.  
2. The server queries The Muse API.  
3. Data is processed and returned as JSON.  
4. Results are rendered in the table on the page.  

Main modules
1. Server — runs the HTTP server and search API.
2. Model/Provider/Strategy — fetching and aggregating jobs.
3. View — HTML interface and rendering results.
4. VO (JobPosting)** — job data structure.
