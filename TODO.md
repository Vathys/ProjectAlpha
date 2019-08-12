# To-Do List
- [x] Make a client
  * [x] Set up sockets
  * [x] Test connections with server
- [x] Start Handling connections in Client
- [x] Develop Cuncurrent systems 
- [x] Develop a Protocol for server/client communication
- [x] GUI
  * [x] Use Document Listener to listen to documents and send changes to client; changes will then go to server
- [ ] Add feature to allow opening multiple documents in different windows
- [ ] Save and Open Documents
  * [ ] Add project feature in server
  * [ ] Allow clients to open files only in project folder
  * Allow clients to sync on different documents
  * Server machine is where the documents are saved
  * When server is opened, user will have to pick a folder that contains a .project file, which will contain information on location of bin and src, and location of libraries.
  * Only folders that contain a .project file can be opened
  * When clients are first connected to the server, the server will send about the project file to the client. The client creates a temporary file in the machine that can also be used to only keep the client changes in the document
- [ ] Add compile and/or run options in Clients
  * [ ] Open console in all client machine when run
  * Would it be easier to signal all machine's to run OR send run results of one machine to all others?

# Current Protocol
{EventType Offset Length StringValue}

The whole message (0)

EventType -> [+ (EventType.INSERT) OR - (EventType.REMOVE)] (1)

Offset -> [off, #] (2)

Length -> [len, #] (3)

StringValue -> "val" (4)
## Example:
{[+][off12][len1]"d"}

EventType -> [+] -> EventType.INSERT

Offset -> [off12] -> offset = 12

Length -> [len1] -> length = 1

val -> "d"

(In case of a newLine, val = "newLine" but length = 1)

# Websites to Checkout
Solution for Multiple Carets: https://stackoverflow.com/questions/33611407/how-to-show-multiple-carets-in-a-jtextarea-or-jtextpane

