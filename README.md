## HTTP Server
We have developed a web server that can handle multiple incoming HTTP GET and POST requests and send HTTP response message back to the client, with appropriate Content-type in the response (according to the standard http protocol).

Features:
1. Listen on a specified port, other than the standard port 80, and accept HTTP requests.
2. Handle each HTTP request in a separate thread.
3. Handle HTTP version 1.0 GET and POST requests.
4. Extract filename from HTTP request and return the file or an appropriate error message
(e.g. if we type “localhost:8080/index.html” in the address bar of a browser, then we will
get the index.html page if found. If not the browser will show a 404 Not Found Message).
5. Return a HTTP response message and close the client socket connection.
6. Return appropriate status code, e.g. 200 (OK) or 404 (NOT FOUND), in the response.
7. Determine and send the correct MIME type specifier in the response message.
8. Able to generate an appropriate log file for the corresponding http request.
