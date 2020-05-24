# websockets
	- Who subscribes to the event and 
	- Number of sessions which are opne
	- Send message to specific session id

- Notify healthCheck to other services who have subsribed to same topic
- If there are 3 services subscribed to a topic, we will have to be able to send message from one service and consume that message from another service
- From UI perspective, if three different users are subrscribed, client 1 must be able to send message and client 2 must be able to consume it
- When service A recieves any request, it send a message to the topic, which is then read by all other services
