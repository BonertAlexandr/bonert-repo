#GREP
Analogue grep. It filters input text stream and returns strings which are contain words that are passed in arguments.
* program case insensitive
* colde be more than one arguments 
* regular expression could be passed as a argument

#SORT
Sorts input text stream by alphabet. Depending on keys argument it can:
* ignore case
* sort by character amount in line
* sort by index of the word in line

#CHAT
Users send messages by Message class which contains information about specific user(username, time and date of message, ip) on server.
Server receives messages from user and send to another users. If user enter/exit chat, server will send message about it to all users.

conf.xml file stores port value and the amount of recent messages in the message history.
