# IBM i SSH Tunneling

When communicating with IBM i we use "low" ports (eg. port 23) and using (binding to) ports below 1024 requires root/administrator privileges.

## Windows

Run the program as *administrator* and be sure to allow the program **Private** and **Public** access in the firewall (if enabled).


## MacOS

Run the program with *sudo* from the terminal: ```sudo open /Application/iJump.app```
When running the application for the first time, you are not allowed, as the program is not signed. Go to *Security Settings* and click *Open*.

## Linux

Run the program with *sudo* from the terminal: ```sudo /opt/ijump-x86-64/bin/iJump```