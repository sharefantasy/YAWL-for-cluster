# YAWL for cluster

Author: Fan Kai Nang(范啟能)  
Lab: Worflow lab, SYSU  

## Idea
　　'YAWL for cluster' is an experimental project on making YAWL, an traditional workflow engine, into a distributed engine with minimal modifications.  

## Method
　　We extend the engine with an interface, which we called interface C. Like other interfaces, interface C exposes limited but enough engine infomation and operations, so that an external management application can access and control the engine.  
　　The management application 'cluster_client' manages a set of workflow engines, provides workitem scheudling and HA services.  

## Features
　　1. Heartbeat.  
　　　　Checks if the engines are still available.  
　　2. Workitem scheduling  
　　　　Switch active workitems to an available engine when the old engine is down.  
　　3. Load balance
　　　　Monitor the distribution of engines on hosts, adjust in reference of allocating speed and historical allocating speed.  
　　4. OpenStack adapter

## Future
　　1. Session management (single sign on)  
　　2. Backup cluster management  
　　3. Security