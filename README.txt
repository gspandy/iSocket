iSocket v0.9
Copyright (c) 2011 yangjun
(yangjun1120 _at_ gmail.com)
======================================================================

This software is released under an  Apache License   Version 2.0 (please see the
License file). 

This software is a custom socket server framework, depends on the Grizzly (v 2.1.1). 

To build, you can either run "ant build.xml" on Unix or ant from the top
directory. 

To run an example, 
 server:
  java -cp ./classes:$CLASSPATH org.young.isokcet.server.NIOSocketServer start
  java -cp ./classes:$CLASSPATH org.young.isokcet.server.NIOSocketServer stop
  
  client:
  java -cp ./classes:$CLASSPATH org.young.isokcet.client.NIOSocketClient

Please send comments, queries, code fixes, constructive criticisms to 
(yangjun1120 _at_ gmail.com)