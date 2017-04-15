# Micro HTTP Server for Android

NOTE: This is an incomplete library. Use as an example or maybe a starting point.

This is a micro http server for Android (also works with plain Java 1.7+). I initially wrote this as a proof-of-concept to develop a small/lightweight http server library for Android for a project that I was working on. I've sinced developed a different version for the project and unfortunately this version went unmaintained and remains incomplete. This can possibly be a starting point for finishing it out yourself, or an example of how to do socket programming in Java. This incomplete library is pretty crude, it will need a lot of work to make it a full fledged web server.

The server and client handler runs in a single thread. If you want to make this even remotely usage as a web server, you'll need to handle each client in a separate thread. You could modify the client handler to implement Runnable and then use a thread pool.
